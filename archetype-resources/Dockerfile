FROM openjdk:8-alpine

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

VOLUME /tmp

ARG JAR_FILE
COPY $${JAR_FILE} app.jar
ENV SPRING_PROFILES_ACTIVE="prd"
ENV JAVA_OPTS="-Xmx128m"
ENTRYPOINT [ "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
