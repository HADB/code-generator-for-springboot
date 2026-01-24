FROM eclipse-temurin:25.0.1_8-jre-alpine

COPY build/libs/app.jar /app.jar

ENV SPRING_PROFILES_ACTIVE="prd"

ENTRYPOINT ["java", "-Xmx256m", "-jar", "/app.jar"]
