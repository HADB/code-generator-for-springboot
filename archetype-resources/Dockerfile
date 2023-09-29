FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/${artifact_id}.jar app.jar
ENV SPRING_PROFILES_ACTIVE="prd"
ENV JAVA_OPTS="-Xmx128m"
ENV TZ="Asia/Shanghai"
ENTRYPOINT [ "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
