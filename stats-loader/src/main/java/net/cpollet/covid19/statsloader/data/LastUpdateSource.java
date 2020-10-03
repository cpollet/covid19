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
package net.cpollet.covid19.statsloader.data;

import net.cpollet.covid19.statsloader.BuildPropertiesFactory;
import org.influxdb.dto.Point;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class LastUpdateSource implements Source<Point> {
    @Override
    public Stream<Point> stream() {
        return Stream.of(
                Point.measurement("LastUpdate")
                        .addField("value", now())
                        .addField("commit", BuildPropertiesFactory.properties().getProperty("build.commit", "-").substring(0, 7))
                        .build()
        );
    }

    @NotNull
    private String now() {
        return Instant.now()
                .atZone(ZoneId.of("Europe/Zurich"))
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm"));
    }
}
