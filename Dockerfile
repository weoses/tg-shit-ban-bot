FROM openjdk:21
ARG JAR_FILE=target/*.jar
COPY "${JAR_FILE}" app.jar
VOLUME /app/etc/banbot
ENTRYPOINT ["java", "-Dspring.config.location=/app/etc/banbot/","-jar","/app.jar"]