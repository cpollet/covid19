package net.cpollet.covid19.statsloader;

import net.cpollet.covid19.statsloader.data.openzh.OpenZHRoot;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("https://covid19-rest.herokuapp.com/api/openzh/v1/all");

        OpenZHRoot data = webTarget.request(MediaType.APPLICATION_JSON)
                .get(OpenZHRoot.class);

        final String serverURL = "http://" + System.getProperty("influxdb.host") + ":8086";
        final String databaseName = "covid19";

        final InfluxDB influxDB = InfluxDBFactory.connect(serverURL);

        influxDB.query(new Query("drop database " + databaseName));
        influxDB.query(new Query("create database " + databaseName));

        influxDB.setDatabase(databaseName);

        data.getRecords().stream()
                .filter(r -> r.getPlace().equals("GE") || r.getPlace().equals("VD"))
                .forEach(r -> {
                    LocalDateTime dateTime = LocalDateTime.of(
                            r.getDate(),
                            Optional.ofNullable(r.getTime()).orElse(LocalTime.of(12, 0))
                    );
                    long timestamp = dateTime.toEpochSecond(ZoneId.of("Europe/Zurich").getRules().getOffset(dateTime));
                    influxDB.write(
                            Point.measurement("cumulatedConfirmedForward")
                                    .time(timestamp, TimeUnit.SECONDS)
                                    .tag("canton", r.getPlace())
                                    .addField("value", r.getCumulatedConfirmedForward())
                                    .build()
                    );
                });
    }
}
