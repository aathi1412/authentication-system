# 🔐 Identity & Access Management (IAM) Authentication System

A production-ready full-stack Identity and Access Management platform built with **Spring Boot 4**, **Spring Security 7**, **JJWT**, **MySQL 8**, and **React 19** with Tailwind CSS and Radix UI / shadcn components.

---


## 🚀 Key Features

### Backend Architecture
- **JWT & Opaque Refresh Tokens:** Short-lived signed JWT access tokens with rotating, database-backed opaque refresh tokens delivered via secure `HttpOnly` cookies.
- **Brute-Force & Lockout Protection:** 5 consecutive failed login attempts trigger an automated account lockout (configurable duration).
- **Email Domain & MX Validation:** Real-time DNS MX record verification on user registration preventing invalid domain signups.
- **HTML Email Templates:** Responsive, sanitized email delivery for activation and password reset tokens via Spring Mail.
- **Fine-Grained Role & Permission Model:** `SUPER_ADMIN`, `ADMIN`, and `USER` roles with granular permission checks (`@PreAuthorize` and method security).
- **Profile Image Storage:** Safe avatar upload pipeline with MIME verification, path-traversal prevention, unique filename hashing, and public CDN-style serving.
- **Admin Management API:** Real-time stats aggregation, user search/filtering, lock/unlock, role reassignment with safeguards against demoting the last super-admin, and session revocation.
- **Flyway Migrations:** Version-controlled database schema management with automated super-admin bootstrapping.
- **Robust Exception Handling:** Standardized `ApiResponse` payload with field-level validation errors, security error handlers, and zero information leakage on internal server errors.

### Frontend Features
- **Modern React 19 SPA:** Built with Vite, Tailwind CSS, Lucide icons, and Radix UI / shadcn primitives.
- **Role-Aware Routing:** Strict route guards distinguishing `USER`, `ADMIN`, and `SUPER_ADMIN` areas with automatic redirection.
- **Admin Dashboard:** Real-time KPI statistics cards (Total Users, Verified Rate, Locked Accounts, Active Sessions, 24h Signups) and quick action panels.
- **User Management Console:** Paginated directory with server-side name/email search, role/status filtering, lock/unlock toggles, role change dialogs, session revocation, and soft deletion.
- **Account Center:** Profile management with live avatar upload and preview, security status overview, password change, and paginated activity audit logs.
- **Unified Toast Notifications:** Consolidated notification pipeline using shadcn/ui toast system with promise tracking.
- **Automatic Silent Token Refresh:** Centralized Axios client with request queuing and automatic token renewal on HTTP 401.

---

## 🛠️ Technology Stack

| Component | Technology | Version |
|---|---|---|
| **Backend** | Java | 21 (LTS) |
| **Framework** | Spring Boot | 4.1.0 |
| **Security** | Spring Security, JJWT | 7.x, 0.12.7 |
| **Database** | MySQL | 8.0 |
| **Migrations**| Flyway | Integrated |
| **Frontend** | React, Vite | 19, 6.x |
| **Styling** | Tailwind CSS, Radix UI | 3.4.x |
| **Forms** | React Hook Form, Zod | 7.x, 3.x |
| **Container** | Docker, Docker Compose, Nginx | Multi-stage |

---

## Authentication Flow

1. User registers through the React frontend.
2. Backend validates the email domain (MX lookup) and sends a verification email.
3. User clicks the verification link; the backend marks the account as enabled and redirects to the frontend.
4. User logs in with verified credentials.
5. Backend issues a JWT **access token** (returned in the response body) and a **refresh token** (set as an HttpOnly cookie scoped to `/api/auth`).
6. Frontend uses the access token to call protected APIs.
7. When the access token expires, the frontend calls `/api/auth/refresh`; the backend validates and **rotates** the refresh token (old one is revoked, a new one issued).
8. Users can request a password reset via email, or change their password while logged in.
9. After 5 failed login attempts, the account is locked for 15 minutes.
## 🔑 Environment Variables

Configure these settings in `backend/.env` (or root `.env` for Docker):

| Variable | Description | Default / Example |
|---|---|---|
| `DB_URL` | JDBC Connection URL | `jdbc:mysql://localhost:3306/authentication_system` |
| `DB_USERNAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | `your_database_password` |
| `JWT_SECRET` | 256-bit+ key for JWT signing | `404E635266556A586E3272357538782F413F4428472B...` |
| `FRONTEND_URL` | Public frontend URL | `http://localhost:5173` (or `http://localhost`) |
| `BACKEND_URL` | Public backend URL | `http://localhost:8080` |
| `UPLOAD_DIR` | Filesystem avatar upload path | `./uploads/avatars` |
| `COOKIE_SECURE`| Set to `true` for production HTTPS | `false` |
| `MAIL_HOST` | SMTP server host | `smtp.gmail.com` |
| `MAIL_PORT` | SMTP port | `587` |
| `MAIL_USERNAME`| SMTP account username | `user@example.com` |
| `MAIL_PASSWORD`| SMTP account app password | `app_password_here` |

