# Build stage
FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Build application
COPY src ./src
RUN mvn clean package -DskipTests && \
    mv target/*.jar target/app.jar

# Runtime stage
FROM eclipse-temurin:23-jre
WORKDIR /app
COPY --from=build /app/target/app.jar ./app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]