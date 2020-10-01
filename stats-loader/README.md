# stats-loader
```
mvn clean package dockerfile:push

docker run -it --rm \
  -e INFLUXDB_HOST=${host} \
  cpollet/covid19-stats-loader:${version}

docker run -it --rm \
  -e INFLUXDB_HOST=${host} \ 
  -e SCHEDULED=true \
  cpollet/covid19-stats-loader:${version}
```

## Configuration
 * `h2.url` system property: the h2 URL; defaults to in memory;
 * `influxdb.host` system property (falls back to `INFLUXDB_HOST` env variable): the influxDB host;
 * `SCHEDULED` env variable: if set to `true`, starts in scheduled mode.