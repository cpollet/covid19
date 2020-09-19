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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.function.Supplier;

/**
 * Return data from
 * <a href="https://github.com/timoll/bag_scrape">https://github.com/timoll/bag_scrape</a>.
 */
public class TmDataSupplier implements Supplier<TmRoot> {
    private static final String CASES_URL = "https://raw.githubusercontent.com/timoll/bag_scrape/master/out/ch_cantons/cases.csv";
    private static final String DEATHS_URL = "https://raw.githubusercontent.com/timoll/bag_scrape/master/out/ch_cantons/deaths.csv";

    @Override
    public TmRoot get() {
        Client client = ClientBuilder.newBuilder().build();

        WebTarget webTargetCasesData = client.target(CASES_URL);
        String casesData = webTargetCasesData.request().get().readEntity(String.class);

        WebTarget webTargetDeathsData = client.target(DEATHS_URL);
        String deathsData = webTargetDeathsData.request().get().readEntity(String.class);

        return new TmRoot(casesData, deathsData);
    }
}
