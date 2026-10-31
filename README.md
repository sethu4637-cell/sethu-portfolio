
# J. Sethuraman – Java Full Stack Portfolio Website

> **A production-ready, fully integrated Java Full Stack personal portfolio website for J. Sethuraman, Junior Full Stack Developer & 2026 B.E. CSE Graduate.**
> Featuring a modern dark-mode responsive frontend, Spring Boot 3.3.5 backend, MySQL database persistence, BCrypt authentication, secure session management, real-time visitor tracking, and an administrative dashboard.

---

## 🏗️ Architecture & Technology Stack

| Layer | Technologies |
|---|---|
| **Frontend** | HTML5 (Semantic, Accessible), CSS3 (Modern Glassmorphism, CSS Custom Properties), Vanilla JavaScript (ES6+, zero heavy framework overhead) |
| **Backend Framework** | Spring Boot 3.3.5, Spring Web (REST Controllers), Spring Data JPA / Hibernate, Spring Security, Jakarta Validation |
| **Database** | MySQL 8.x (Production) / H2 in-memory (Automated Integration Testing) |
| **Authentication & Security** | Form-based session authentication (`JSESSIONID`, `HttpOnly`, `SameSite=Strict`), BCrypt password hashing (cost factor 12), In-memory rate limiting, SHA-256 IP privacy hashing |
| **Build & Packaging** | Apache Maven 3.9+, Java 17/21/25 compatible |

---

## 📁 Project Structure

```
sethu-portfolio/
├── admin/
│   ├── login.html                 # Professional Admin Login page (BCrypt + Spring Security)
│   └── dashboard.html             # Protected Admin Dashboard (stats, message inbox, analytics)
├── assets/
│   ├── images/
│   │   ├── favicon.png            # Site favicon
│   │   └── og-image.png           # Open Graph social share image
│   └── resume.pdf                 # Sethuraman's resume PDF
├── css/
│   └── style.css                  # Modern responsive design tokens, glassmorphism, animations
├── js/
│   └── script.js                  # Frontend interactions, REST contact submission & visitor counter
├── index.html                     # Main Portfolio page (Hero, About, Skills, Projects, Contact, Footer)
├── database-setup.sql             # Standalone MySQL schema initialization script
├── backend/
│   ├── pom.xml                    # Maven configuration with dependencies & frontend resource bundling
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/sethuraman/portfolio/
│   │   │   │   ├── PortfolioApplication.java        # Spring Boot main entry point
│   │   │   │   ├── config/
│   │   │   │   │   ├── SecurityConfig.java          # Spring Security: auth, sessions, CORS, CSRF rules
│   │   │   │   │   ├── WebMvcConfig.java            # Route forwarding & static resource handlers
│   │   │   │   │   └── RateLimiter.java             # In-memory sliding-window spam protection
│   │   │   │   ├── controller/
│   │   │   │   │   ├── ContactController.java       # POST /api/contact (rate-limited, validated)
│   │   │   │   │   ├── VisitorController.java       # POST /api/visitors, GET /api/visitors/count
│   │   │   │   │   └── AdminController.java         # Protected /api/admin/dashboard, messages, stats
│   │   │   │   ├── dto/
│   │   │   │   │   ├── ContactRequest.java          # Server-validated contact submission DTO
│   │   │   │   │   ├── ContactMessageDto.java       # Safe admin response DTO
│   │   │   │   │   ├── VisitorCountDto.java         # Public & admin visitor statistics DTO
│   │   │   │   │   └── DashboardDto.java            # Aggregated admin dashboard summary DTO
│   │   │   │   ├── entity/
│   │   │   │   │   ├── ContactMessage.java          # JPA entity for contact form submissions
│   │   │   │   │   ├── VisitorStat.java             # JPA entity for per-day visit aggregates
│   │   │   │   │   └── VisitorFingerprint.java      # JPA entity for daily deduplication (hashed)
│   │   │   │   ├── exception/
│   │   │   │   │   └── GlobalExceptionHandler.java  # Sanitized JSON errors without stack traces
│   │   │   │   ├── repository/
│   │   │   │   │   ├── ContactMessageRepository.java
│   │   │   │   │   ├── VisitorStatRepository.java
│   │   │   │   │   └── VisitorFingerprintRepository.java
│   │   │   │   └── service/
│   │   │   │       ├── ContactService.java          # Contact persistence & privacy hashing
│   │   │   │       └── VisitorService.java          # Visitor tracking & daily deduplication
│   │   │   └── resources/
│   │   │       ├── application.properties           # Spring Boot configuration with env overrides
│   │   │       ├── schema.sql                       # DDL schema migrations for MySQL
│   │   │       └── static/                          # Bundled frontend assets (HTML, CSS, JS, Admin)
│   │   └── test/
│   │       ├── java/com/sethuraman/portfolio/
│   │       │   └── PortfolioApplicationTests.java   # 15 automated integration tests (100% pass)
│   │       └── resources/
│   │           └── application-test.properties      # Test profile using H2 in-memory database
└── README.md
```

