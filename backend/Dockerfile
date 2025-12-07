FROM maven:3 AS build

WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:21-ea-21-slim-buster

ENV DATASOURCE_URL=
ENV DATASOURCE_USERNAME=
ENV DATASOURCE_PASSWORD=
ENV ALLOWED_ORIGINS=
ENV SECRET_KEY=

WORKDIR /app

COPY --from=build /app/target/**.jar ./target/PersonalSystem.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/target/PersonalSystem.jar"]