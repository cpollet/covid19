package net.cpollet.covid19.statsloader.data.h2.series;

import net.cpollet.covid19.statsloader.data.h2.H2Field;
import net.cpollet.covid19.statsloader.data.h2.H2Row;
import net.cpollet.covid19.statsloader.domain.Switzerland;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

public class DeathsSeries implements DataSeries {
    private final JdbcTemplate jdbcTemplate;

    public DeathsSeries(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Stream<H2Row> rows() {
        return Stream.of(
                jdbcTemplate.query(
                        "select" +
                                "  date," +
                                "  canton," +
                                "  deaths," +
                                "  sum(cases) over (partition by canton order by date range between unbounded preceding and current row) as total_cases," +
                                "  sum(deaths) over (partition by canton order by date range between unbounded preceding and current row) as total_deaths," +
                                "  (select count from population where canton = d.canton and sex='M') as male_pop," +
                                "  (select count from population where canton = d.canton and sex='F') as female_pop " +
                                "from " +
                                "  contiguous_covid_data d",
                        (rs, rowNum) -> new H2Row(
                                LocalDate.parse(rs.getString("date")),
                                Switzerland.CantonCode.valueOf(rs.getString("canton")),
                                Arrays.asList(
                                        new H2Field("new", rs.getDouble("deaths")),
                                        new H2Field("total", rs.getDouble("total_deaths")),
                                        new H2Field("death_rate", rs.getDouble("total_deaths") / rs.getDouble("total_cases")),
                                        new H2Field(
                                                "incidence",
                                                casesPer100k(
                                                        rs.getDouble("total_deaths"),
                                                        rs.getLong("male_pop") + rs.getLong("female_pop")
                                                )
                                        )
                                )

                        )
                ).stream(),
                jdbcTemplate.query(
                        "select " +
                                "  (select sum(cases) cases from contiguous_covid_data) as cases," +
                                "  (select sum(deaths) deaths from contiguous_covid_data) as deaths",
                        (rs, rowNum) -> new H2Row(
                                LocalDate.now(),
                                Switzerland.CantonCode.CH,
                                new H2Field(
                                        "death_rate",
                                        rs.getDouble("deaths") / rs.getLong("cases")
                                )

                        )
                ).stream()
        ).flatMap(Function.identity());
    }

    private double casesPer100k(double value, long population) {
        return value / (double) population * 100_000.0;
    }
}
