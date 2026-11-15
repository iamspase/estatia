FROM maven:3.10.0-eclipse-temurin-25 AS build
WORKDIR /app
COPY app/pom.xml .
RUN mvn -q dependency:go-offline
COPY app/src ./src
RUN mvn -q package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]