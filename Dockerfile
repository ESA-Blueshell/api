FROM maven:3.6.1-jdk-11-slim AS build
WORKDIR /home/app

COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f pom.xml clean package

FROM openjdk:11-jre-slim
COPY --from=build /home/app/target/api-0.0.1-SNAPSHOT.jar /usr/local/lib/api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/api.jar"]


