FROM maven:3.8.1-openjdk-17 AS build
WORKDIR /usr/src/app
COPY ./src/ ./src/
COPY ./pom.xml .
RUN mvn clean package

FROM openjdk:17
COPY --from=build /usr/src/app/target/project-backend-0.0.1-SNAPSHOT.jar target/project-backend-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/project-backend-0.0.1-SNAPSHOT.jar"]