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

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@Getter
public class Covid19ReRecord {
    private final LocalDate date;
    private final String canton;
    private final float medianRMean;
    private final float medianRHigh;
    private final float medianRLow;

    public Covid19ReRecord(String row) {
        String[] columns = row.split(",");
        this.date = LocalDate.parse(columns[5], DateTimeFormatter.ISO_LOCAL_DATE);
        this.canton = columns[1];
        this.medianRMean = Float.parseFloat(columns[6]);
        this.medianRHigh = Float.parseFloat(columns[7]);
        this.medianRLow = Float.parseFloat(columns[8]);

    }

    public long getTimestamp() {
        LocalDateTime dateTime = LocalDateTime.of(
                getDate(),
                LocalTime.of(23, 59, 59)
        );
        return dateTime.toEpochSecond(ZoneId.of("Europe/Zurich").getRules().getOffset(dateTime));
    }

    public String getDayOfWeek() {
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }
}
