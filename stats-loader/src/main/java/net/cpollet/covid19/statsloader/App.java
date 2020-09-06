package net.cpollet.covid19.statsloader;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import net.cpollet.covid19.statsloader.data.Source;
import net.cpollet.covid19.statsloader.data.apfeuti.ApDataSupplier;
import net.cpollet.covid19.statsloader.data.apfeuti.ApPointSource;
import net.cpollet.covid19.statsloader.data.foph.FophDataSupplier;
import net.cpollet.covid19.statsloader.data.foph.FophPointSource;
import net.cpollet.covid19.statsloader.db.InfluxDBFactory;
import org.influxdb.InfluxDB;

import java.util.Arrays;
import java.util.stream.Stream;

public class App implements HttpFunction {
    public static void main(String[] args) {
        InfluxDB influxDB = InfluxDBFactory.covid19();

        Stream.of(
                new ApPointSource(Arrays.asList("GE", "VD", "ZH", "VS"), new ApDataSupplier()),
                new FophPointSource(new FophDataSupplier())
        )
                .parallel()
                .flatMap(Source::stream)
                .forEach(influxDB::write);
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        App.main(new String[]{});
    }
}
