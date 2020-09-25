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

import lombok.RequiredArgsConstructor;
import net.cpollet.covid19.statsloader.data.DataPoint;
import net.cpollet.covid19.statsloader.data.Source;
import net.cpollet.covid19.statsloader.data.h2.series.CasesSeries;
import net.cpollet.covid19.statsloader.data.h2.series.DeathsSeries;
import net.cpollet.covid19.statsloader.data.h2.series.HospitalizedSeries;
import net.cpollet.covid19.statsloader.data.h2.series.TestsSeries;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class H2PointSource implements Source<DataPoint> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Stream<DataPoint> stream() {
        return Stream.of(
                new CasesSeries(jdbcTemplate).rows().map(r -> r.toPoint("h2.Cases")),
                new DeathsSeries(jdbcTemplate).rows().map(r -> r.toPoint("h2.Deaths")),
                new HospitalizedSeries(jdbcTemplate).rows().map(r -> r.toPoint("h2.Hospitalized")),
                new TestsSeries(jdbcTemplate).rows().map(r -> r.toPoint("h2.Tests"))
        ).flatMap(Function.identity());
    }

    private double casesPer100k(double value, long population) {
        return value / (double) population * 100_000.0;
    }
}
