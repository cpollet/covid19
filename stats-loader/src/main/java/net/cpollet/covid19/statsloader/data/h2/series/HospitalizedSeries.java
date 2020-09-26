package net.cpollet.covid19.statsloader.data.h2.series;

import lombok.SneakyThrows;
import net.cpollet.covid19.statsloader.data.h2.H2Field;
import net.cpollet.covid19.statsloader.data.h2.H2Row;
import net.cpollet.covid19.statsloader.domain.Switzerland;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Stream;

public class HospitalizedSeries implements DataSeries {
    private final JdbcTemplate jdbcTemplate;

    public HospitalizedSeries(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @SneakyThrows
    @Override
    public Stream<H2Row> rows() {
        return jdbcTemplate.query(
                new String(HospitalizedSeries.class.getResourceAsStream("/sql/hospitalized.sql").readAllBytes()),
                (rs, rowNum) -> new H2Row(
                        LocalDate.parse(rs.getString("date")),
                        Switzerland.CantonCode.valueOf(rs.getString("canton")),
                        Arrays.asList(
                                new H2Field("current", rs.getDouble("hospitalized")),
                                new H2Field(
                                        "new",
                                        rs.getDouble("hospitalized") - rs.getDouble("prev_hospitalized")
                                )
                        )

                )
        ).stream();
    }
}
