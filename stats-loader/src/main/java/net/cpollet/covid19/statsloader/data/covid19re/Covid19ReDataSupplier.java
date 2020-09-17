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
package net.cpollet.covid19.statsloader.data.covid19re;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.function.Supplier;

/**
 * Return data from
 * <a href="https://github.com/covid-19-Re/dailyRe-Data">https://github.com/covid-19-Re/dailyRe-Data</a>.
 */
public class Covid19ReDataSupplier implements Supplier<Covid19ReRoot> {
    private static final String URL = "https://raw.githubusercontent.com/covid-19-Re/dailyRe-Data/master/CHE-estimates.csv";

    @Override
    public Covid19ReRoot get() {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget webTarget = client.target(URL);
        String data = webTarget.request().get().readEntity(String.class);

        return new Covid19ReRoot(data);
    }
}
