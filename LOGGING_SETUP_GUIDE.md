# Logging Configuration Guide

## Overview
This project has been configured with comprehensive logging using SLF4J and Logback. The logging system provides multiple output formats and log levels to help with development, debugging, and production monitoring.

## Logging Dependencies Added
- **SLF4J API**: Standard logging interface for Java
- **Logstash Logback Encoder**: For structured JSON logging

## Log Configuration Files

### 1. application.properties
Contains basic logging configuration:
- Root log level: INFO
- Application-specific log level: DEBUG
- Console and file patterns
- File rotation settings

### 2. logback-spring.xml
Advanced configuration with multiple appenders:
- **Console Appender**: For development
- **File Appender**: General application logs
- **Error File Appender**: Only ERROR level messages
- **Auth File Appender**: Authentication-specific logs
- **JSON File Appender**: Structured JSON format

## Log Files Generated

All log files are stored in the `logs/` directory:

1. **application.log**: General application logs
2. **error.log**: Error-level messages only
3. **auth.log**: Authentication-related logs
4. **application.json**: JSON-formatted structured logs

## Log Levels Used

- **INFO**: General application flow information
- **DEBUG**: Detailed debugging information
- **WARN**: Warning messages for potential issues
- **ERROR**: Error conditions that need attention

## Logging Features Added

### 1. AuthController Logging
- Login attempts and results
- Registration attempts and validation
- Security-related events
- Error handling with detailed context

### 2. AuthService Logging
- User authentication processes
- User registration processes
- Database operations
- Password validation

### 3. PaperController Logging
- Paper creation and publication
- Search operations
- Role-based access control

### 4. PaperService Logging
- Paper management operations
- Tag processing
- Database interactions

### 5. Global Exception Handler Logging
- Validation errors
- Unexpected exceptions
- Stack traces for debugging

### 6. Application Startup Logging
- Application initialization
- Configuration loading
- Component startup

## Log Patterns

### Console Pattern
```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

### File Pattern
```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

### JSON Pattern
Structured JSON with timestamp, level, logger name, message, and stack traces.

## File Rotation Configuration

- **Max File Size**: 100MB
- **Max History**: 30 days
- **Total Size Cap**: 1GB for application logs, 500MB for error/auth logs

## Usage Examples

### In Controllers
```java
private static final Logger logger = LoggerFactory.getLogger(YourController.class);

logger.info("User login attempt for username: {}", username);
logger.warn("Invalid credentials for user: {}", username);
logger.error("Unexpected error during operation: {}", e.getMessage(), e);
```

### In Services
```java
private static final Logger logger = LoggerFactory.getLogger(YourService.class);

logger.debug("Processing request with parameters: {}", parameters);
logger.info("Operation completed successfully for user: {}", userId);
```

## Benefits

1. **Development**: Easy debugging with detailed logs
2. **Production**: Structured monitoring and alerting
3. **Security**: Audit trail for authentication events
4. **Performance**: Track slow operations and bottlenecks
5. **Troubleshooting**: Comprehensive error information with context

## Best Practices

1. Use appropriate log levels (DEBUG for detailed info, INFO for general flow, WARN for issues, ERROR for failures)
2. Include relevant context in log messages (user IDs, request parameters, etc.)
3. Avoid logging sensitive information (passwords, tokens)
4. Use structured logging for better parsing and analysis
5. Monitor log file sizes and rotation
6. Use correlation IDs for request tracing in microservices

## Monitoring Log Files

You can monitor the logs in real-time using:

```bash
# Windows PowerShell
Get-Content logs\application.log -Tail 50 -Wait

# Or view specific log types
Get-Content logs\auth.log -Tail 20 -Wait
Get-Content logs\error.log -Tail 10 -Wait
```

## Configuration Customization

To adjust logging levels for specific packages, add to `application.properties`:

```properties
# Reduce Spring Framework logging
logging.level.org.springframework=WARN

# Increase Hibernate logging
logging.level.org.hibernate=DEBUG

# Custom package logging
logging.level.com.example.auth.controller=INFO
logging.level.com.example.auth.service=DEBUG
```