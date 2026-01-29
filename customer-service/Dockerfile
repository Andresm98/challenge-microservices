# Etapa 1: Compilación
FROM maven:3.9.5-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copiar el pom y descargar dependencias (optimiza caché)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Imagen ligera)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copiar solo el JAR resultante de la etapa anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]