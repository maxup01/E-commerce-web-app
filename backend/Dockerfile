FROM openjdk:23-jdk AS build

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY src src

RUN chmod +x mvnw
RUN ./mvnw clean package


FROM eclipse-temurin:23-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]
