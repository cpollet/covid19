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

import net.cpollet.covid19.statsloader.data.Source;
import org.influxdb.dto.Point;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FophPointSource implements Source<Point> {
    private static final List<Function<FophRecord, Optional<Point>>> collectors = Collections.singletonList(
            r -> Optional.of(Point.measurement("Tests")
                    .time(r.getTimestamp(), TimeUnit.SECONDS)
                    .tag("dayOfWeek", r.getDayOfWeek())
                    .addField("negative", r.getNegative())
                    .addField("positive", r.getPositive())
                    .addField("total", r.getNegative() + r.getPositive())
                    .addField("posRatio", (double) r.getPositive() / (r.getNegative() + r.getPositive()))
                    .build())
    );

    private final FophDataSupplier supplier;

    public FophPointSource(FophDataSupplier supplier) {
        this.supplier = supplier;
    }

    @Override
    public Stream<Point> stream() {
        List<FophRecord> records = supplier.get().getRecords();

        verifyNoDuplicateDay(records);

        return records.stream()
                .flatMap(
                        r -> collectors.stream()
                                .map(c -> c.apply(r))
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                );
    }

    private void verifyNoDuplicateDay(List<FophRecord> records) {
        boolean foundDuplicateDay = records.stream()
                .collect(Collectors.groupingBy(FophRecord::getDate))
                .entrySet().stream()
                .anyMatch(e -> e.getValue().size() > 1);

        if (foundDuplicateDay) {
            throw new IllegalArgumentException("Found a duplicate date");
        }
    }
}
