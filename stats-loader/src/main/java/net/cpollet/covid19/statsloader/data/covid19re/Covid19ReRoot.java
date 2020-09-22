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

import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class Covid19ReRoot {
    private final List<Covid19ReRecord> records;

    public Covid19ReRoot(String data) {
        this.records = Stream.of(data.split("\n")).skip(1)
                .filter(s -> s.contains("Cori_slidingWindow"))
                .filter(s -> s.contains("Confirmed cases"))
                .map(Covid19ReRecord::fromString)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
