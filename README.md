# 🔐 Authentication System

A full-stack authentication application built with **Spring Boot**, **Spring Security**, **JWT**, and **React**. Provides secure registration, email verification, login with brute-force protection, password recovery, refresh-token rotation, and role-based access control.

---

## ✨ Features

### Backend
- User registration with email domain validation (live MX record lookup)
- Email verification via signed one-time tokens
- Login with JWT access tokens
- Rotating, revocable refresh tokens (stored server-side, not stateless JWTs)
- Account lockout after 5 failed login attempts (auto-unlocks after 15 minutes)
- Forgot password / reset password flow
- Change password (authenticated)
- Role-based authorization (`SUPER_ADMIN`, `ADMIN`, `USER`) with fine-grained permissions
- Global exception handling with consistent JSON error responses
- BCrypt password hashing
- Database migrations via Flyway
- OpenAPI/Swagger documentation

### Frontend
- Registration, login, and email verification pages
- Forgot password / reset password / reset-success pages
- Client-side form validation (React Hook Form + Zod)
- Axios-based API layer

> **In progress:** dashboard/profile pages and route-level auth guards exist as components but aren't yet wired into the router, and the frontend doesn't yet auto-attach the JWT to authenticated requests. See [Roadmap](#-roadmap).

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 4.1, Spring Security, Spring Data JPA |
| Auth | JJWT, BCrypt, opaque refresh tokens |
| Database | MySQL, Flyway migrations |
| Email | Spring Mail (SMTP), dnsjava (MX validation) |
| API Docs | springdoc-openapi (Swagger UI) |
| Frontend | React 19, React Router, React Hook Form, Zod, Tailwind CSS, Axios |
| Tooling | Maven, ESLint, Vite |

---

## 📂 Project Structure

```
authentication-system/
├── backend/
│   ├── src/main/java/com/aathi/authenticationsystem/
│   │   ├── configuration/   # Security, JWT, cookie config
│   │   ├── controller/      # REST controllers (auth, user, admin)
│   │   ├── dto/              # Request/response records
│   │   ├── enums/            # Role, Permissions, VerificationStatus
│   │   ├── exception/        # Custom exceptions + global handler
│   │   ├── models/            # JPA entities
│   │   ├── repository/       # Spring Data repositories
│   │   ├── security/          # JWT filter, cookie service, user details
│   │   └── service/            # Business logic
│   └── src/main/resources/
│       ├── application.properties
│       └── db/migration/       # Flyway SQL migrations
└── frontend/
    └── src/
        ├── api/                # Axios instance + auth API calls
        ├── features/auth/      # Auth pages & components
        ├── features/user/      # User pages (dashboard, profile)
        └── routes/             # React Router config
```

---

## 🔄 Authentication Flow

1. User registers through the React frontend.
2. Backend validates the email domain (MX lookup) and sends a verification email.
3. User clicks the verification link; the backend marks the account as enabled and redirects to the frontend.
4. User logs in with verified credentials.
5. Backend issues a JWT **access token** (returned in the response body) and a **refresh token** (set as an HttpOnly cookie scoped to `/api/auth`).
6. Frontend uses the access token to call protected APIs.
7. When the access token expires, the frontend calls `/api/auth/refresh`; the backend validates and **rotates** the refresh token (old one is revoked, a new one issued).
8. Users can request a password reset via email, or change their password while logged in.
9. After 5 failed login attempts, the account is locked for 15 minutes.

---

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Node.js & npm
- MySQL
- Maven (or the bundled `mvnw` wrapper)
- A Gmail account with an [App Password](https://myaccount.google.com/apppasswords) for sending email (or another SMTP provider)

### Clone the Repository

```bash
git clone https://github.com/aathi1412/authentication-system.git
```

### Backend Setup

Create the database:

```sql
CREATE DATABASE authentication_system;
```

Set the required environment variables (see [Environment Variables](#-environment-variables) below), then:

```bash
cd backend
./mvnw spring-boot:run
```

Flyway will automatically create the schema and seed a default super-admin account on first run.

### Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

The frontend runs on `http://localhost:5173`; the backend on `http://localhost:8080`.

---

## 🔑 Environment Variables

**Do not commit real credentials to `application.properties`.** Set these as environment variables instead:

| Variable | Description |
|---|---|
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |
| `JWT_SECRET` | Secret key used to sign access tokens (long, random string) |
| `MAIL_USERNAME` | SMTP username (e.g. your Gmail address) |
| `MAIL_PASSWORD` | SMTP password (e.g. a Gmail App Password, **not** your account password) |

Update `application.properties` to reference them, e.g.:

```properties
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
jwt.secret-key=${JWT_SECRET}
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
```

> If you've previously committed real secrets to this repo, rotate them (new Gmail App Password, new DB password) and scrub git history with `git filter-repo` or BFG — pushing a fix on top isn't enough, since the old values remain in earlier commits.

---

## 📌 API Reference

Base URL: `http://localhost:8080`

### Auth (`/api/auth`) — public
| Method | Endpoint | Description |
|---|---|---|
| POST | `/register` | Register a new user |
| POST | `/login` | Log in, returns access token + sets refresh token cookie |
| POST | `/refresh` | Rotate refresh token, get a new access token |
| GET | `/verify-email?token=` | Verify email via link, redirects to frontend |
| POST | `/resend-verification-email` | Resend the verification email |
| POST | `/forgot-password` | Request a password reset email |
| POST | `/reset-password` | Reset password using a reset token |

### Auth (`/api/auth`) — authenticated
| Method | Endpoint | Description |
|---|---|---|
| POST | `/logout` | Revoke the current refresh token |
| POST | `/change-password` | Change password for the logged-in user |

### User (`/api/user`) — authenticated
| Method | Endpoint | Description |
|---|---|---|
| GET | `/home` | Home page placeholder |
| GET | `/dashboard` | Dashboard placeholder |
| GET | `/about` | About placeholder |
| GET | `/contact` | Contact placeholder |

### Admin (`/api/admin`) — `ADMIN` / `SUPER_ADMIN` only
| Method | Endpoint | Description |
|---|---|---|
| GET | `/home` | Admin home placeholder |
| POST | `/register` | Register a new admin account |
| PUT | `/update/user` | Placeholder — not yet implemented |

---

## 🔒 Security Features

- Stateless JWT access tokens (short-lived, 15 min default)
- Opaque, rotating refresh tokens stored server-side and revocable on logout
- Refresh token delivered as an HttpOnly, `SameSite=Strict` cookie scoped to `/api/auth`
- BCrypt password hashing
- Email verification required before login
- Account lockout after repeated failed logins
- MX-record domain validation on registration
- Role- and permission-based authorization via Spring Security

---

## 🧪 Testing

Use Postman or Swagger UI (`http://localhost:8080/swagger-ui.html`) to exercise the API. Suggested scenarios:

- Register → verify email → login
- Login with invalid credentials 5 times → confirm lockout → wait 15 min → confirm unlock
- Forgot password → reset password → login with new password
- Refresh an access token and confirm the old refresh token is rejected afterward
- Access `/api/admin/**` as a `USER` role and confirm a 403

---

## 🔮 Roadmap

- [ ] Wire dashboard/profile pages into the router with a route guard
- [ ] Add an axios interceptor to attach the access token and handle silent refresh on 401
- [ ] Move DB and mail credentials fully to environment variables
- [ ] OAuth2 login (Google/GitHub)
- [ ] Two-factor authentication (2FA)
- [ ] Docker support
- [ ] Redis-backed token blacklisting
- [ ] CI/CD pipeline
- [ ] Cloud deployment (AWS/Azure)

---

## 👨‍💻 Author

Aathi

If you found this project helpful, feel free to ⭐ the repository.
