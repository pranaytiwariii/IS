# Full-Stack Authentication Application

This project consists of a Spring Boot backend with PostgreSQL database and an Angular frontend, implementing basic user authentication with login and signup functionality.

## Project Structure

```
IS/
├── backend/               # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/auth/
│   │   │   │   ├── controller/     # REST controllers
│   │   │   │   ├── dto/           # Data Transfer Objects
│   │   │   │   ├── entity/        # JPA entities
│   │   │   │   ├── repository/    # Data repositories
│   │   │   │   ├── service/       # Business logic
│   │   │   │   └── AuthBackendApplication.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml            # Maven configuration
├── frontend/              # Angular application
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/
│   │   │   │   ├── login/
│   │   │   │   └── signup/
│   │   │   ├── services/
│   │   │   ├── app.component.*
│   │   │   └── app.module.ts
│   │   ├── index.html
│   │   ├── main.ts
│   │   └── styles.css
│   ├── angular.json
│   ├── package.json
│   └── tsconfig.json
└── README.md
```

## Features

- **Backend (Spring Boot)**:

  - User registration and login
  - Password hashing using BCrypt
  - PostgreSQL database integration
  - REST API endpoints
  - Input validation
  - CORS configuration

- **Frontend (Angular)**:
  - Login and signup forms
  - Form validation
  - HTTP client integration
  - Bootstrap styling
  - Routing between components

## Prerequisites

Before running this application, make sure you have the following installed:

- **Java 17** or higher
- **Maven 3.6** or higher
- **Node.js 18** or higher
- **npm 9** or higher
- **PostgreSQL 12** or higher
- **Angular CLI** (`npm install -g @angular/cli`)

## Database Setup

1. Install and start PostgreSQL
2. Create a database named `authdb`:
   ```sql
   CREATE DATABASE authdb;
   ```
3. Update the database credentials in `backend/src/main/resources/application.properties` if needed:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/authdb
   spring.datasource.username=postgres
   spring.datasource.password=password
   ```

## Running the Backend

1. Navigate to the backend directory:

   ```bash
   cd backend
   ```

2. Install dependencies and run the Spring Boot application:

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

3. The backend will be available at `http://localhost:8080`

### Backend API Endpoints

- `POST /api/auth/signup` - Register a new user

  ```json
  {
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }
  ```

- `POST /api/auth/login` - Authenticate user
  ```json
  {
    "username": "testuser",
    "password": "password123"
  }
  ```

## Running the Frontend

1. Navigate to the frontend directory:

   ```bash
   cd frontend
   ```

2. Install dependencies:

   ```bash
   npm install
   ```

3. Start the Angular development server:

   ```bash
   npm start
   # or
   ng serve
   ```

4. The frontend will be available at `http://localhost:4200`

## Usage

1. Start the PostgreSQL database
2. Run the backend Spring Boot application
3. Run the frontend Angular application
4. Open your browser and navigate to `http://localhost:4200`
5. Use the navigation to switch between Login and Sign Up pages
6. Register a new user or login with existing credentials

## Development

### Backend Development

- The backend uses Spring Boot with the following key dependencies:
  - Spring Boot Starter Web
  - Spring Boot Starter Data JPA
  - Spring Boot Starter Validation
  - PostgreSQL Driver
  - Spring Security Crypto (for password hashing)

### Frontend Development

- The frontend uses Angular with the following features:
  - Reactive forms with validation
  - HTTP client for API communication
  - Bootstrap for styling
  - Angular Router for navigation

### Database Schema

The application automatically creates the following table structure:

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(120) NOT NULL
);
```

## Security Features

- Passwords are hashed using BCrypt before storage
- Input validation on both frontend and backend
- CORS configuration for cross-origin requests
- SQL injection protection through JPA

## Troubleshooting

1. **Database Connection Issues**:

   - Ensure PostgreSQL is running
   - Check database credentials in `application.properties`
   - Verify the database `authdb` exists

2. **Frontend Build Issues**:

   - Run `npm install` to ensure all dependencies are installed
   - Check Node.js and npm versions

3. **CORS Issues**:
   - Ensure the backend CORS configuration matches the frontend URL
   - Default frontend URL is `http://localhost:4200`

## License

This project is for educational purposes.
