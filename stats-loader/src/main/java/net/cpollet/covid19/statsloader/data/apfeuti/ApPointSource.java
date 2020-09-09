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
package net.cpollet.covid19.statsloader.data.apfeuti;

import net.cpollet.covid19.statsloader.data.Source;
import net.cpollet.covid19.statsloader.utils.PairingCollector;
import org.influxdb.dto.Point;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApPointSource implements Source<Point> {
    private static final List<Function<PairingCollector.Pair<ApRecord>, Optional<Point>>> collectors = Arrays.asList(
            p -> pointIf(
                    p,
                    pair -> pair.getCurrent().getCumulatedTested() != null,
                    pair -> Point.measurement("Tested")
                            .time(p.getCurrent().getTimestamp(), TimeUnit.SECONDS)
                            .tag("canton", p.getCurrent().getPlace())
                            .tag("dayOfWeek", p.getCurrent().getDayOfWeek())
                            .addField("new", delta(p, ApRecord::getCumulatedTestedForward))
                            .addField("cumulated", p.getCurrent().getCumulatedConfirmedForward())
                            .build()
            ),
            p -> Optional.of(Point.measurement("Confirmed")
                    .time(p.getCurrent().getTimestamp(), TimeUnit.SECONDS)
                    .tag("canton", p.getCurrent().getPlace())
                    .tag("dayOfWeek", p.getCurrent().getDayOfWeek())
                    .addField("new", delta(p, ApRecord::getCumulatedConfirmedForward))
                    .addField("cumulated", p.getCurrent().getCumulatedConfirmedForward())
                    .build())
    );

    private final ApDataSupplier supplier;

    public ApPointSource(ApDataSupplier supplier) {
        this.supplier = supplier;
    }

    private static <T> Optional<Point> pointIf(
            PairingCollector.Pair<T> pair,
            Predicate<PairingCollector.Pair<T>> predicate,
            Function<PairingCollector.Pair<T>, Point> recordConverter) {
        if (!predicate.test(pair)) {
            return Optional.empty();
        }

        return Optional.of(recordConverter.apply(pair));
    }

    private static <T> int delta(PairingCollector.Pair<T> pair, ToIntFunction<T> f) {
        int current = f.applyAsInt(pair.getCurrent());
        int previous = pair.getPrevious().map(f::applyAsInt).orElse(0);
        return current - previous;
    }

    @Override
    public Stream<Point> stream() {
        Map<String, List<ApRecord>> recordsGroupedByPlace = supplier
                .get()
                .getRecords().stream()
                .collect(Collectors.groupingBy(ApRecord::getPlace));

        verifyNoDuplicateDay(recordsGroupedByPlace);

        return recordsGroupedByPlace.keySet().stream()
                .map(recordsGroupedByPlace::get)
                .flatMap(
                        records -> records.stream()
                                .sorted(Comparator.comparingLong(ApRecord::getTimestamp))
                                .collect(PairingCollector.collector()).stream()
                                .flatMap(
                                        pair -> collectors.stream()
                                                .map(c -> c.apply(pair))
                                                .filter(Optional::isPresent)
                                                .map(Optional::get)
                                )

                );
    }

    private void verifyNoDuplicateDay(Map<String, List<ApRecord>> recordsGroupedByPlace) {
        boolean foundDuplicateDay = recordsGroupedByPlace.values().stream()
                .anyMatch(l ->
                        l.stream()
                                .collect(Collectors.groupingBy(ApRecord::getDate))
                                .entrySet().stream()
                                .anyMatch(e -> e.getValue().size() > 1)
                );

        if (foundDuplicateDay) {
            throw new IllegalArgumentException("Found a duplicate date");
        }
    }
}
