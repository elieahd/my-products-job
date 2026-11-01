## My Products

```mermaid
erDiagram
PRODUCT_CATEGORY ||--o{ PRODUCT : has

    PRODUCT_CATEGORY {
        BIGINT id PK
        VARCHAR code
        VARCHAR description
    }

    PRODUCT {
        BIGINT id PK
        VARCHAR name
        VARCHAR description
        BIGINT price
        TIMESTAMP created_at
        BIGINT category_id FK
        BOOLEAN active
    }
```

Already there is a `Job` configured that contains a dummy `Tasklet` step.

Clone project and open in IntelliJ IDEA 

## Specify path of the SDK

- Click on Project Structure 

![Project Structure](./assets/project-structure.png)

- Specify SDK path and apply changes

![Project Structure / SDK](./assets/project-structure-sdk.png)

If you need to use other java version, don't forget to change `java.version` in `pom.xml`

## Override maven `setting.xml`

- Click on Settings

![Settings](./assets/settings.png)

- Override maven `settings.xml` file

![Settings](./assets/maven-settings.png)

## Compile project

![compile project](./assets/compile.png)

## Create `.env` at the root with the following information 

```properties
DB_HOST=...
DB_PORT=...
DB_NAME=...
DB_USERNAME=...
DB_PASSWORD=...
DB_SCHEMA=...
```

## Modify run configuration

![modify run configuration](./assets/modify-run-configuration.png)

- Modify options to add Environment variables
- Specify .env path as environment variable

![run configuration](./assets/run-configuration.png)

## Run application 

Make sure liquibase is well executed on your schema 

## Open swagger to discover project 

http://localhost:8080/swagger-ui/index.html
