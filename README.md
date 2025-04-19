# OTPAuth - Email OTP Authentication System

## Project Overview

OTPAuth is a Spring Boot based authentication system that implements a secure email OTP (One-Time Password) workflow for
user registration and login. Instead of traditional password-based authentication, users receive verification codes via
email that they can exchange for access tokens. The system uses JWT (JSON Web Token) for secure authentication and
integrates Redis for efficient caching of verification codes and session management.

## Technology Stack

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Redis
- Thymeleaf
- Java Mail Sender
- JWT (Java-JWT 4.5.0)
- Lombok
- Docker Compose

## Features

- **Email-based OTP Authentication Flow**:
    - User initiates registration/login by providing email
    - System generates a time-limited verification code
    - Verification code is sent to user's email using customizable Thymeleaf templates
    - User submits email + verification code to obtain JWT access token
    - No passwords required

- **Secure Token Management**:
    - JWT-based access tokens with configurable expiration
    - Refresh token support for extended sessions
    - Token validation and verification endpoints

- **Redis Integration**:
    - Efficient storage and retrieval of verification codes
    - Automatic code expiration handling

- **Security Features**:
    - Rate limiting for OTP requests
    - Verification code validation with custom validators
    - Secure token exchange

## Project Structure

```
OTPAuth
├── src/main/java/fans/goldenglow/otpauth
│   ├── config
│   │   ├── RedisConfig
│   │   └── SecurityConfig
│   ├── controller
│   │   ├── AuthController
│   │   ├── TokenController
│   │   └── UserController
│   ├── dto
│   ├── model
│   ├── repository
│   ├── service
│   └── validation
├── src/main/resources
│   ├── static
│   ├── templates
│   └── application.yml
```

## Getting Started

### Prerequisites

- Java 21
- Docker and Docker Compose (for running PostgreSQL and Redis)

### Build and Run

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/OTPAuth.git
   cd OTPAuth
   ```

2. Start the dependent services with Docker Compose:
   ```bash
   docker-compose up -d
   ```

3. Build and run the application with Gradle:
   ```bash
   ./gradlew bootRun
   ```

4. The application will be running at `http://localhost:8080`

## API Endpoints

### Authentication

- `POST /api/v1/auth/request-verification` - Request an email verification code
  ```json
  {
    "email": "user@example.com"
  }
  ```

- `POST /api/v1/auth/token` - Exchange verification code for access and refresh tokens
  ```json
  {
    "email": "user@example.com",
    "verification_code": "123456"
  }
  ```
  Response:
  ```json
  {
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
  ```

### Token Management

- `POST /api/v1/auth/token/refresh` - Refresh an access token using refresh token
  ```json
  {
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
  ```

- `POST /api/v1/auth` - Verify if a token is valid (protected endpoint)

### User Management

- `GET /api/v1/user/me` - Get current user profile (protected endpoint)

## Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

[AGPL-3.0](https://www.gnu.org/licenses/agpl-3.0.html)