FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy project files
COPY . .

# Ensure mvnw is executable (Windows → Linux fix)
RUN chmod +x mvnw

# Build the Spring Boot jar
RUN ./mvnw clean package -DskipTests

# Copy the generated jar to a fixed name
RUN cp target/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
