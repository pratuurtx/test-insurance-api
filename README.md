# Spring Boot Application with Docker Compose, PostgreSQL and MinIO

This guide will walk you through setting up and running the Spring Boot application with PostgreSQL database and MinIO object storage using Docker Compose.

## Prerequisites

- Docker installed on your system
- Docker Compose installed
- Java JDK (version 17)

## Step 1: Database Schema and Configuration Setup

1. **Create the required files** in `src/main/resources`:

src/main/resources/

├── application.properties

└── schema.sql

2. Add your database schema to `schema.sql`.
```sql
SET TIME ZONE 'Asia/Bangkok';

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE status_enum AS ENUM ('ACTIVE', 'INACTIVE');
CREATE TYPE category_enum AS ENUM ('BANNER', 'PROMOTION', 'INSURANCE', 'SUIT_INSURANCE');

CREATE TABLE IF NOT EXISTS contents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    effective_from  TIMESTAMP NOT NULL,
    effective_to TIMESTAMP NOT NULL,
    status status_enum NOT NULL,
    category category_enum NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS banners (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cover_image_path VARCHAR(255) NOT NULL,
    cover_hyper_link VARCHAR(255) NOT NULL,
    content_id UUID NOT NULL UNIQUE,
    CONSTRAINT fk_banner_content FOREIGN KEY (content_id)
    REFERENCES contents(id)
    );

CREATE TABLE IF NOT EXISTS banner_contents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    banner_id UUID NOT NULL,
    content_image_path VARCHAR(255) NOT NULL,
    content_hyper_link VARCHAR(255) NOT NULL,
    CONSTRAINT fk_banner_content_banner FOREIGN KEY (banner_id)
    REFERENCES banners(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS suit_insurances (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title_th VARCHAR(255) NOT NULL,
    title_en VARCHAR(255) NOT NULL,
    image_path VARCHAR(255) NOT NULL,
    content_id UUID NOT NULL UNIQUE,
    CONSTRAINT fk_suit_insurance_content FOREIGN KEY (content_id)
    REFERENCES contents(id)
    );

CREATE TABLE IF NOT EXISTS insurances (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    icon_image_path VARCHAR(255) NOT NULL,
    cover_image_path VARCHAR(255) NOT NULL,
    title_th VARCHAR(255) NOT NULL,
    title_en VARCHAR(255) NOT NULL,
    description_th TEXT NOT NULL,
    description_en TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    content_id UUID NOT NULL UNIQUE,
    CONSTRAINT fk_insurance_content FOREIGN KEY (content_id)
    REFERENCES contents(id)
    );

CREATE TABLE IF NOT EXISTS promotions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cover_image_path VARCHAR(255) NOT NULL,
    title_th VARCHAR(255) NOT NULL,
    title_en VARCHAR(255) NOT NULL,
    description_th TEXT NOT NULL,
    description_en TEXT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    content_id UUID NOT NULL UNIQUE,
    CONSTRAINT fk_promotion_content FOREIGN KEY (content_id)
    REFERENCES contents(id)
    );

CREATE INDEX IF NOT EXISTS idx_banner_content_banner_id ON banner_contents (banner_id);
CREATE INDEX IF NOT EXISTS idx_insurance_deleted_at ON insurances (deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_promotion_deleted_at ON promotions (deleted_at) WHERE deleted_at IS NULL;
```
3. Create application.properties with this configuration:
```properties
server.address=127.0.0.1

spring.application.name=Insurance Backend API

spring.datasource.url=jdbc:postgresql://localhost:5432/insurance_db
spring.datasource.username=root
spring.datasource.password=example
spring.datasource.driver-class-name=org.postgresql.Driver

spring.datasource.hikari.maximum-pool-size=5

spring.sql.init.mode=never
spring.sql.init.schema-locations=classpath:schema.sql

spring.jpa.hibernate.ddl-auto=none

spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=20MB

minio.endpoint=http://localhost:9000
minio.access-key=YOUR_ACCESS_KEY
minio.secret-key=YOUR_SECRET_KEY
minio.bucket-name=insurance-bucket
```
## Step 2: Run Docker Compose

1. Create a `docker-compose.yml` file with the following content:

```yaml
services:
  postgres:
    image: postgres:17.4-alpine3.21
    container_name: postgres_db
    ports:
      - "5432:5432"
    environment:
      POSTGRES_USER: root
      POSTGRES_PASSWORD: example
      POSTGRES_DB: insurance_db
    volumes:
      - postgres_data:/var/lib/postgresql/data

  minio:
    image: minio/minio:latest
    container_name: minio
    ports:
      - "9000:9000"
      - "9001:9001"
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin
    volumes:
      - minio_data:/data
    command: server /data --console-address ":9001"

volumes:
  postgres_data:
  minio_data:
```

2. Run the following command to start the services:
```bash
docker-compose up -d
```
This will start PostgreSQL and MinIO in detached mode.

## Step 3: Configure MinIO Access Keys
1. Open your browser and go to http://localhost:9001
2. Login with:
    - Username: `minioadmin`
    - Password: `minioadmin`
      ![Minio Login Page](https://i.ibb.co/ZpZrkTT7/Capture.png)
3. Navigate to the "Access Keys" section in the sidebar
   ![Minio Login Page](https://i.ibb.co/xSkBw8Sm/Capture2.png)
4. Click "Create Access Key" button
   ![Minio Login Page](https://i.ibb.co/gL3Dr3fj/Capture3.png)
   ![Minio Login Page](https://i.ibb.co/5W3PBqXK/Capture4.png)
5. Copy the generated Access Key and Secret Key
6. In your Spring Boot application's `application.properties`, replace these values:
```properties
minio.access-key=YOUR_ACCESS_KEY
minio.secret-key=YOUR_SECRET_KEY
```

### Step 4: Database Initialization
1. For the first run, modify your application.properties to enable database initialization:
```properties
spring.sql.init.mode=always
```
2. Run your Spring Boot application (either through your IDE or using Maven/Gradle):
3. After the application starts successfully and the database is initialized, change back the property:
```properties
spring.sql.init.mode=never
```
4. Step 4: Run the Application Normally
   Now you can run the application with the normal configuration:

### Application Properties Reference
Here's the complete application.properties configuration for reference:
```properties
server.address=127.0.0.1

spring.application.name=Insurance Backend API

spring.datasource.url=jdbc:postgresql://localhost:5432/insurance_db
spring.datasource.username=root
spring.datasource.password=example
spring.datasource.driver-class-name=org.postgresql.Driver

spring.datasource.hikari.maximum-pool-size=5

spring.sql.init.mode=never
spring.sql.init.schema-locations=classpath:schema.sql

spring.jpa.hibernate.ddl-auto=none

spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=20MB

minio.endpoint=http://localhost:9000
minio.access-key=YOUR_ACCESS_KEY
minio.secret-key=YOUR_SECRET_KEY
minio.bucket-name=insurance-bucket
```