FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/${artifact_id}.jar /app
ENV SPRING_PROFILES_ACTIVE="prd"
ENV JAVA_OPTS="-Xmx128m"
ENV TZ="Asia/Shanghai"
ENTRYPOINT [ "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/${artifact_id}.jar"]
