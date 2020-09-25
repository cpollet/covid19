package net.cpollet.covid19.statsloader.data.h2.series;

import lombok.SneakyThrows;
import net.cpollet.covid19.statsloader.data.h2.H2Field;
import net.cpollet.covid19.statsloader.data.h2.H2Row;
import net.cpollet.covid19.statsloader.domain.Switzerland;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
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
                cases().stream(),
                incidence().stream(),
                prevalence().stream()
        ).flatMap(Function.identity());
    }

    @SneakyThrows
    private List<H2Row> cases() {
        return jdbcTemplate.query(
                new String(CasesSeries.class.getResourceAsStream("/sql/cases.sql").readAllBytes()),
                (rs, rowNum) -> new H2Row(
                        LocalDate.parse(rs.getString("date")),
                        Switzerland.CantonCode.valueOf(rs.getString("canton")),
                        Arrays.asList(
                                new H2Field("new", rs.getDouble("cases")),
                                new H2Field("sum", rs.getDouble("sum")),
                                new H2Field("sum_7d", rs.getDouble("sum_7d")),
                                new H2Field("sum_14d", rs.getDouble("sum_14d")),
                                new H2Field("avg_7d", rs.getDouble("avg_7d")),
                                new H2Field("avg_14d", rs.getDouble("avg_14d"))
                        )
                )
        );
    }

    @SneakyThrows
    private List<H2Row> incidence() {
        return jdbcTemplate.query(
                new String(CasesSeries.class.getResourceAsStream("/sql/cases-incidence.sql").readAllBytes()),
                (rs, rowNum) -> new H2Row(
                        LocalDate.parse(rs.getString("date")),
                        Switzerland.CantonCode.valueOf(rs.getString("canton")),
                        new H2Field(
                                "incidence",
                                casesPer100k(rs.getDouble("sum_14d"), rs.getLong("population"))
                        )
                )
        );
    }

    @SneakyThrows
    private List<H2Row> prevalence() {
        return jdbcTemplate.query(
                new String(CasesSeries.class.getResourceAsStream("/sql/cases-prevalence.sql").readAllBytes()),
                (rs, rowNum) -> new H2Row(
                        LocalDate.now(),
                        Switzerland.CantonCode.valueOf(rs.getString("canton")),
                        new H2Field("prevalence", casesPer100k(rs.getDouble("cases"), rs.getLong("population")))
                )
        );
    }

    private double casesPer100k(double value, long population) {
        return value / (double) population * 100_000.0;
    }
}
