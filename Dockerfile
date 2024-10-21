# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR file into the container
COPY infrastructure/build/libs/infrastructure-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Set environment variables
ENV JWT_SECRET=ing-bank-8f3fa72b8dfc9d2e1f5765a0d2c8b162fc9d2e1f5765a0d2c8b162f
ENV ADMIN_USERNAME=admin
ENV ADMIN_PASSWORD=ing

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]