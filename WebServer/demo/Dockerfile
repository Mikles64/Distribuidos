# Stage 1: build with Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -Dmaven.test.skip=true package


# Stage 2: runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copia el JAR sombreado producido por maven-shade (artifactId-version-shaded.jar)
# Usamos patrón para capturar el archivo terminado en -shaded.jar
COPY --from=build /workspace/target/*-shaded.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java -jar /app/app.jar ${PORT:-8080}"]
