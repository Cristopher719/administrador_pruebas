FROM eclipse-temurin:17-jdk-alpine
COPY "./target/parcial_java-0.0.1-SNAPSHOT.jar" "app.jar"
EXPOSE 8121
ENTRYPOINT ["java", "-jar", "app.jar"]