---

## 🗄️ MySQL Database Setup Instructions

### Option 1: Automated Table Creation via Hibernate
1. Open MySQL Workbench or MySQL CLI.
2. Create the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS portfolio_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. Set your credentials in environment variables or `application.properties`:
   ```properties
   DB_URL=jdbc:mysql://localhost:3306/portfolio_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true
   DB_USERNAME=root
   DB_PASSWORD=your_mysql_password
   ```
4. Spring Boot will automatically create and update the required tables (`contact_messages`, `visitor_stats`, `visitor_fingerprints`) via `spring.jpa.hibernate.ddl-auto=update`.

### Option 2: Run the SQL Script Manually
You can execute the provided `database-setup.sql` script directly:
```bash
mysql -u root -p < database-setup.sql
```

---

## 🚀 How to Run the Application

### Method A: From Your IDE (IntelliJ IDEA / Eclipse / VS Code)
1. Open the project folder or the `backend` folder in your IDE.
2. Ensure **Java 17 or higher** is configured as the Project SDK.
3. Locate `backend/src/main/java/com/sethuraman/portfolio/PortfolioApplication.java`.
4. Right-click and choose **Run 'PortfolioApplication'** (or Debug).
5. Open your browser and navigate to:
   - **Portfolio Website:** `http://localhost:8080/`
   - **Admin Login:** `http://localhost:8080/admin/login.html` (or `http://localhost:8080/admin`)
   - **Admin Dashboard:** `http://localhost:8080/admin/dashboard.html`

### Method B: From the Command Line with Maven
Navigate to the `backend/` directory:
```bash
cd backend
mvn spring-boot:run
```

### Method C: Run the Self-Contained Executable JAR
```bash
cd backend
mvn clean package -DskipTests
java -jar target/portfolio-backend-1.0.0.jar
```
The application will launch on port `8080` with the entire frontend pre-packaged inside the JAR!

---

## 🔐 Admin Authentication & Credentials

### Local Development Credentials
- **Username:** `sethu_admin`
- **Password:** `sethu@2026!`
- **Login URL:** `http://localhost:8080/admin/login.html`

### How It Works
- The password is stored as a **BCrypt hash** (cost factor 12) inside `application.properties`:
  ```
  $2a$12$dP7DuEoTaUN/x6oFmEXd3e7q0kjCmktTzLp/GKeMi7RwrwbvdNaRq
  ```
- Passwords are **never** stored in plaintext or exposed in frontend code.
- Authentication uses secure HTTP session cookies (`JSESSIONID`) configured with `HttpOnly=true` and `SameSite=Strict`.
- Concurrent logins are restricted to 1 active session per admin.

### How to Change the Admin Password Securely
1. Generate a new BCrypt hash for your desired password using Spring Security or any BCrypt generator with 12 rounds.
2. In production, pass the new credentials as environment variables without modifying source code:
   ```bash
   export ADMIN_USERNAME="your_new_username"
   export ADMIN_PASSWORD_HASH="$2a$12$your_new_generated_bcrypt_hash"
   ```
3. Alternatively, update `admin.username` and `admin.password.hash` in `backend/src/main/resources/application.properties`.

---

