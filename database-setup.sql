-- PostgreSQL Database Setup Script
-- Run this script as postgres superuser to create the database and user

-- Create database
CREATE DATABASE authdb;

-- Connect to the database
\c authdb;

-- Create user (if not exists)
DO
$do$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_catalog.pg_roles
      WHERE  rolname = 'postgres') THEN
      CREATE ROLE postgres LOGIN PASSWORD 'psql@123';
   END IF;
END
$do$;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE authdb TO postgres;
GRANT ALL ON SCHEMA public TO postgres;

-- The tables will be created automatically by Hibernate when the application starts
-- with spring.jpa.hibernate.ddl-auto=create-drop

-- Sample data will be inserted through the application
