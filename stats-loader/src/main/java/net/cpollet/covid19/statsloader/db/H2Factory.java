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
package net.cpollet.covid19.statsloader.db;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public final class H2Factory {
    private static JdbcTemplate template;

    private H2Factory() {
        // nothing
    }

    public static JdbcTemplate inMemory() {
        if (template == null) {
            String h2Url = System.getProperty("h2.url");
            if (h2Url == null) {
                template = new JdbcTemplate(
                        new EmbeddedDatabaseBuilder()
                                .setType(EmbeddedDatabaseType.H2)
                                .addScript("classpath:jdbc/covid19.sql")
                                .setName("covid19")
                                .build()
                );
            }
            else {
                JdbcDataSource datasource = new JdbcDataSource();
                datasource.setURL(h2Url);
                datasource.setUser("sa");
                template = new JdbcTemplate(datasource);
            }
        }

        return template;
    }
}
