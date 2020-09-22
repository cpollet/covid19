package net.cpollet.covid19.statsloader;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import net.cpollet.covid19.statsloader.data.DataPoint;
import net.cpollet.covid19.statsloader.data.LastUpdateSource;
import net.cpollet.covid19.statsloader.data.Source;
import net.cpollet.covid19.statsloader.data.apfeuti.ApDataLoader;
import net.cpollet.covid19.statsloader.data.apfeuti.ApDataSupplier;
import net.cpollet.covid19.statsloader.data.covid19re.Covid19ReDataSupplier;
import net.cpollet.covid19.statsloader.data.covid19re.Covid19RePointSource;
import net.cpollet.covid19.statsloader.data.foph.FophDataLoader;
import net.cpollet.covid19.statsloader.data.foph.FophDataSupplier;
import net.cpollet.covid19.statsloader.data.h2.H2PointSource;
import net.cpollet.covid19.statsloader.data.timoll.TmDataLoader;
import net.cpollet.covid19.statsloader.data.timoll.TmDataSupplier;
import net.cpollet.covid19.statsloader.db.H2Factory;
import net.cpollet.covid19.statsloader.db.InfluxDBFactory;
import net.cpollet.covid19.statsloader.domain.Switzerland;
import org.influxdb.dto.BatchPoints;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App implements HttpFunction {
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        new App().doWork();
    }

    private void doWork() {
        long startTime = System.currentTimeMillis();

        List<DataPoint> points = computePoints();

        if (!Thread.interrupted() && !points.isEmpty()) {
            LOGGER.info("Sending points to influxdb");
            InfluxDBFactory.covid19().write(
                    BatchPoints.builder()
                            .points(
                                    points.stream()
                                            .map(DataPoint::toPoint)
                                            .collect(Collectors.toList())
                            )
                            .points(new LastUpdateSource().stream().collect(Collectors.toList())).build()
            );

            long duration = System.currentTimeMillis() - startTime;
            LOGGER.info(String.format("%d points sent in %d ms", points.size(), duration));
        }
    }

    private List<DataPoint> computePoints() {
        LOGGER.info("Importing data");
        Switzerland.instance(H2Factory.inMemory());
        new TmDataLoader(H2Factory.inMemory()).load(new TmDataSupplier());
        new ApDataLoader(H2Factory.inMemory()).load(new ApDataSupplier());
        new FophDataLoader(H2Factory.inMemory()).load(new FophDataSupplier());

        LOGGER.info("Computing points");
        return Stream.of(
                new H2PointSource(H2Factory.inMemory()),
                new Covid19RePointSource(new Covid19ReDataSupplier())
        ).flatMap(Source::stream).collect(Collectors.toList());
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        doWork();
    }
}
