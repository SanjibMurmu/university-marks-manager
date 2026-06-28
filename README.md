# University Marks Manager

A secure, full-stack enterprise web application designed to streamline academic evaluation and marks management. Built with a decoupled architecture featuring a high-performance **React frontend** and a production-hardened **Java Spring Boot backend**, the system provides robust authentication and stateless role-based access control for students and administrators.

## 🔗 Live Deployments

* **Frontend UI:** [https://university-marks-manager.netlify.app](https://university-marks-manager.netlify.app)
* **Backend API:** [https://university-marks-manager.onrender.com](https://university-marks-manager.onrender.com)

---

## 🛠️ Technology Stack

### Frontend Architecture
* **Core:** React.js (Vite modern build pipeline)
* **HTTP Client:** Axios with global interceptors for stateless auth overheads
* **Styling:** Tailwind CSS (utility-first interface framework)
* **Hosting:** Netlify Edge Network CDN

### Backend Infrastructure
* **Core Framework:** Java 17 / Spring Boot
* **Security Configuration:** Spring Security 6 (Stateless Session Engine)
* **Authentication:** JSON Web Tokens (JWT) with automated validation filters
* **Cryptography:** BCrypt Password Hashing with randomized salting
* **Hosting:** Render Web Services

### Cloud Database Layer
* **Provider:** Aiven Cloud Platform
* **Engine:** Managed Relational SQL Database System (PostgreSQL/MySQL)

---

## ✨ System Features

* **Role-Based Access Control (RBAC):** Strict view-layer switching and endpoint access control isolating Student scopes from Administrative mutations. Admin controls are securely encapsulated in the frontend `AdminControls.jsx`.
* **Stateless Authentication Pipeline:** Implements a custom cryptographic filter (`JwtFilter.java`) running upstream of the standard username-password verification mechanisms.
* **PDF Report Generation:** Integrated `PdfService.java` to handle automated document generation for student results and marksheets.
* **Automated Pre-flight Resolution:** Explicit CORS configuration mapping native local (`localhost:5173`) and cloud endpoints (`netlify.app`) to safely bypass browser security policies.

---

## 📂 Repository Architecture

```text
university-marks-manager/
├── demo/                               # Spring Boot Backend Root
│   ├── render.yaml                     # Render Cloud Deployment Blueprint
│   ├── pom.xml                         # Maven Build Specifications
│   └── src/main/java/com/sanju/marks/
│       ├── MarksApplication.java       # Application Main Gateway
│       ├── SecurityConfig.java         # SecurityFilterChain & CORS policies
│       ├── JwtFilter.java & JwtUtil.java # JWT Authentication Bouncers
│       ├── StudentController.java      # Main REST Endpoints
│       ├── StudentService.java         # Core Business Logic
│       ├── PdfService.java             # PDF Report Generator Engine
│       ├── AppUser.java & Grade.java   # JPA Data Entities
│       └── *Repository.java            # Database Access Interfaces
│
└── marks-frontend/                     # React Frontend Root
    ├── package.json                    # NPM Dependency Manifesto
    ├── vite.config.js                  # Vite Client Matrix
    ├── index.html                      # Application Entry HTML
    └── src/
        ├── App.jsx & main.jsx          # React Component Tree Roots
        ├── AdminControls.jsx           # Protected Administrator Interface
        ├── index.css & App.css         # Global Styles & Tailwind Directives
        └── assets/                     # Static SVGs and Images
```

---

## ⚙️ Getting Started (Local Development)

### Prerequisites

- **Java Development Kit (JDK):** Version 17 or higher
- **NodeJS:** Runtime environment v18+
- **Build Tools:** Maven 3.x+ & NPM

### Backend Configuration

1. Navigate into the Java core root directory:

   ```bash
   cd demo
   ```

2. Configure your local database target using environment profiles, or by editing `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:mysql://YOUR_DB_HOST:YOUR_DB_PORT/YOUR_DB_NAME
   spring.datasource.username=YOUR_DB_USER
   spring.datasource.password=YOUR_DB_PASSWORD
   ```

3. Execute the Maven wrapper to resolve remote dependencies and boot the application context local runner:

   ```bash
   ./mvnw clean spring-boot:run
   ```

The engine exposes the API locally at: **http://localhost:8080**

### Frontend Configuration

1. Navigate into the client folder structure and fetch the requisite platform modules:

   ```bash
   npm install
   ```

2. Inject your target environment configuration. Create a `.env.local` file under your client root:

   ```
   VITE_API_BASE_URL=http://localhost:8080
   ```

3. Launch the active development compiler matrix:

   ```bash
   npm run dev
   ```

The UI will spin up locally at: **http://localhost:5173**

---

## 🔒 API Endpoints Routing Matrix

The baseline routes managed via `SecurityConfig.java` use the following access controls:

| Request Protocol | Endpoint URI                       | Access Authorization Rule       |
|-------------------|-------------------------------------|----------------------------------|
| POST              | `/api/students/add`                | Public Access Allowed           |
| POST              | `/api/students/login`              | Public Access Allowed           |
| POST              | `/api/students/forgot-password`    | Public Access Allowed           |
| POST              | `/api/students/reset-password`     | Public Access Allowed           |
| OPTIONS           | `/**`                              | Pre-flight Allowed              |
| ANY               | `/api/marks/**`                    | Authenticated Bearer JWT Only    |

---

## 🚀 Cloud Deployment Pipeline

### Backend Pipeline Configuration (Render)

- **Root Directory:** `demo`
- **Build Command:** `mvn -B clean package -DskipTests`
- **Start Command:** `java -jar target/demo-0.0.1-SNAPSHOT.jar`
- **Configured Environment Variables:** Ensure `DB_URL`, `DB_USER`, and `DB_PASSWORD` match your active live production instance cloud strings.

---

## 👤 Author

**Sanjib Murmu**
Undergraduate Student, Department of Information Technology
Jadavpur University
