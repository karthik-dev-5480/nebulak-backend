# --- STAGE 1: Build the Application (Builder Stage) ---
FROM eclipse-temurin:17-jdk-focal AS builder

# Set the working directory
WORKDIR /app

# Copy the Maven project files (pom.xml, etc.) first to leverage Docker's build cache
# This means if only the source code changes, Maven dependencies won't be re-downloaded
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Copy the rest of the source code
COPY src src

# Run the Maven package command to compile and create the JAR/WAR
# The 'target' folder will be created inside the container
RUN ./mvnw clean package -DskipTests

# --- STAGE 2: Create the Final Image (Runner Stage) ---
# Use a minimal JRE image for running the application
FROM eclipse-temurin:17-jre-focal

# Set the working directory
WORKDIR /app

# IMPORTANT: Render expects port 10000 by default. 
# It is recommended that your Spring Boot app reads the $PORT env var.
# If your app is hardcoded to 8080, you must configure 8080 in Render's dashboard.
EXPOSE 8080 

# Copy the packaged JAR file from the builder stage
# The file path is correct IF the artifact name hasn't changed.
# The `target/` directory now exists inside the 'builder' stage container.
COPY --from=builder /app/target/nebulak-0.0.1-SNAPSHOT.jar /app/app.jar

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]