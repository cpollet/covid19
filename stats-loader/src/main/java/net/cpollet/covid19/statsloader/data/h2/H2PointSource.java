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
import net.cpollet.covid19.statsloader.data.Source;
import net.cpollet.covid19.statsloader.domain.Switzerland;
import org.influxdb.dto.Point;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class H2PointSource implements Source<Point> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Stream<Point> stream() {
        return Stream.of(
                jdbcTemplate.query(
                        "select" +
                                "  date," +
                                "  canton," +
                                "  cases," +
                                "  sum(cases) over (partition by canton order by date range between unbounded preceding and current row) as total," +
                                "  sum(cases) over (partition by canton order by date range between 15 preceding and 1 preceding) as moving_window," +
                                "  (select count from population where canton = d.canton and sex='M') as male_pop," +
                                "  (select count from population where canton = d.canton and sex='F') as female_pop " +
                                "from" +
                                "  contiguous_covid_data d",
                        (rs, rowNum) -> new H2Row(
                                LocalDate.parse(rs.getString("date")),
                                Switzerland.CantonCode.valueOf(rs.getString("canton")),
                                Arrays.asList(
                                        new H2Field("new", rs.getDouble("cases")),
                                        new H2Field("total", rs.getDouble("total")),
                                        new H2Field(
                                                "incidence",
                                                incidence(
                                                        rs.getDouble("moving_window"),
                                                        rs.getLong("male_pop") + rs.getLong("female_pop")
                                                )
                                        )
                                )

                        )
                ).stream().map(r -> r.toPoint("h2.Confirmed")),
                jdbcTemplate.query(
                        "select " +
                                "  (select sum(cases) cases from contiguous_covid_data where date > sysdate - 15 and date < sysdate - 1) as cases," +
                                "  (select sum(count) from population) as total_pop",
                        (rs, rowNum) -> new H2Row(
                                LocalDate.now(),
                                Switzerland.CantonCode.CH,
                                new H2Field(
                                        "incidence_14d",
                                        incidence(rs.getDouble("cases"), rs.getLong("total_pop"))
                                )

                        )
                ).stream().map(r -> r.toPoint("h2.Confirmed")),
                jdbcTemplate.query(
                        "select" +
                                "  date," +
                                "  canton," +
                                "  deaths," +
                                "  sum(cases) over (partition by canton order by date range between unbounded preceding and current row) as total_cases," +
                                "  sum(deaths) over (partition by canton order by date range between unbounded preceding and current row) as total_deaths," +
                                "  (select count from population where canton = d.canton and sex='M') as male_pop," +
                                "  (select count from population where canton = d.canton and sex='F') as female_pop " +
                                "from " +
                                "  contiguous_covid_data d",
                        (rs, rowNum) -> new H2Row(
                                LocalDate.parse(rs.getString("date")),
                                Switzerland.CantonCode.valueOf(rs.getString("canton")),
                                Arrays.asList(
                                        new H2Field("new", rs.getDouble("deaths")),
                                        new H2Field("total", rs.getDouble("total_deaths")),
                                        new H2Field("death_rate", rs.getDouble("total_deaths") / rs.getDouble("total_cases")),
                                        new H2Field(
                                                "incidence",
                                                incidence(
                                                        rs.getDouble("total_deaths"),
                                                        rs.getLong("male_pop") + rs.getLong("female_pop")
                                                )
                                        )
                                )

                        )
                ).stream().map(r -> r.toPoint("h2.Deaths"))
        ).flatMap(Function.identity());
    }

    private double incidence(double value, long population) {
        return value / (double) population * 100_000.0;
    }
}
