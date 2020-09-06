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
package net.cpollet.covid19.statsloader.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;

public final class PairingCollector {
    private PairingCollector() {
        // empty
    }

    public static <T> Collector<T, ?, List<Pair<T>>> collector() {
        class Pairing {
            private final List<Pair<T>> recordPairs = new ArrayList<>();
            private T lastRecord;

            private void accept(T record) {
                if (lastRecord == null) {
                    lastRecord = record;
                    recordPairs.add(new Pair<>(record));
                    return;
                }

                recordPairs.add(new Pair<>(lastRecord, record));
                this.lastRecord = record;
            }

            private Pairing combine(Pairing other) {
                throw new UnsupportedOperationException("Parallel streams not supported");
            }

            private List<Pair<T>> finish() {
                return recordPairs;
            }
        }

        return Collector.of(
                Pairing::new,
                Pairing::accept,
                Pairing::combine,
                Pairing::finish
        );
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Pair<T> {
        private final T previous;

        @NonNull
        private final T current;

        public Pair(T current) {
            this(null, current);
        }

        public Optional<T> getPrevious() {
            return Optional.ofNullable(previous);
        }
    }
}
