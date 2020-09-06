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

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;

public final class InfluxDBFactory {
    private InfluxDBFactory() {
        // nothing
    }

    public static InfluxDB covid19() {
        final InfluxDB influxDB = org.influxdb.InfluxDBFactory.connect("http://" + dbHost() + ":8086");
        final String databaseName = "covid19";
        influxDB.query(new Query("drop database " + databaseName));
        influxDB.query(new Query("create database " + databaseName));
        influxDB.setDatabase(databaseName);

        return influxDB;
    }

    private static String dbHost() {
        return System.getProperty("influxdb.host", System.getenv("INFLUXDB_HOST"));
    }
}
