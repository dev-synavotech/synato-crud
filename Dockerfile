# Use a lightweight Python base image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the requirements file and install dependencies
COPY target/*.jar app.jar

# Expose the port the application will listen on
EXPOSE 8000

# Define the command to run when the container starts
CMD ["java", "-jar", "app.jar"]