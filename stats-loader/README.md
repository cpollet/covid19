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