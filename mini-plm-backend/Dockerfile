FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

# make mvnw executable (Windows → Linux fix)
RUN chmod +x ./mvnw

RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/*.jar"]
