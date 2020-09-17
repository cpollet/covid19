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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Switzerland implements Area {
    private static Switzerland INSTANCE;
    private final Map<CantonCode, Canton> cantons = Collections.synchronizedMap(new EnumMap<>(CantonCode.class));
    private final Map<String, Object> cache = Collections.synchronizedMap(new HashMap<>());
    private final JdbcTemplate jdbcTemplate;

    public static Switzerland instance(JdbcTemplate jdbcTemplate) {
        if (INSTANCE == null) {
            INSTANCE = new Switzerland(jdbcTemplate);
        }
        return INSTANCE;
    }

    public static Switzerland instance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Instance not ready");
        }
        return INSTANCE;
    }

    @Override
    public int population() {
        return (int) cache.computeIfAbsent("population",
                key -> jdbcTemplate.query(
                        "select sum(count) from population",
                        (rs, rowNum) -> rs.getInt(1)
                ).get(0)
        );
    }

    @Override
    public int population(Sex sex) {
        return (int) cache.computeIfAbsent("population:" + sex,
                key -> new NamedParameterJdbcTemplate(jdbcTemplate).query(
                        "select sum(count) from population where sex = :sex",
                        new MapSqlParameterSource("sex", sex.shortName()),
                        (rs, rowNum) -> rs.getInt(1)
                ).get(0)
        );
    }

    public Canton canton(CantonCode canton) {
        if (!cantons.containsKey(canton)) {
            cantons.put(canton, new Canton(jdbcTemplate, canton));
        }

        return cantons.get(canton);
    }

    public enum CantonCode {
        ZH, BE, LU, UR, SZ, OW, NW, GL, ZG, FR, SO, BS, BL, SH, AR, AI, SG, GR, AG, TG, TI, VD, VS, NE, GE, JU, FL
    }
}
