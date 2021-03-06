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

import com.google.common.collect.ImmutableMap;
import net.cpollet.covid19.statsloader.data.DataPoint;
import net.cpollet.covid19.statsloader.domain.Switzerland;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class H2Row {
    private final LocalDate date;
    private final Switzerland.CantonCode canton;
    private final List<H2Field> fields;
    private final boolean last;

    public H2Row(LocalDate date, Switzerland.CantonCode canton, List<H2Field> fields, boolean last) {
        this.date = date;
        this.canton = canton;
        this.fields = fields;
        this.last = last;
    }

    public H2Row(LocalDate date, Switzerland.CantonCode canton, List<H2Field> fields) {
        this(date, canton, fields, false);
    }

    public H2Row(LocalDate date, Switzerland.CantonCode canton, H2Field field) {
        this(date, canton, Collections.singletonList(field), false);
    }

    public H2Row(LocalDate date, Switzerland.CantonCode canton, H2Field fields, boolean last) {
        this(date, canton, Collections.singletonList(fields), last);
    }

    public DataPoint toPoint(String measure) {
        return new DataPoint(
                date.atTime(23, 59, 59),
                measure,
                ImmutableMap.of(
                        "dayOfWeek", dayOfWeek(),
                        "canton", canton.name(),
                        "last", Boolean.toString(last)
                ),
                fields.stream().collect(Collectors.toMap(H2Field::getMeasure, H2Field::getValue))
        );

    }

    private String dayOfWeek() {
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }
}
