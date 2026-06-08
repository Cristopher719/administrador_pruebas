FROM eclipse-temurin:21-jdk-alpine
COPY "./target/parcial_java-0.0.1-SNAPSHOT.jar" "app.jar"
EXPOSE 8121
ENTRYPOINT ["java", "-Dserver.port=8121", "-jar", "app.jar"]
