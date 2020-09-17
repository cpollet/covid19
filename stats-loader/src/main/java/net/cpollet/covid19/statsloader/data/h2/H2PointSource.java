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
import java.util.stream.Stream;

@RequiredArgsConstructor
public class H2PointSource implements Source<Point> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Stream<Point> stream() {
        return jdbcTemplate.query("select " +
                        "  date," +
                        "  canton," +
                        "  cases," +
                        "  sum(cases) over (partition by canton order by date range between unbounded preceding and current row)," +
                        "  sum(cases) over (partition by canton order by date range between 14 preceding and current row)" +
                        "from" +
                        "  covid_data",
                (rs, rowNum) -> new H2Row(
                        LocalDate.parse(rs.getString(1)),
                        Switzerland.CantonCode.valueOf(rs.getString(2)),
                        Arrays.asList(
                                new H2Field("new", rs.getDouble(3)),
                                new H2Field("total", rs.getDouble(4)),
                                new H2Field(
                                        "prevalence",
                                        prevalence(rs.getDouble(4), Switzerland.CantonCode.valueOf(rs.getString(2)))
                                )
                        )
                )
        ).stream().map(r -> r.toPoint("h2.Confirmed"));
    }

    private double prevalence(double value, Switzerland.CantonCode canton) {
        return value / Switzerland.instance().canton(canton).population();
    }
}
