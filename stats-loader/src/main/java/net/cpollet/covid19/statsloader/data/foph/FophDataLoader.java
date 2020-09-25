package net.cpollet.covid19.statsloader.data.foph;

import lombok.AllArgsConstructor;
import net.cpollet.covid19.statsloader.db.DataLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.time.LocalDate;
import java.util.function.Supplier;

@AllArgsConstructor
public class FophDataLoader implements DataLoader<FophRoot> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void load(Supplier<FophRoot> supplier) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        supplier.get().getRecords().forEach(
                r -> namedParameterJdbcTemplate.update(
                        "merge into raw_data (date, canton, tests_positive, tests_negative)" +
                                "values (:date, :canton, :tests_positive, :tests_negative)",
                        new MapSqlParameterSource()
                                .addValue("date", r.getDate())
                                .addValue("canton", "CH")
                                .addValue("tests_positive", r.getPositive())
                                .addValue("tests_negative", r.getNegative())
                )
        );

        jdbcTemplate.query("select min(date) from raw_data", rs -> {
            LocalDate minDate = LocalDate.parse(rs.getString(1)).minusDays(1);
            LocalDate current = LocalDate.now();

            while (current.isAfter(minDate)) {
                namedParameterJdbcTemplate.update("merge into dates values (:date)",
                        new MapSqlParameterSource("date", current)
                );
                current = current.minusDays(1);
            }
        });
    }
}
