package net.cpollet.covid19.statsloader;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import net.cpollet.covid19.statsloader.data.LastUpdateSource;
import net.cpollet.covid19.statsloader.data.Source;
import net.cpollet.covid19.statsloader.data.apfeuti.ApDataSupplier;
import net.cpollet.covid19.statsloader.data.apfeuti.ApPointSource;
import net.cpollet.covid19.statsloader.data.foph.FophDataSupplier;
import net.cpollet.covid19.statsloader.data.foph.FophPointSource;
import net.cpollet.covid19.statsloader.db.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App implements HttpFunction {
    private static Logger LOGGER = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        new App().doWork();
    }

    private void doWork() {
        long startTime = System.currentTimeMillis();

        LOGGER.info("Computing points");
        List<Point> points = Stream.of(
                new ApPointSource(new ApDataSupplier()),
                new FophPointSource(new FophDataSupplier())
        )
                .parallel()
                .flatMap(Source::stream)
                .collect(Collectors.toList());

        LOGGER.info("Sending points to influxdb");
        InfluxDBFactory.covid19().write(
                BatchPoints.builder()
                        .points(points)
                        .points(new LastUpdateSource().stream().collect(Collectors.toList())).build()
        );

        long duration = System.currentTimeMillis() - startTime;
        LOGGER.info(String.format("Finised in %d ms", duration));
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        doWork();
    }
}
