package net.cpollet.covid19.statsloader;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException {
        LOGGER.info("CI build number:  {}, commit {}",
                BuildPropertiesFactory.properties().getOrDefault("build.number", "-"),
                BuildPropertiesFactory.properties().getOrDefault("build.commit", "-"));

        new App().doWork();
        if ("true".equals(System.getenv("SCHEDULED"))) {
            LOGGER.info("Start in scheduled mode");
            Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(
                    () -> {
                        if (LocalDateTime.now().getMinute() == 0) {
                            new App().doWork();
                        }
                    },
                    0L,
                    30,
                    TimeUnit.SECONDS
            );
        }
    }

    private void doWork() {
        try {
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
        catch (Exception e) {
            LOGGER.error("Exception while executing: {}, ", e.getMessage(), e);
        } finally {
            H2Factory.shutdown();
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
}
