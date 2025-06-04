FROM openjdk:17-jdk-slim

# Install required tools for Gradle build
RUN apt-get update && apt-get install -y curl unzip && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy the entire project
COPY . .

# Make gradlew executable
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew clean build -x test

# Expose port
EXPOSE 8081

# Run the application
CMD ["java", "-jar", "build/libs/demo1-docker-0.0.1.jar"]