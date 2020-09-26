select
  date,
  canton,
  hospitalized,
  nvl((select p.hospitalized from full_data p where p.canton = d.canton and p.date = d.date - 1), 0) as prev_hospitalized
from
  full_data d