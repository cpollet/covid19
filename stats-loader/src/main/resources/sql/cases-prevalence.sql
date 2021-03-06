select
  d.date,
  d.canton,
  sum(d.cases) over (partition by d.canton order by d.date range between unbounded preceding and current row) as cases,
  p.count as population
from
  full_data d, population p
where
  d.canton = p.canton and p.sex='T'