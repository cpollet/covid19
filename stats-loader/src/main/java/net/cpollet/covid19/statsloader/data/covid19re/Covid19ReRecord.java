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
package net.cpollet.covid19.statsloader.data.covid19re;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.cpollet.covid19.statsloader.data.DataPoint;
import net.cpollet.covid19.statsloader.domain.Switzerland;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;

@Getter
public class Covid19ReRecord {
    private final LocalDate date;
    private final Switzerland.CantonCode canton;
    private final double medianRMean;
    private final double medianRHigh;
    private final double medianRLow;

    private Covid19ReRecord(LocalDate date, Switzerland.CantonCode canton, double medianRMean, double medianRHigh, double medianRLow) {
        this.date = date;
        this.canton = canton;
        this.medianRMean = medianRMean;
        this.medianRHigh = medianRHigh;
        this.medianRLow = medianRLow;
    }

    public static Optional<Covid19ReRecord> fromString(String row) {
        String[] columns = row.trim().split(",");

        if (columns[1].equals("CHE")) {
            columns[1] = "CH";
        }
        if (!Switzerland.CantonCode.isValid(columns[1])) {
            return Optional.empty();
        }

        return Optional.of(
                new Covid19ReRecord(
                        LocalDate.parse(columns[5], DateTimeFormatter.ISO_LOCAL_DATE),
                        Switzerland.CantonCode.valueOf(columns[1]),
                        Float.parseFloat(columns[6]),
                        Float.parseFloat(columns[7]),
                        Float.parseFloat(columns[8])
                )
        );
    }

    public DataPoint toPoint() {
        return new DataPoint(
                date.atTime(23, 59, 59),
                "covid19re.Re",
                ImmutableMap.of(
                        "dayOfWeek", dayOfWeek(),
                        "canton", canton.name()
                ),
                ImmutableMap.of(
                        "re_median", medianRMean,
                        "re_low", medianRLow,
                        "re_high", medianRHigh
                )
        );
    }

    private String dayOfWeek() {
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }
}
