select
  date,
  tests_negative,
  tests_positive,
  tests_total,
  avg(tests_total) over (rows between 6 preceding and current row) as avg_tests_total_7d,
  avg(tests_total) over (rows between 13 preceding and current row) as avg_tests_total_14d,
  sum(tests_positive) over (rows between 6 preceding and current row) as sum_tests_positive_7d,
  sum(tests_total) over (rows between 6 preceding and current row) as sum_tests_total_7d,
  sum(tests_positive) over (rows between 13 preceding and current row) as sum_tests_positive_14d,
  sum(tests_total) over (rows between 13 preceding and current row) as sum_tests_total_14d
from
  full_data
where
  canton='CH'