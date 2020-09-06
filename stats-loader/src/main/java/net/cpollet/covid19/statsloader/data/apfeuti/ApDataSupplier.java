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
package net.cpollet.covid19.statsloader.data.apfeuti;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.function.Supplier;

/**
 * Return data from
 * <a href="https://github.com/apfeuti/covid19-rest">https://github.com/apfeuti/covid19-rest</a>.
 */
public class ApDataSupplier implements Supplier<ApRoot> {
    private static final String URL = "https://covid19-rest.herokuapp.com/api/openzh/v1/all";

    @Override
    public ApRoot get() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(URL);
        return webTarget.request(MediaType.APPLICATION_JSON).get(ApRoot.class);
    }
}
