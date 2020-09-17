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
package net.cpollet.covid19.statsloader.data.h2;

import lombok.AllArgsConstructor;
import net.cpollet.covid19.statsloader.domain.Switzerland;
import org.influxdb.dto.Point;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class H2Row {
    private final LocalDate date;
    private final Switzerland.CantonCode canton;
    private final List<H2Field> fields;

    public H2Row(LocalDate date, Switzerland.CantonCode canton, H2Field field) {
        this(date, canton, Collections.singletonList(field));
    }

    public Point toPoint(String measure) {
        Point.Builder pointBuilder = Point.measurement(measure)
                .time(timestamp(), TimeUnit.SECONDS)
                .tag("dayOfWeek", dayOfWeek())
                .tag("canton", canton.name());

        fields.forEach(f -> pointBuilder.addField(f.getMeasure(), f.getValue()));

        return pointBuilder.build();
    }

    private long timestamp() {
        LocalDateTime dateTime = LocalDateTime.of(
                date,
                LocalTime.of(12, 0, 0)
        );
        return dateTime.toEpochSecond(ZoneId.of("Europe/Zurich").getRules().getOffset(dateTime));
    }

    private String dayOfWeek() {
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }
}
