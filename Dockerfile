FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD app.jar app.jar
RUN apk add --no-cache tini
ENTRYPOINT ["/sbin/tini", "--", "java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
