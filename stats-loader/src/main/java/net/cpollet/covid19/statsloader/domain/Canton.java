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
package net.cpollet.covid19.statsloader.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Canton implements Area {
    private final JdbcTemplate jdbcTemplate;
    private final Switzerland.CantonCode canton;
    private final Map<String, Object> cache = Collections.synchronizedMap(new HashMap<>());

    @Override
    public int population() {
        return (int) cache.computeIfAbsent("population",
                key -> new NamedParameterJdbcTemplate(jdbcTemplate).query(
                        "select sum(count) from population where canton = :canton",
                        new MapSqlParameterSource("canton", canton.name()),
                        (rs, rowNum) -> rs.getInt(1)
                ).get(0)
        );
    }

    @Override
    public int population(Sex sex) {
        return (int) cache.computeIfAbsent("population:" + sex,
                key -> new NamedParameterJdbcTemplate(jdbcTemplate).query(
                        "select sum(count) from population where canton = :canton and sex = :sex",
                        new MapSqlParameterSource()
                                .addValue("canton", canton.name())
                                .addValue("sex", sex.shortName()),
                        (rs, rowNum) -> rs.getInt(1)
                ).get(0)
        );
    }
}
