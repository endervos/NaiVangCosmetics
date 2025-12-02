# Giai đoạn 1: Dùng Maven để đóng gói ứng dụng (Build)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Giai đoạn 2: Dùng Java để chạy ứng dụng (Run)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Copy file JAR từ giai đoạn 1 sang giai đoạn 2
COPY --from=build /app/target/NaiVangCosmetics-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]