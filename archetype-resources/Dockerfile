FROM eclipse-temurin:21-jre-alpine

COPY target/app.jar /app.jar

ENV SPRING_PROFILES_ACTIVE="prd"
ENV JAVA_OPTS="-Xmx128m"

ENTRYPOINT java $$JAVA_OPTS -jar /app.jar
