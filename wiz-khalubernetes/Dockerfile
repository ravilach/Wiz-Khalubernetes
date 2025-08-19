# Multi-stage Dockerfile for Wiz Khalubernetes
# Build frontend
FROM node:20 AS frontend-build
WORKDIR /app/frontend
COPY frontend/package.json frontend/yarn.lock* frontend/ .
RUN npm install --legacy-peer-deps
COPY frontend/ .
RUN npm run build

# Build backend
FROM eclipse-temurin:17-jdk AS backend-build
WORKDIR /app/backend
RUN apt-get update && apt-get install -y maven
COPY backend/pom.xml ./
COPY backend/src ./src
RUN mvn package -DskipTests

# Final image
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=backend-build /app/backend/target/*.jar app.jar
COPY --from=frontend-build /app/frontend/build ./frontend/build
COPY wizexercise.txt ./wizexercise.txt
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-jar", "app.jar"]
