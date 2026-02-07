# Spring Boot Demo Project

This is a comprehensive Spring Boot application demonstrating a secure, scalable, and modern API architecture. It features JWT-based authentication, user management, product management, and PostgreSQL integration.

## 🚀 Tech Stack

*   **Java**: 25
*   **Spring Boot**: 4.0.2
*   **Database**: PostgreSQL (Production), H2 (Test)
*   **Security**: Spring Security, JWT (Access & Refresh Tokens)
*   **Documentation**: OpenAPI (Swagger UI)
*   **Containerization**: Docker

## ✨ Key Features

*   **Authentication & Authorization**:
    *   Secure Registration and Login using Email.
    *   Stateless Session Management via JWT.
    *   **Refresh Token Rotation** for enhanced security.
*   **User Management**:
    *   Profile updates (First Name, Last Name) via secure endpoints.
    *   Data preservation using secure schema updates (`ddl-auto=update`).
*   **Product Management**:
    *   CRUD operations for Products.
*   **Database Integration**:
    *   Production-ready PostgreSQL setup.
    *   Auto-migration of database schema.

## 🛠️ Prerequisites

*   Java JDK 25
*   Docker (optional, for containerized deployment)

## ⚙️ Installation & Setup

1.  **Clone the repository**
    ```bash
    git clone <repository-url>
    cd demo
    ```

2.  **Configuration**
    The application is pre-configured to connect to a remote PostgreSQL database. You can view or modify settings in `src/main/resources/application.properties`.

## 🏃‍♂️ Running the Application

### Option 1: Using Maven Wrapper (Local)

```bash
./mvnw spring-boot:run
```
The application will start on `http://localhost:8080`.

### Option 2: Using Docker

1.  **Build the Image**
    ```bash
    docker build -t spring-boot-demo .
    ```

2.  **Run the Container**
    ```bash
    docker run -p 8080:8080 spring-boot-demo
    ```

## 📚 API Documentation

The API is fully documented using Swagger UI. Once the application is running, you can explore and test the endpoints at:

👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

### Key Endpoints

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register a new user | ❌ |
| `POST` | `/api/auth/login` | Login and receive Access/Refresh tokens | ❌ |
| `POST` | `/api/auth/refresh-token` | Obtain a new Access Token | ❌ |
| `PUT` | `/api/users/profile` | Update user profile (First/Last Name) | ✅ |
| `GET` | `/products` | List all products | ✅ |

## 🧪 Testing

Run unit and integration tests using Maven:

```bash
./mvnw test
```

## 📝 License

This project is open-source and available under the MIT License.
