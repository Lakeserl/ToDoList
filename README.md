# Hướng dẫn chạy dự án

## 1. Yêu cầu hệ thống

- Docker và Docker Compose

## 2. Khởi động ứng dụng (Docker Compose)

Chạy lệnh sau tại thư mục gốc của dự án:

```bash
docker compose up --build
```

Sau khi khởi động thành công:
- Giao diện người dùng (Frontend): http://localhost:4200
- Cổng kết nối API (Backend): http://localhost:8080/api/todos
- Swagger UI (Tài liệu API): http://localhost:8080/swagger-ui.html

## 3. Hướng dẫn chạy các bộ Test

### Chạy Unit & Integration Test (Backend)

```bash
docker run --rm -v ~/.m2:/root/.m2 -v "$(pwd)/backend:/app" -w /app eclipse-temurin:21-jdk ./mvnw clean test
```

### Chạy Unit Test (Frontend)

```bash
cd frontend
npm install
npm run test -- --watch=false
```

### Chạy E2E Test (Playwright)

Đảm bảo ứng dụng đang chạy trước khi thực hiện bộ test E2E:

```bash
cd frontend
npm install
npx playwright install chromium
npm run e2e
```
