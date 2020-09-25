select
  d.canton,
  sum(d.cases) as cases,
  p.count as population
from full_data d, population p
where d.canton = p.canton and p.sex='T'
group by d.canton