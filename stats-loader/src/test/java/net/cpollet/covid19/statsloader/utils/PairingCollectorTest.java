package net.cpollet.covid19.statsloader.utils;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

class PairingCollectorTest {
    @Test
    void pairingNoElements_returnsAnEmptyList() {
        List<PairingCollector.Pair<Object>> collected = Stream.empty().collect(PairingCollector.collector());

        Assertions.assertThat(collected).isEmpty();
    }

    @Test
    void pairingOddNumberOfElements_returnsList() {
        List<PairingCollector.Pair<String>> collected = Stream.of("V").collect(PairingCollector.collector());

        Assertions.assertThat(collected)
                .hasSize(1)
                .element(0)
                .matches(p -> !p.getPrevious().isPresent(), "previous is absent")
                .matches(p -> p.getCurrent().equals("V"), "current equals V");
    }

    @Test
    void pairingEvenNumberOfElements_returnsList() {
        List<PairingCollector.Pair<String>> collected = Stream.of("A", "B").collect(PairingCollector.collector());

        ListAssert<PairingCollector.Pair<String>> listAssert = Assertions
                .assertThat(collected)
                .hasSize(2);
        listAssert.element(0)
                .matches(p -> !p.getPrevious().isPresent(), "previous is absent")
                .matches(p -> p.getCurrent().equals("A"), "current equals A");
        listAssert.element(1)
                .matches(p -> p.getPrevious().map(prev -> prev.equals("A")).orElse(false), "previous equals A")
                .matches(p -> p.getCurrent().equals("B"), "current equals B");
    }
}