---

## Getting Started

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
## 🏃 Running the Application

### Option A: Running with Docker Compose (Recommended)

1. Copy the example environment file:
   ```bash
   cp .env.example .env
   ```
2. Start the entire containerized stack:
   ```bash
   docker compose up --build -d
   ```
3. Access the services:
   - **Frontend application:** `http://localhost`
   - **Backend API & Swagger:** `http://localhost:8080/swagger-ui.html`
   - **MySQL Database:** `localhost:3306`

### Option B: Running Locally for Development

#### 1. MySQL Database
Ensure a local MySQL instance is running and create the database:
```sql
CREATE DATABASE authentication_system;
```

#### 2. Backend
Navigate to the `backend/` directory:
```bash
cd backend
cp .env.example .env
# Edit .env with your local MySQL credentials
./mvnw clean spring-boot:run
```
The backend starts on `http://localhost:8080`.

#### 3. Frontend
Navigate to the `frontend/` directory in a new terminal:
```bash
cd frontend
npm install
npm run dev
```
The frontend starts on `http://localhost:5173`.

---

## 📌 API Reference

### 1. Authentication Endpoints (`/api/auth`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register new user with MX email check |
| `POST` | `/api/auth/login` | Public | Authenticate user, returns access token + sets refresh cookie |
| `POST` | `/api/auth/refresh` | Public (Cookie) | Rotates refresh token and returns new access token |
| `POST` | `/api/auth/logout` | Authenticated | Revokes active refresh token and clears cookie |
| `GET` | `/api/auth/activate-account` | Public | Activates account via email verification token |
| `POST` | `/api/auth/resend-verification-email`| Public | Issues a fresh verification email token |
| `POST` | `/api/auth/forgot-password` | Public | Sends password reset email |
| `POST` | `/api/auth/reset-password` | Public | Resets password using single-use reset token |
| `POST` | `/api/auth/change-password` | Authenticated | Updates password for authenticated user |

### 2. User & Account Endpoints (`/api/users`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/users/profile` | Authenticated | Get current user profile details |
| `PUT` | `/api/users/profile` | Authenticated | Update user name, phone, bio |
| `POST` | `/api/users/profile/image` | Authenticated | Upload profile avatar (JPEG, PNG, WebP <= 5MB) |
| `GET` | `/api/users/profile/image/{filename}`| Public | Fetch profile avatar image |
| `GET` | `/api/users/security` | Authenticated | Security status (verification, 2FA, lockout) |
| `GET` | `/api/users/activity` | Authenticated | Paginated audit trail of account events |

### 3. Admin Endpoints (`/api/admin`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/admin/stats` | `ADMIN`, `SUPER_ADMIN` | Aggregated metrics (total, verified, locked, sessions, new) |
| `GET` | `/api/admin/users` | `ADMIN`, `SUPER_ADMIN` | Paginated user listing with search and role/status filters |
| `GET` | `/api/admin/users/{id}` | `ADMIN`, `SUPER_ADMIN` | Detailed user record |
| `PUT` | `/api/admin/users/{id}/lock` | `ADMIN`, `SUPER_ADMIN` | Administratively lock user account and revoke sessions |
| `PUT` | `/api/admin/users/{id}/unlock` | `ADMIN`, `SUPER_ADMIN` | Unlock user and reset failed login attempts |
| `PUT` | `/api/admin/users/{id}/role` | `ADMIN`, `SUPER_ADMIN` | Change role (protected against last super-admin demotion) |
| `POST` | `/api/admin/users/{id}/revoke-sessions` | `ADMIN`, `SUPER_ADMIN` | Revoke all refresh tokens for user |
| `DELETE` | `/api/admin/users/{id}` | `ADMIN`, `SUPER_ADMIN` | Soft-disable user account |

---

## 🧪 Automated Testing

### Backend Unit & Integration Tests
Run all unit and integration tests:
```bash
cd backend
./mvnw test
```
Package production artifact:
```bash
./mvnw clean package
```

### Frontend Build & Lint Verification
Run ESLint check:
```bash
cd frontend
npm run lint
```
Build production bundle:
```bash
npm run build
```

---

## 🔒 Security Architecture Highlights

1. **HttpOnly Cookies:** Refresh tokens are never exposed to client-side JavaScript, strictly scoped to `/api/auth` with `SameSite=Strict`.
2. **Token Rotation & Reuse Detection:** Every refresh operation revokes the old token and issues a new one. If an already-revoked token is presented, the system detects potential token theft.
3. **Defense in Depth Authorization:** Authorization is enforced at the controller and method level with `@EnableMethodSecurity` and `@PreAuthorize`, backed by granular authorities.
4. **Brute Force Defense:** Accounts are locked upon 5 failed attempts, preventing automated dictionary and credential stuffing attacks.
5. **Zero Information Leakage:** Production error handling masks stack traces and internal exceptions, providing clean, actionable error messages.
