package net.cpollet.covid19.statsloader.data.h2.series;

import net.cpollet.covid19.statsloader.data.h2.H2Field;
import net.cpollet.covid19.statsloader.data.h2.H2Row;
import net.cpollet.covid19.statsloader.domain.Switzerland;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

public class CasesSeries implements DataSeries {
    private final JdbcTemplate jdbcTemplate;

    public CasesSeries(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Stream<H2Row> rows() {
        return Stream.of(
                jdbcTemplate.query(
                        "select" +
                                "  date," +
                                "  canton," +
                                "  cases," +
                                "  sum(cases) over (partition by canton order by date range between unbounded preceding and current row) as sum, " +
                                "  sum(cases) over (partition by canton order by date range between 6 preceding and current row) as sum_7d, " +
                                "  sum(cases) over (partition by canton order by date range between 13 preceding and current row) as sum_14d, " +
                                "  avg(cases) over (partition by canton order by date range between 6 preceding and current row) as avg_7d, " +
                                "  avg(cases) over (partition by canton order by date range between 13 preceding and current row) as avg_14d, " +
                                "  (select count from population where canton = d.canton and sex='M') as male_pop, " +
                                "  (select count from population where canton = d.canton and sex='F') as female_pop " +
                                "from" +
                                "  contiguous_covid_data d",
                        (rs, rowNum) -> new H2Row(
                                LocalDate.parse(rs.getString("date")),
                                Switzerland.CantonCode.valueOf(rs.getString("canton")),
                                Arrays.asList(
                                        new H2Field("new", rs.getDouble("cases")),
                                        new H2Field("sum", rs.getDouble("sum")),
                                        new H2Field("sum_7d", rs.getDouble("sum_7d")),
                                        new H2Field("sum_14d", rs.getDouble("sum_14d")),
                                        new H2Field("avg_7d", rs.getDouble("avg_7d")),
                                        new H2Field("avg_14d", rs.getDouble("avg_14d")),
                                        new H2Field(
                                                "incidence",
                                                casesPer100k(
                                                        rs.getDouble("sum_14d"),
                                                        rs.getLong("male_pop") + rs.getLong("female_pop")
                                                )
                                        )
                                )

                        )
                ).stream(),
                jdbcTemplate.query(
                        "select " +
                                "  (select sum(cases) cases from contiguous_covid_data where date > sysdate - 15 and date < sysdate - 1) as cases," +
                                "  (select sum(count) from population) as total_pop",
                        (rs, rowNum) -> new H2Row(
                                LocalDate.now(),
                                Switzerland.CantonCode.CH,
                                new H2Field(
                                        "incidence",
                                        casesPer100k(rs.getDouble("cases"), rs.getLong("total_pop"))
                                )

                        )
                ).stream(),
                jdbcTemplate.query(
                        "with " +
                                "canton_population as (" +
                                "  select canton, sum(count) as sum_population from population group by canton" +
                                ") " +
                                "select " +
                                "  c.canton," +
                                "  c.sum_population population," +
                                "  sum(d.cases) cases " +
                                "from" +
                                "  canton_population c," +
                                "  contiguous_covid_data d " +
                                "where c.canton = d.canton " +
                                "group by d.canton",
                        (rs, rowNum) -> new H2Row(
                                LocalDate.now(),
                                Switzerland.CantonCode.valueOf(rs.getString("canton")),
                                new H2Field("prevalence", casesPer100k(rs.getDouble("cases"), rs.getLong("population")))
                        )
                ).stream(),
                jdbcTemplate.query(
                        "select " +
                                "(select sum(count) from population) population," +
                                "(select sum(cases) from contiguous_covid_data) cases",
                        (rs, rowNum) -> new H2Row(
                                LocalDate.now(),
                                Switzerland.CantonCode.CH,
                                new H2Field("prevalence", casesPer100k(rs.getDouble("cases"), rs.getLong("population")))
                        )
                ).stream()
        ).flatMap(Function.identity());
    }

    private double casesPer100k(double value, long population) {
        return value / (double) population * 100_000.0;
    }
}
