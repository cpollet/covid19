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
package net.cpollet.covid19.statsloader.data.timoll;

import lombok.RequiredArgsConstructor;
import net.cpollet.covid19.statsloader.db.DataLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.function.Supplier;


@RequiredArgsConstructor
public class TmDataLoader implements DataLoader<TmRoot> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void load(Supplier<TmRoot> supplier) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        supplier.get().getRecords().forEach(
                r -> namedParameterJdbcTemplate.update(
                        "merge into covid_data (date, canton, " + r.getMeasure() + ")" +
                                "values (:date, :canton, :measure)",
                        new MapSqlParameterSource()
                                .addValue("date", r.getDate())
                                .addValue("canton", r.getCanton().name())
                                .addValue("measure", r.getValue())
                )
        );
    }
}
