# --- STAGE 1: Build the Application (Builder Stage) ---
# Use a JDK image suitable for compiling/packaging the application
FROM eclipse-temurin:17-jdk-focal AS builder

# Set the working directory inside this stage
WORKDIR /app

# Copy the packaged JAR file that you created in Step 1
# Assuming your JAR is in the 'target' directory and is named 'my-springboot-app.jar'
# Adjust the path/name if yours is different (e.g., build/libs/ for Gradle)
COPY target/nebulak-0.0.1-SNAPSHOT.jar /app/nebulak-0.0.1-SNAPSHOT.jar

# --- STAGE 2: Create the Final Image (Runner Stage) ---
# Use a minimal JRE image (no development tools) for running the application
FROM eclipse-temurin:17-jre-focal

# Set the working directory
WORKDIR /app

# The port your Spring Boot app is configured to run on (default is 8080)
EXPOSE 8080

# Copy the packaged JAR file from the builder stage
COPY --from=builder /app/nebulak-0.0.1-SNAPSHOT.jar /app/app.jar

# Define the command to run the application
# The use of '-jar' runs the embedded web server
ENTRYPOINT ["java", "-jar", "app.jar"]