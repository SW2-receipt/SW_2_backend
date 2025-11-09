# MySQL Database Setup Guide

## 1. MySQL Database Creation

Create a database for the application:

```sql
CREATE DATABASE your_database CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 2. application.properties Configuration

Update the following values in `src/main/resources/application.properties`:

```properties
# MySQL Database Settings
spring.datasource.url=jdbc:mysql://localhost:3306/your_database?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.properties.hibernate.default_schema=your_database
```

### Required Changes:
- `your_database`: Your MySQL database name
- `your_username`: Your MySQL username
- `your_password`: Your MySQL password

## 3. Database Table Creation

The application will automatically create the `users` table on first startup due to:
```properties
spring.jpa.hibernate.ddl-auto=update
```

### Manual Table Creation (Optional)

If you prefer to create the table manually:

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider VARCHAR(20) NOT NULL,
    oauth_id VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    name VARCHAR(50),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    UNIQUE KEY unique_provider_oauth (provider, oauth_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

## 4. Verify Connection

After starting the application, check the logs to ensure MySQL connection is successful.

## 5. Troubleshooting

### Connection Error
- Verify MySQL is running
- Check username and password
- Ensure database exists
- Verify MySQL server allows connections from your IP

### Character Encoding Issues
- Ensure database charset is `utf8mb4`
- Verify connection URL includes `characterEncoding=UTF-8`

### Table Creation Issues
- Check user has CREATE TABLE permissions
- Verify `spring.jpa.hibernate.ddl-auto=update` is set

