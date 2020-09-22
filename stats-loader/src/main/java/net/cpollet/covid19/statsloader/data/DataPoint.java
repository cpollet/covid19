package net.cpollet.covid19.statsloader.data;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.influxdb.dto.Point;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@ToString
public class DataPoint {
    private final LocalDateTime date;
    private final String measure;
    private final Map<String, String> tags;
    private final Map<String, Double> fields;

    public Point toPoint() {
        Point.Builder pointBuilder = Point.measurement(measure)
                .time(timestamp(), TimeUnit.SECONDS);

        tags.forEach(pointBuilder::tag);
        fields.forEach(pointBuilder::addField);

        return pointBuilder.build();
    }

    private long timestamp() {
        return date.toEpochSecond(ZoneId.of("Europe/Zurich").getRules().getOffset(date));
    }
}
