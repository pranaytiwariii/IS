# PostgreSQL Database Setup Guide

## Prerequisites

1. **Install PostgreSQL** (if not already installed)
   - Download from: https://www.postgresql.org/download/windows/
   - During installation, remember the password you set for the `postgres` user
   - Default port: 5432

## Step-by-Step Setup

### 1. Connect to PostgreSQL Command Line

Open Command Prompt or PowerShell as Administrator and run:

```bash
# Connect to PostgreSQL as superuser
psql -U postgres -h localhost
```

When prompted, enter the password you set during PostgreSQL installation.

### 2. Create Database and User

```sql
-- Create the database
CREATE DATABASE authdb;

-- Connect to the new database
\c authdb;

-- Verify connection
SELECT current_database();

-- Exit psql
\q
```

### 3. Update Application Configuration

The application is already configured to use:

- **Database**: `authdb`
- **Username**: `postgres`
- **Password**: `psql@123`
- **Port**: `5432`
- **Host**: `localhost`

If your PostgreSQL password is different, update it in:
`backend/src/main/resources/application.properties`

```properties
spring.datasource.password=YOUR_ACTUAL_PASSWORD
```

### 4. Start the Application

```bash
# Navigate to backend directory
cd "C:\Users\user\Desktop\IS\backend"

# Clean and compile
mvn clean compile

# Run the application
mvn spring-boot:run
```

### 5. Verify Database Setup

The application will automatically:

- Create all necessary tables with relationships
- Insert sample data for testing
- Display initialization messages in the console

### 6. Access the Application

- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080
- **PostgreSQL**: You can connect with any PostgreSQL client using the credentials above

## Sample User Accounts Created

The system will automatically create these test accounts:

| Username   | Password    | Role      | Description                   |
| ---------- | ----------- | --------- | ----------------------------- |
| student1   | password123 | STUDENT   | Can view published papers     |
| author1    | password123 | AUTHOR    | Can create and manage papers  |
| author2    | password123 | AUTHOR    | Can create and manage papers  |
| committee1 | password123 | COMMITTEE | Can review and publish papers |

## Sample Data Created

### Papers with Relationships:

1. **"Machine Learning in Healthcare"**

   - Author: author1
   - Tags: Artificial Intelligence
   - Status: Published by committee1

2. **"Distributed Database Design Patterns"**

   - Author: author2
   - Tags: Database Systems
   - Status: Draft (unpublished)

3. **"Modern Web Security Vulnerabilities"**
   - Author: author1
   - Tags: Web Development, Cybersecurity
   - Status: Published by committee1

### Database Relationships Demonstrated:

- **One-to-One**: Users ↔ Roles
- **One-to-Many**: Authors → Papers
- **One-to-Many**: Committee → Published Papers
- **Many-to-Many**: Papers ↔ Tags

## Verification Steps

### 1. Check Database Tables

```sql
-- Connect to database
psql -U postgres -d authdb

-- List all tables
\dt

-- View relationships
\d users
\d papers
\d paper_tags

-- Sample queries
SELECT u.username, r.name as role FROM users u JOIN roles r ON u.role_id = r.id;
SELECT p.title, u.username as author FROM papers p JOIN users u ON p.author_id = u.id;
SELECT p.title, COUNT(pt.tag_id) as tag_count FROM papers p LEFT JOIN paper_tags pt ON p.id = pt.paper_id GROUP BY p.id, p.title;
```

### 2. Test Frontend Access

- Navigate to http://localhost:4200
- Login with any sample account
- Explore different dashboards based on roles
- Create new papers, add tags, publish papers

## Troubleshooting

### Connection Issues:

- Verify PostgreSQL service is running
- Check firewall settings for port 5432
- Confirm username/password in application.properties

### Permission Issues:

- Ensure postgres user has necessary privileges
- Run PostgreSQL installer as Administrator if needed

### Application Startup Issues:

- Check Java version (requires Java 17+)
- Verify Maven is properly installed
- Clear target directory: `mvn clean`

## Database Schema Verification

After successful startup, your PostgreSQL database will contain:

```
authdb
├── users (id, username, email, password, role_id[UNIQUE])
├── roles (id, name)
├── papers (id, title, abstract_text, content, author_id, published_by_committee_id)
├── tags (id, name, description)
└── paper_tags (paper_id, tag_id) [Junction Table]
```

This demonstrates all major relationship types in a real-world application scenario.
