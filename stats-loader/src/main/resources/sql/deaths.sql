select
  d.date,
  d.canton,
  d.deaths,
  d.cases,
  sum(d.cases) over (partition by d.canton order by d.date range between unbounded preceding and current row) as sum_cases,
  sum(d.deaths) over (partition by d.canton order by d.date range between unbounded preceding and current row) as sum_deaths,
  p.count as population
from
  full_data d, population p
where d.canton = p.canton and p.sex='T'