# Sử dụng OpenJDK 17 làm image cơ sở
FROM eclipse-temurin:17-jdk-jammy

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép file .jar đã được build từ thư mục target vào container
COPY target/*.jar app.jar

# Mở cổng 8080 và chỉ định lệnh để chạy ứng dụng
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]