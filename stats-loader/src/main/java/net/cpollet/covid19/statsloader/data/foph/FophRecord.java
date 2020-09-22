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

import com.google.common.collect.ImmutableMap;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.cpollet.covid19.statsloader.data.DataPoint;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;

@RequiredArgsConstructor
public class FophRecord {
    private final LocalDate date;
    private final int positive;
    private final int negative;

    public LocalDate getDate() {
        return date;
    }

    public DataPoint toPoint() {
        return new DataPoint(
                date.atTime(23, 59, 59),
                "foph.Tests",
                ImmutableMap.of(
                        "dayOfWeek", dayOfWeek()
                ),
                ImmutableMap.of(
                        "negative", (double) negative,
                        "positive", (double) positive,
                        "total", (double) negative + positive,
                        "posRatio", (double) positive / (negative + positive)
                )
        );
    }

    private String dayOfWeek() {
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }
}
