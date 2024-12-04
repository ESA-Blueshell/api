FROM maven:3.6.1-jdk-11-slim
WORKDIR /usr/app
COPY pom.xml .
COPY src ./src
EXPOSE 8080
CMD ["mvn", "clean", "spring-boot:run"]
