# Build stage
FROM eclipse-temurin:25-jdk-jammy AS build
WORKDIR /app

# Copy maven wrapper files
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (this step is cached unless pom.xml changes)
# Note: running dependency:go-offline can sometimes be flaky with some artifacts, 
# but it's good practice. If it fails, standard package command will download them.
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

# Copy the built artifact from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
