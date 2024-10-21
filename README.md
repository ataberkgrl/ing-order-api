# ING Order API

## Project Structure

The project is organized into two main modules:

1. `domain`: Contains the core business logic and domain models.
2. `infrastructure`: Implements the infrastructure layer, including REST controllers, JPA repositories, and security configurations.

### Key Components

#### Domain Module

- `order`: Handles order-related operations (create, cancel, list)
- `asset`: Manages asset-related operations (deposit, withdraw, list)
- `user`: Defines user-related models and interfaces

#### Infrastructure Module

- `adapters`: Implements the infrastructure-specific adapters for orders, assets, and users
- `authentication`: Handles user authentication and JWT token management
- `config`: Contains configuration classes for security and Swagger
- `exception`: Defines global exception handling

## Getting Started

### Prerequisites

- Java 17
- Docker

### Building and Running the Application

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/ing-order-api.git
   cd ing-order-api
   ```

2. Build the application:
   ```
   ./gradlew build
   ```

3. Build the Docker image:
   ```
   docker build -t ing-order-api .
   ```

4. Run the Docker container:
   ```
   docker run -p 8080:8080 \
     -e JWT_SECRET=ing-bank-8f3fa72b8dfc9d2e1f5765a0d2c8b162fc9d2e1f5765a0d2c8b162f \
     -e ADMIN_USERNAME=<admin-username> \
     -e ADMIN_PASSWORD=<admin-password> \
     ing-order-api
   ```

   Replace `<admin-username>` and `<admin-password>` with your desired admin credentials.

The API will be available at `http://localhost:8080`

## API Documentation

Swagger UI is available at `http://localhost:8080/swagger-ui.html`

## Security

The API implements two types of authentication:

### 1. JWT Authentication for Customers

1. Register a new user: `POST /api/auth/register`
2. Login to obtain a JWT token: `POST /api/auth/login`
3. Include the JWT token in the `Authorization` header for subsequent requests:
   ```
   Authorization: Bearer <your-jwt-token>
   ```

### 2. Basic Authentication for Admin Access

Admin users can access protected endpoints using HTTP Basic Authentication:

1. Include an `Authorization` header with Base64 encoded credentials:
   ```
   Authorization: Basic <base64-encoded-credentials>
   ```
   Where `<base64-encoded-credentials>` is the Base64 encoding of `<admin-username>:<admin-password>`.

2. The admin username and password are set through the environment variables `ADMIN_USERNAME` and `ADMIN_PASSWORD` when running the Docker container.

Note: Ensure you use HTTPS in production to secure the transmission of credentials.

## Testing

To run tests:

```
./gradlew test
```