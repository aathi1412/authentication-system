🔐 Authentication System

A full-stack authentication application built with Spring Boot, Spring Security, JWT, and React. The application provides secure user authentication through email verification, password recovery, refresh tokens, and account protection mechanisms while offering a responsive user interface for seamless user interactions.

---

✨ Features

Backend

- User Registration & Login
- JWT Authentication
- Refresh Token Support
- Email Verification
- Forgot Password
- Password Reset
- Account Lock After Multiple Failed Login Attempts
- Role-Based Authorization
- RESTful API Architecture
- Request Validation
- Global Exception Handling
- Password Encryption using BCrypt

Frontend

- Responsive Authentication Interface
- User Registration
- Login
- Email Verification Page
- Forgot Password Page
- Reset Password Page
- Password Reset Success Page
- Protected Routes
- JWT Token Management
- API Integration using Axios
- Client-side Form Validation

---

🛠️ Tech Stack

Backend

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate

Frontend

- React
- React Router
- Axios
- HTML
- CSS

Database

- MySQL

Security

- JWT (JSON Web Token)
- BCrypt Password Encoder

Tools

- Maven
- Git
- GitHub
- Postman
- IntelliJ IDEA
- VS Code

---

📂 Project Structure

AuthenticationSystem
│
├── backend
│   ├── src
│   ├── pom.xml
│   └── ...
│
├── frontend
│   ├── src
│   ├── public
│   ├── package.json
│   └── ...
│
└── README.md

---

🔄 Authentication Flow

1. User registers through the React frontend.
2. A verification email is sent to the registered email address.
3. User verifies the account using the verification link.
4. User logs in with valid credentials.
5. The backend generates an Access Token and Refresh Token.
6. The frontend uses the Access Token to access protected APIs.
7. When the Access Token expires, a new one is generated using the Refresh Token.
8. Users can reset forgotten passwords via email.
9. After multiple failed login attempts, the account is temporarily locked for enhanced security.

---

📌 API Modules

Authentication

- Register User
- Login
- Verify Email
- Refresh Token
- Logout

Password Management

- Forgot Password
- Reset Password

User

- Get User Profile
- Update User Profile

---

🔒 Security Features

- Stateless Authentication with JWT
- Secure Password Hashing using BCrypt
- Email Verification Before Account Activation
- Refresh Token Mechanism
- Role-Based Authorization
- Protected REST Endpoints
- Account Lock After Repeated Failed Login Attempts
- Request Validation
- Global Exception Handling

---

🚀 Getting Started

Prerequisites

- Java 21+
- Node.js & npm
- MySQL
- Maven
- Git

Clone the Repository

git clone https://github.com/your-username/AuthenticationSystem.git

Backend Setup

cd backend
mvn spring-boot:run

Frontend Setup

cd frontend
npm install
npm run dev

Configure Database

Update the database configuration in "application.properties".

spring.datasource.url=jdbc:mysql://localhost:3306/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password

---

🧪 Testing

Use Postman to test backend APIs.

Recommended test scenarios:

- Register a new user
- Verify email
- Login with valid credentials
- Login with invalid credentials
- Trigger account lock after repeated failed login attempts
- Forgot Password
- Reset Password
- Refresh JWT Token
- Access protected endpoints using JWT

---

📚 Concepts Demonstrated

- Spring Boot
- Spring Security
- JWT Authentication
- Authentication & Authorization
- REST API Development
- React
- React Router
- Axios
- Spring Data JPA
- Hibernate
- MySQL
- Layered Architecture
- DTO Pattern
- Dependency Injection
- Bean Validation
- Exception Handling
- Password Encryption
- Token-Based Security

---

🔮 Future Enhancements

- OAuth2 Login (Google/GitHub)
- Two-Factor Authentication (2FA)
- User Profile Management
- Remember Me Functionality
- Docker Support
- Redis Token Blacklisting
- Swagger/OpenAPI Documentation
- CI/CD Pipeline
- Cloud Deployment (AWS/Azure)

---

👨‍💻 Author

Aathi

If you found this project helpful, feel free to ⭐ the repository.
