FROM azul/zulu-openjdk-alpine:11

ENTRYPOINT ["java", "-jar", "/usr/share/stats-loader.jar"]

ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/stats-loader.jar
