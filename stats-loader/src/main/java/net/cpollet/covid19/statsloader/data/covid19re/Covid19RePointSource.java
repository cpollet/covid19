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

import net.cpollet.covid19.statsloader.data.DataPoint;
import net.cpollet.covid19.statsloader.data.Source;

import java.util.stream.Stream;

public class Covid19RePointSource implements Source<DataPoint> {
    private final Covid19ReDataSupplier supplier;

    public Covid19RePointSource(Covid19ReDataSupplier supplier) {
        this.supplier = supplier;
    }

    @Override
    public Stream<DataPoint> stream() {
        return supplier.get().getRecords().stream()
                .map(Covid19ReRecord::toPoint);
    }
}
