/*
 * Copyright 2020 Christophe Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cpollet.covid19.statsloader.data.foph;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FophDataSupplier implements Supplier<FophRoot> {
    private static final String URL = "https://www.bag.admin.ch/dam/bag/fr/dokumente/mt/k-und-i/" +
            "aktuelle-ausbrueche-pandemien/2019-nCoV/covid-19-basisdaten-labortests.xlsx.download.xlsx/" +
            "Dashboard_3_COVID19_labtests_positivity.xlsx";

    @Override
    public FophRoot get() {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget webTarget = client.target(URL);
        InputStream inputStream = webTarget.request().get().readEntity(InputStream.class);

        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);

            return new FophRoot(
                    StreamSupport.stream(sheet.spliterator(), false)
                            .skip(1) // skip the reader row
                            .map(DeserializedRow::new)
                            .collect(Collectors.groupingBy(DeserializedRow::getDate))
                            .entrySet().parallelStream()
                            .map(e -> new FophRecord(
                                    e.getKey(),
                                    e.getValue().stream().mapToInt(DeserializedRow::getPositive).sum(),
                                    e.getValue().stream().mapToInt(DeserializedRow::getNegative).sum()
                            ))
                            .collect(Collectors.toList())
            );
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static class DeserializedRow {
        private static final int COL_DATE = 1;
        private static final int COL_KIND = 3;
        private static final int COL_COUNT = 2;
        private final Row row;

        public DeserializedRow(Row row) {
            this.row = row;
        }

        public LocalDate getDate() {
            return row.getCell(COL_DATE).getLocalDateTimeCellValue().toLocalDate();
        }

        public int getPositive() {
            if (getKind() == Kind.NEGATIVE) {
                return 0;
            }
            return getCount();
        }

        public int getNegative() {
            if (getKind() == Kind.POSITIVE) {
                return 0;
            }
            return getCount();
        }

        private int getCount() {
            return (int) row.getCell(COL_COUNT).getNumericCellValue();
        }

        private Kind getKind() {
            return Kind.from(row.getCell(COL_KIND).getStringCellValue());
        }

        private enum Kind {
            POSITIVE("Positive"),
            NEGATIVE("Negative");

            private final String label;

            Kind(String label) {
                this.label = label;
            }

            public static Kind from(String label) {
                return Arrays.stream(values())
                        .filter(k -> k.label.equals(label))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException(label + " is not a valid label value"));
            }
        }
    }
}
