FROM eclipse-temurin:21-jre-alpine

COPY target/app.jar /app.jar

ENV SPRING_PROFILES_ACTIVE="prd"

ENTRYPOINT ["java", "-Xmx256m", "-jar", "/app.jar"]
