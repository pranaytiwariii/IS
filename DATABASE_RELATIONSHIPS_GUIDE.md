# Full-Stack Authentication Application with Database Relationships Demo

## Project Overview

This is a comprehensive full-stack application demonstrating **database relationships** and **inheritance patterns** using Spring Boot backend with Angular frontend.

## Technology Stack

### Backend

- **Spring Boot 3.2.0** - Main framework
- **H2 Database** - In-memory database for development
- **JPA/Hibernate** - ORM for database management
- **Spring Data JPA** - Repository pattern
- **BCrypt** - Password encryption
- **Maven** - Build tool

### Frontend

- **Angular 17** - Frontend framework
- **TypeScript** - Programming language
- **Bootstrap 5** - CSS framework
- **RxJS** - Reactive programming

## Database Relationships Implemented

### 1. One-to-One Relationship

**Users ↔ Roles**

- Each User has exactly one Role
- Each Role belongs to exactly one User
- Foreign key: `role_id` in `users` table with UNIQUE constraint
- Demonstrates: User inheritance based on role

```sql
ALTER TABLE users ADD CONSTRAINT UK_krvotbtiqhudlkamvlpaqus0t UNIQUE (role_id);
```

### 2. One-to-Many Relationships

#### Author → Papers

- One Author (User with AUTHOR role) can write multiple Papers
- Each Paper has exactly one Author
- Foreign key: `author_id` in `papers` table

#### Committee → Published Papers

- One Committee member can publish multiple Papers
- Each Paper can be published by one Committee member
- Foreign key: `published_by_committee_id` in `papers` table

### 3. Many-to-Many Relationship

**Papers ↔ Tags**

- Each Paper can have multiple Tags
- Each Tag can be associated with multiple Papers
- Junction table: `paper_tags` with composite primary key
- Foreign keys: `paper_id` and `tag_id`

```sql
CREATE TABLE paper_tags (
    paper_id bigint not null,
    tag_id bigint not null,
    primary key (paper_id, tag_id)
);
```

## Inheritance Pattern - Role-Based User System

### Role Hierarchy

1. **STUDENT** - Can view and read papers (read-only access)
2. **AUTHOR** - Can create and manage their own papers
3. **COMMITTEE** - Can review and publish papers from authors

### Inheritance Implementation

#### Backend (Entity Level)

```java
// User entity with role-based behavior
@Entity
public class User {
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", unique = true)
    private Role role;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private Set<Paper> authoredPapers = new HashSet<>();

    @OneToMany(mappedBy = "publishedByCommittee", fetch = FetchType.LAZY)
    private Set<Paper> publishedPapers = new HashSet<>();
}

// Role-based enum for type safety
public enum RoleName {
    STUDENT, AUTHOR, COMMITTEE
}
```

#### Frontend (Component Level)

- **StudentDashboardComponent** - Read papers, view relationships
- **AuthorDashboardComponent** - Create papers with tags
- **CommitteeDashboardComponent** - Review and publish papers

## API Endpoints

### Authentication

- `POST /api/auth/login` - User login with role information
- `POST /api/auth/signup` - User registration with role selection

### Papers Management

- `GET /api/papers/` - Get all papers
- `POST /api/papers/` - Create new paper (Authors only)
- `PUT /api/papers/{id}/publish` - Publish paper (Committee only)
- `GET /api/papers/author/{authorId}` - Get papers by author
- `GET /api/papers/published` - Get published papers

### Tags Management

- `GET /api/papers/tags` - Get all available tags
- Papers automatically associated with tags (Many-to-Many)

## Frontend Dashboard Features

### Student Dashboard

- **View all published papers** (demonstrates One-to-Many: Committee→Papers)
- **See paper authors** (demonstrates One-to-Many: Author→Papers)
- **View paper tags** (demonstrates Many-to-Many: Papers↔Tags)
- **Role information display** (demonstrates One-to-One: User↔Role)

### Author Dashboard

- **Create new papers** with title, abstract, content
- **Add tags to papers** (demonstrates Many-to-Many relationship)
- **View own authored papers** (demonstrates One-to-Many: Author→Papers)
- **See publication status** by committee members

### Committee Dashboard

- **Review pending papers** from all authors
- **Publish papers** (creates One-to-Many: Committee→Published Papers)
- **View all papers with relationships** shown in table format
- **See foreign key relationships** in detailed views

## Relationship Demonstrations

### Visual Indicators in UI

1. **One-to-One (User-Role)**:

   ```html
   <span class="badge bg-primary">Role: {{user.role}}</span>
   ```

2. **One-to-Many (Author-Papers)**:

   ```html
   <td>Author: {{paper.authorName}} (ID: {{paper.authorId}})</td>
   ```

3. **One-to-Many (Committee-Published Papers)**:

   ```html
   <td>Published by: {{paper.publishedBy || 'Not Published'}}</td>
   ```

4. **Many-to-Many (Papers-Tags)**:
   ```html
   <span class="badge bg-info me-1" *ngFor="let tag of paper.tags"
     >{{tag}}</span
   >
   ```

## Database Schema

```sql
-- One-to-One: Users ↔ Roles
users (id, username, email, password, role_id[UNIQUE])
roles (id, name)

-- One-to-Many: Users → Papers (as authors)
papers (id, title, abstract_text, content, author_id, published_by_committee_id)

-- Many-to-Many: Papers ↔ Tags
paper_tags (paper_id, tag_id)
tags (id, name, description)
```

## Key Learning Outcomes

1. **One-to-One Relationships**: Implemented through foreign key with unique constraint
2. **One-to-Many Relationships**: Implemented through foreign keys without unique constraints
3. **Many-to-Many Relationships**: Implemented through junction tables
4. **Inheritance Patterns**: Role-based user behavior with different UI experiences
5. **Full-Stack Integration**: Backend entity relationships reflected in frontend UI

## Running the Application

### Backend

```bash
cd backend
mvn spring-boot:run
# Runs on http://localhost:8080
# H2 Console: http://localhost:8080/h2-console
```

### Frontend

```bash
cd frontend
npm start
# Runs on http://localhost:4200
```

## Testing the Relationships

1. **Register users** with different roles (STUDENT, AUTHOR, COMMITTEE)
2. **Login as AUTHOR** → Create papers with tags (Many-to-Many)
3. **Login as COMMITTEE** → Publish papers (One-to-Many: Committee→Papers)
4. **Login as STUDENT** → View all relationships in read-only mode
5. **Check H2 Console** to see actual database relationships

## Advanced Features

- **Role-based routing** in Angular
- **Reactive forms** with validation
- **Real-time relationship visualization**
- **Database relationship explanations** in UI
- **Tabbed interfaces** showing different relationship types
- **Foreign key information** displayed in tables

This application serves as a comprehensive demonstration of how to implement and visualize database relationships in a modern full-stack web application.
