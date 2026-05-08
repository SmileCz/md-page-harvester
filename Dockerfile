FROM eclipse-temurin:25-jdk AS build

WORKDIR /workspace
COPY . .
RUN ./gradlew :apps:service:shadowJar --no-daemon

FROM eclipse-temurin:25-jre

WORKDIR /app
RUN useradd --create-home --shell /bin/bash app \
    && mkdir -p /data/pages \
    && chown -R app:app /app /data

COPY --from=build /workspace/apps/service/build/libs/md-page-harvester.jar /app/app.jar

USER app

ENV MICRONAUT_ENVIRONMENTS=docker
ENV HTTP_PORT=8080

EXPOSE 8080
VOLUME ["/data"]

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
