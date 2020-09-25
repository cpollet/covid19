package net.cpollet.covid19.statsloader.data.h2.series;

import lombok.SneakyThrows;
import net.cpollet.covid19.statsloader.data.h2.H2Field;
import net.cpollet.covid19.statsloader.data.h2.H2Row;
import net.cpollet.covid19.statsloader.domain.Switzerland;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Stream;

public class DeathsSeries implements DataSeries {
    private final JdbcTemplate jdbcTemplate;

    public DeathsSeries(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @SneakyThrows
    @Override
    public Stream<H2Row> rows() {
        return jdbcTemplate.query(
                new String(DeathsSeries.class.getResourceAsStream("/sql/deaths.sql").readAllBytes()),
                (rs, rowNum) -> new H2Row(
                        LocalDate.parse(rs.getString("date")),
                        Switzerland.CantonCode.valueOf(rs.getString("canton")),
                        Arrays.asList(
                                new H2Field("new", rs.getDouble("deaths")),
                                new H2Field("sum", rs.getDouble("sum_deaths")),
                                new H2Field("rate", rs.getDouble("sum_deaths") / rs.getDouble("sum_cases")),
                                new H2Field(
                                        "incidence",
                                        casesPer100k(
                                                rs.getDouble("sum_deaths"),
                                                rs.getLong("population")
                                        )
                                )
                        )
                )
        ).stream();
    }

    private double casesPer100k(double value, long population) {
        return value / (double) population * 100_000.0;
    }
}
