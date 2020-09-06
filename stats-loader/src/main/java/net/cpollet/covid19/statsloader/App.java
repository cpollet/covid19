package net.cpollet.covid19.statsloader;

import net.cpollet.covid19.statsloader.data.LastUpdateSource;
import net.cpollet.covid19.statsloader.data.Source;
import net.cpollet.covid19.statsloader.data.apfeuti.ApDataSupplier;
import net.cpollet.covid19.statsloader.data.apfeuti.ApPointSource;
import net.cpollet.covid19.statsloader.data.foph.FophDataSupplier;
import net.cpollet.covid19.statsloader.data.foph.FophPointSource;
import net.cpollet.covid19.statsloader.db.InfluxDBFactory;
import org.influxdb.InfluxDB;

import java.util.Arrays;
import java.util.stream.Stream;

public class App {
    public static void main(String[] args) {
        InfluxDB influxDB = InfluxDBFactory.covid19();

        Stream.of(
                new ApPointSource(Arrays.asList("GE", "VD", "ZH", "VS"), new ApDataSupplier()),
                new FophPointSource(new FophDataSupplier()),
                new LastUpdateSource()
        )
                .parallel()
                .flatMap(Source::stream)
                .forEach(influxDB::write);
    }
}
