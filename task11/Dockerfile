FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B package

FROM eclipse-temurin:17-jre-jammy
RUN groupadd --system events && useradd --system --gid events events
WORKDIR /app
COPY --from=build --chown=events:events /workspace/target/task11-1.0.0.jar app.jar
USER events
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
