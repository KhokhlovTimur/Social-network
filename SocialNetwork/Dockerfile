FROM maven:3.8.7-eclipse-temurin-19 as build

WORKDIR /app
COPY src /app/src
COPY pom.xml /app

RUN --mount=type=cache,target=/.m2 mvn -f /app/pom.xml clean package

FROM openjdk:19-jdk-alpine
COPY --from=build /app/target/app.jar /usr/local/lib/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/app.jar"]