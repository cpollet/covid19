create table raw_data (
    date date,
    canton varchar(2),
    -- weekday int, -- ISO-8601 standard
    -- week int,
    -- sex varchar(1), -- M, F
    cases int default 0,
    deaths int default 0,
    hospitalized int default 0,
    tests_positive int default 0,
    tests_negative int default 0,
    primary key (date, canton)
);

create table dates (
    date date primary key
);

create table population (
    canton varchar(2),
    sex varchar(1), -- M, F
    count int
);

-- contains all cantons + CH
create view cantons as (
    select distinct canton as canton from population
    union
    select 'CH'
);

-- contains all dates and cantons combinations
create view universe as (
  select dates.date, cantons.canton from dates cross join cantons
);

-- contains the full data set (sums for CH, actual values for the cantons)
create view consolidated_data as (
  select
    date,
    'CH' as canton,
    sum(cases) as cases,
    sum(deaths) as deaths,
    sum(hospitalized) as hospitalized,
    sum(tests_positive) as tests_positive,
    sum(tests_negative) as tests_negative
  from raw_data
  group by date
  union (
    select
      date,
      canton,
      cases,
      deaths,
      hospitalized,
      0 as tests_positive,
      0 as tests_negative
    from raw_data
    where canton<>'CH'
  )
);

-- contains a consolidated data row for each date+canton combinations
create view full_data as (
  select
    u.date,
    u.canton,
    nvl(d.cases,0) as cases,
    nvl(d.deaths,0) as deaths,
    nvl(d.hospitalized,0) as hospitalized,
    nvl(d.tests_positive,0) as tests_positive,
    nvl(d.tests_negative,0) as tests_negative,
    nvl(d.tests_positive,0) + nvl(d.tests_negative,0) as tests_total
  from consolidated_data d right join universe u on d.date = u.date and d.canton = u.canton
);

create view contiguous_covid_data as ( -- deprecated
    with dataset as (select dates.date, cantons.canton from dates cross join cantons)
    select
        d.date,
        d.canton,
        nvl(rd.cases,0) as cases,
        nvl(rd.deaths,0) as deaths,
        nvl(hospitalized,0) as hospitalized,
        nvl(tests_positive,0) as tests_positive,
        nvl(tests_negative,0) as tests_negative,
        nvl(tests_positive,0) + nvl(tests_negative,0) as tests_total
    from raw_data rd right join dataset d on rd.date = d.date and rd.canton = d.canton
);

-- source: http://www.pxweb.bfs.admin.ch/sq/a1e5c2da-3e0d-4a48-a99d-901bb55f5db8
insert into population values ('ZH','M',757081);
insert into population values ('ZH','F',763887);
insert into population values ('BE','M',507791);
insert into population values ('BE','F',527186);
insert into population values ('LU','M',204100);
insert into population values ('LU','F',205457);
insert into population values ('UR','M',18609);
insert into population values ('UR','F',17824);
insert into population values ('SZ','M',81599);
insert into population values ('SZ','F',77566);
insert into population values ('OW','M',19144);
insert into population values ('OW','F',18697);
insert into population values ('NW','M',22122);
insert into population values ('NW','F',21101);
insert into population values ('GL','M',20448);
insert into population values ('GL','F',19955);
insert into population values ('ZG','M',64195);
insert into population values ('ZG','F',62642);
insert into population values ('FR','M',159579);
insert into population values ('FR','F',159135);
insert into population values ('SO','M',136475);
insert into population values ('SO','F',136719);
insert into population values ('BS','M',94311);
insert into population values ('BS','F',100455);
insert into population values ('BL','M',141341);
insert into population values ('BL','F',146791);
insert into population values ('SH','M',40560);
insert into population values ('SH','F',41431);
insert into population values ('AR','M',27889);
insert into population values ('AR','F',27345);
insert into population values ('AI','M',8300);
insert into population values ('AI','F',7845);
insert into population values ('SG','M',254304);
insert into population values ('SG','F',253393);
insert into population values ('GR','M',99374);
insert into population values ('GR','F',99005);
insert into population values ('AG','M',340776);
insert into population values ('AG','F',337431);
insert into population values ('TG','M',139185);
insert into population values ('TG','F',137287);
insert into population values ('TI','M',172192);
insert into population values ('TI','F',181151);
insert into population values ('VD','M',392529);
insert into population values ('VD','F',406616);
insert into population values ('VS','M',170431);
insert into population values ('VS','F',173524);
insert into population values ('NE','M',86621);
insert into population values ('NE','F',90229);
insert into population values ('GE','M',241848);
insert into population values ('GE','F',257632);
insert into population values ('JU','M',36317);
insert into population values ('JU','F',37102);

insert into population values ('CH', 'M', (select sum(count) from population where sex='M'));
insert into population values ('CH', 'F', (select sum(count) from population where sex='F'));
insert into population select canton, 'T', sum(count) from population group by canton;