## 🌐 REST API Endpoints

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/contact` | Public | Submits a contact message (validates length, email format; rate-limited to 5/min) |
| `POST` | `/api/visitors` | Public | Records a visit on portfolio load (skips if `X-Admin-Dashboard` header present) |
| `GET` | `/api/visitors/count` | Public | Read-only visitor count for the footer badge |
| `POST` | `/api/admin/login` | Public | Form login endpoint (`username`, `password`), creates authenticated session |
| `POST` | `/api/admin/logout` | Public/Admin | Invalidates session and clears `JSESSIONID` cookie |
| `GET` | `/api/admin/me` | Protected (`ROLE_ADMIN`) | Verifies active admin session and returns username |
| `GET` | `/api/admin/dashboard` | Protected (`ROLE_ADMIN`) | Returns aggregated statistics (total visits, today visits, unique visitors, message list) |
| `GET` | `/api/admin/messages` | Protected (`ROLE_ADMIN`) | Returns all contact messages ordered newest first |
| `POST` | `/api/admin/messages/{id}/read`| Protected (`ROLE_ADMIN`) | Marks an individual contact message as read |
| `GET` | `/api/admin/visitors` | Protected (`ROLE_ADMIN`) | Detailed visitor analytics (read-only) |

---

## 🛡️ Security & Privacy Engineering

1. **Privacy-Conscious Unique Visitor Counting:**
   - Raw IP addresses are **never stored** in the database.
   - For unique daily visitor counting, the client's IP and User-Agent are combined with a daily salt and hashed using **SHA-256**.
   - Only the resulting 64-character hash is temporarily tracked in `visitor_fingerprints` for deduplication.
2. **Contact Spam & Abuse Protection:**
   - Server-side validation using `@NotBlank`, `@Email`, and `@Size(min = 20, max = 5000)`.
   - In-memory sliding-window rate limiter prevents spam attacks (maximum 5 submissions per minute per client).
3. **Session & Cookie Security:**
   - `server.servlet.session.cookie.http-only=true` (mitigates XSS cookie theft).
   - `server.servlet.session.cookie.same-site=strict` (mitigates CSRF vulnerabilities).
   - Session timeout set to 30 minutes of inactivity.
4. **Information Disclosure Prevention:**
   - Centralized `GlobalExceptionHandler` returns structured JSON error payloads without stack traces or internal server details.

---

## 🧪 Automated Testing Results

The test suite runs with an isolated H2 in-memory database using `@SpringBootTest` and `MockMvc`:

```bash
mvn test
```

### Test Summary:
- **Total Tests Run:** 15
- **Failures:** 0
- **Errors:** 0
- **Skipped:** 0
- **Status:** `BUILD SUCCESS`

### Verified Scenarios:
1. `contextLoads`: Spring application context boots cleanly.
2. `adminLogin_successfulWithValidCredentials`: Verifies `sethu_admin` / `sethu@2026!` authenticates with HTTP 200 and sets session.
3. `adminLogin_failsWithInvalidCredentials`: Rejects wrong passwords with HTTP 401.
4. `adminSession_loginAccessDashboardAndLogout`: Tests complete session lifecycle (login → access `/api/admin/dashboard` → verify `/api/admin/me` → logout).
5. `adminDashboard_requiresAuthentication`: Unauthenticated request returns HTTP 401.
6. `adminDashboard_authenticatedAdminCanAccess`: Authenticated admin receives full dashboard JSON.
7. `adminDashboard_nonAdminForbidden`: Non-admin roles receive HTTP 403.
8. `adminMessages_requiresAuthentication`: Unauthenticated message access returns HTTP 401.
9. `adminMessages_authenticatedAdminCanRetrieve`: Authenticated admin retrieves message list.
10. `visitorCount_isPubliclyAccessible`: Public footer badge can query count with HTTP 200.
11. `visitorRecording_recordsVisit`: Valid visit increments daily and total statistics.
12. `visitorRecording_skipsAdminDashboard`: Requests with `X-Admin-Dashboard: true` do not inflate public visit statistics.
13. `contactForm_acceptsValidSubmission`: Valid message is saved in database and returns HTTP 200.
14. `contactForm_rejectsEmptySubmission`: Empty payload returns HTTP 400.
15. `contactForm_rejectsShortMessage`: Messages shorter than 20 characters return HTTP 400 with field errors.

---

## 🚢 Production Deployment Instructions

### Required Environment Variables
Configure these variables in your deployment environment (Docker, AWS ECS, Heroku, Render, VPS, or systemd):

```bash
# Database Connection
export DB_URL="jdbc:mysql://<your-db-host>:3306/<database-name>?useSSL=true&serverTimezone=UTC"
export DB_USERNAME="<your-db-user>"
export DB_PASSWORD="<your-db-password>"

# Production Admin Credentials
export ADMIN_USERNAME="<your-production-admin-username>"
export ADMIN_PASSWORD_HASH="<your-bcrypt-hashed-password>"

# Server Port (optional, default 8080)
export PORT="8080"
```

### Production Checklist
1. Enable HTTPS on your domain and set `server.servlet.session.cookie.secure=true` in `application.properties`.
2. Update the allowed CORS origins in `SecurityConfig.java` to your custom domain (`https://sethuraman.dev`).
3. Set `spring.jpa.hibernate.ddl-auto=validate` or `none` after initial migration for strict schema governance.

---

## 👤 Portfolio Owner

- **Name:** J. Sethuraman
- **Role:** Junior Full Stack Developer
- **Email:** sethu4637@gmail.com
- **Phone:** +91 9345659273
- **LinkedIn:** [sethuraman-j-093b2129b](https://www.linkedin.com/in/sethuraman-j-093b2129b)
- **GitHub:** [sethu4637-cell](https://github.com/sethu4637-cell)
- **Education:** B.E. Computer Science & Engineering (Class of 2026), R.V.S. College of Engineering

---
*Developed by J. Sethuraman · © 2026*
