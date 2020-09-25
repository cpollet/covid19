package net.cpollet.covid19.statsloader.data.h2.series;

import net.cpollet.covid19.statsloader.data.h2.H2Row;

import java.util.stream.Stream;

public interface DataSeries {
    Stream<H2Row> rows();
}
