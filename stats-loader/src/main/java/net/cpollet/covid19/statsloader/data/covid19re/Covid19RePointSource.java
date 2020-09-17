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

import net.cpollet.covid19.statsloader.data.Source;
import net.cpollet.covid19.statsloader.data.foph.FophRecord;
import org.influxdb.dto.Point;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

public class Covid19RePointSource implements Source<Point> {
    private static final List<Function<Covid19ReRecord, Optional<Point>>> collectors = Arrays.asList(
            r -> Optional.of(Point.measurement("covid19re.Re")
                    .time(r.getTimestamp(), TimeUnit.SECONDS)
                    .tag("canton", r.getCanton())
                    .tag("dayOfWeek", r.getDayOfWeek())
                    .addField("re_median", r.getMedianRMean())
                    .addField("re_low", r.getMedianRLow())
                    .addField("re_high", r.getMedianRHigh())
                    .build())
    );

    private final Covid19ReDataSupplier supplier;

    public Covid19RePointSource(Covid19ReDataSupplier supplier) {
        this.supplier = supplier;
    }

    @Override
    public Stream<Point> stream() {
        return supplier.get().getRecords().stream()
                .flatMap(
                        r -> collectors.stream()
                                .map(c -> c.apply(r))
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                );
    }
}
