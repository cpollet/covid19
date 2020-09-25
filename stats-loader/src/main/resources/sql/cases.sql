select
  date,
  canton,
  cases,
  sum(cases) over (partition by canton order by date range between unbounded preceding and current row) as sum,
  sum(cases) over (partition by canton order by date range between 6 preceding and current row) as sum_7d,
  sum(cases) over (partition by canton order by date range between 13 preceding and current row) as sum_14d,
  avg(cases) over (partition by canton order by date range between 6 preceding and current row) as avg_7d,
  avg(cases) over (partition by canton order by date range between 13 preceding and current row) as avg_14d,
from
  full_data d
