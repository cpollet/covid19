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
package net.cpollet.covid19.statsloader.data.timoll;

import lombok.Getter;
import net.cpollet.covid19.statsloader.domain.Switzerland;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class TmRoot {
    private final List<TmRecord> records;

    public TmRoot(String data) {
        List<String> rows = Arrays.asList(data.split("\n"));

        Map<Integer, Switzerland.CantonCode> cantons = mapColumnToCanton(rows.get(0).split(","));

        this.records = rows.stream().skip(1)
                .map(r -> toRecords(r, cantons))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<TmRecord> toRecords(String row, Map<Integer, Switzerland.CantonCode> cantons) {
        List<TmRecord> tmRecords = new ArrayList<>();

        List<String> columns = Arrays.asList(row.split(","));
        LocalDate date = LocalDate.parse(columns.get(0), DateTimeFormatter.ISO_LOCAL_DATE);

        for (int i = 1; i < columns.size(); i++) {
            tmRecords.add(new TmRecord(date, cantons.get(i), "cases", (int) Float.parseFloat(columns.get(i))));
        }

        return tmRecords;
    }

    private Map<Integer, Switzerland.CantonCode> mapColumnToCanton(String[] columns) {
        Map<Integer, Switzerland.CantonCode> columnToCanton = new HashMap<>();
        for (int i = 1; i < columns.length; i++) {
            try {
                columnToCanton.put(i, Switzerland.CantonCode.valueOf(columns[i]));
            }
            catch (IllegalArgumentException e) {
                // skip...
            }
        }
        return columnToCanton;
    }
}
