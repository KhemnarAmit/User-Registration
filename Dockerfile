# Use Maven to build the application
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /Login3
COPY . .
RUN mvn clean package -DskipTests

# Use OpenJDK 17 to run the application
FROM openjdk:17-jdk
WORKDIR /Login3
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
