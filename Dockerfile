 
# Use a base image with OpenJDK and install Maven
FROM maven:3.8.6-openjdk-17-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the project files into the container
COPY . /app

# Package the application with Maven
RUN mvn clean package -DskipTests

# Expose port 8080 for the Spring Boot app
EXPOSE 8080

# Run the Spring Boot application (assuming the JAR file is in the target directory)
CMD ["java", "-jar", "target/UserRegistration.jar"]
