package net.cpollet.covid19.statsloader;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import net.cpollet.covid19.statsloader.data.DataPoint;
import net.cpollet.covid19.statsloader.data.LastUpdateSource;
import net.cpollet.covid19.statsloader.data.apfeuti.ApDataLoader;
import net.cpollet.covid19.statsloader.data.apfeuti.ApDataSupplier;
import net.cpollet.covid19.statsloader.data.covid19re.Covid19ReDataSupplier;
import net.cpollet.covid19.statsloader.data.covid19re.Covid19RePointSource;
import net.cpollet.covid19.statsloader.data.foph.FophDataSupplier;
import net.cpollet.covid19.statsloader.data.foph.FophPointSource;
import net.cpollet.covid19.statsloader.data.h2.H2PointSource;
import net.cpollet.covid19.statsloader.data.timoll.TmDataLoader;
import net.cpollet.covid19.statsloader.data.timoll.TmDataSupplier;
import net.cpollet.covid19.statsloader.db.H2Factory;
import net.cpollet.covid19.statsloader.db.InfluxDBFactory;
import net.cpollet.covid19.statsloader.domain.Switzerland;
import org.influxdb.dto.BatchPoints;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

        LOGGER.info("Computing points");
        List<? extends Callable<Stream<DataPoint>>> tasks = Arrays.asList(
                () -> new H2PointSource(H2Factory.inMemory()).stream(),
                () -> new FophPointSource(new FophDataSupplier()).stream(),
                () -> new Covid19RePointSource(new Covid19ReDataSupplier()).stream()
        );

        try {
            ExecutorService executor = Executors.newFixedThreadPool(1);
            try {
                return executor.invokeAll(tasks).stream()
                        .flatMap(this::get)
                        .collect(Collectors.toList());
            } finally {
                executor.shutdown();
            }
        }
        catch (InterruptedException e) {
            LOGGER.info("Interrupted");
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        }
    }

    private Stream<DataPoint> get(Future<Stream<DataPoint>> future) {
        try {
            return future.get();
        }
        catch (InterruptedException e) {
            LOGGER.info("Interrupted");
            Thread.currentThread().interrupt();
            return Stream.empty();
        }
        catch (ExecutionException e) {
            throw new Error(e);
        }
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        doWork();
    }
}
