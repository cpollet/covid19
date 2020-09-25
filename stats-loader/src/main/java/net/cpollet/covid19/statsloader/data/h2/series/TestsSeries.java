package net.cpollet.covid19.statsloader.data.h2.series;

import net.cpollet.covid19.statsloader.data.h2.H2Field;
import net.cpollet.covid19.statsloader.data.h2.H2Row;
import net.cpollet.covid19.statsloader.domain.Switzerland;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Stream;

public class TestsSeries implements DataSeries {
    private final JdbcTemplate jdbcTemplate;

    public TestsSeries(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Stream<H2Row> rows() {
        return jdbcTemplate.query(
                "select" +
                        "  date, " +
                        "  tests_negative, " +
                        "  tests_positive, " +
                        "  tests_total, " +
                        "  avg(tests_total) over (rows between 6 preceding and current row) as avg_tests_total_7d, " +
                        "  avg(tests_total) over (rows between 13 preceding and current row) as avg_tests_total_14d, " +
                        "  sum(tests_positive) over (rows between 6 preceding and current row) as sum_tests_positive_7d, " +
                        "  sum(tests_total) over (rows between 6 preceding and current row) as sum_tests_total_7d, " +
                        "  sum(tests_positive) over (rows between 13 preceding and current row) as sum_tests_positive_14d, " +
                        "  sum(tests_total) over (rows between 13 preceding and current row) as sum_tests_total_14d " +
                        "from" +
                        "  contiguous_covid_data " +
                        "where" +
                        "  canton='CH'",
                (rs, rowNum) -> new H2Row(
                        LocalDate.parse(rs.getString("date")),
                        Switzerland.CantonCode.CH,
                        Arrays.asList(
                                new H2Field("negative", rs.getDouble("tests_negative")),
                                new H2Field("positive", rs.getDouble("tests_positive")),
                                new H2Field("total", rs.getDouble("tests_total")),
                                new H2Field(
                                        "posRatio", rs.getDouble("tests_positive") / rs.getDouble("tests_total")
                                ),
                                new H2Field("avg_tests_total_7d", rs.getDouble("avg_tests_total_7d")),
                                new H2Field("avg_tests_total_14d", rs.getDouble("avg_tests_total_14d")),
                                new H2Field(
                                        "avg_posRatio_7d", rs.getDouble("sum_tests_positive_7d") / rs.getDouble("sum_tests_total_7d")
                                ),
                                new H2Field(
                                        "avg_posRatio_14d", rs.getDouble("sum_tests_positive_14d") / rs.getDouble("sum_tests_total_14d")
                                )
                        )
                )
        ).stream();
    }
}
