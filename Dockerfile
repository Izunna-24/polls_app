LABEL authors="izunna"

FROM maven:3.8.7 as build
COPY . .
RUN mvn -B clean package -DskipTests
FROM openjdk:17
COPY --from=build target/*.jar polls.jar
ENTRYPOINT ["java", "-jar", "-Dserver.port=5000", "polls_app.jar"]