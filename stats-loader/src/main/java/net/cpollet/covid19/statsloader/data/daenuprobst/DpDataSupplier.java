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
package net.cpollet.covid19.statsloader.data.daenuprobst;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.function.Supplier;

/**
 * Return data from
 * <a href="https://github.com/daenuprobst/covid19-cases-switzerland">https://github.com/daenuprobst/covid19-cases-switzerland</a>.
 */
public class DpDataSupplier implements Supplier<DpRoot> {
    private static final String URL = "https://raw.githubusercontent.com/daenuprobst/covid19-cases-switzerland/master/covid19_tested_switzerland_openzh.json";

    @Override
    public DpRoot get() {
        Client client = ClientBuilder.newBuilder().register(JsonProvider.class).build();
        WebTarget webTarget = client.target(URL);
        return webTarget.request(MediaType.APPLICATION_JSON).get(DpRoot.class);
    }
}