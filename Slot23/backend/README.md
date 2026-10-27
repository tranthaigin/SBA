# demo-slot22-backend

Spring Boot 3.4 Category CRUD API for FUNewsManagementSystem. Requires JDK 21, Maven, and SQL Server.

## Database

Run `database\Category.sql` in SSMS or Azure Data Studio. It creates `FUNewsManagementSBA301`, the `Category` table, and three seed rows.

The SQL schema uses the existing column name `CategoryDesciption` (including its spelling); the Java entity maps it to `categoryDescription`.

## Configure and run

The defaults expect SQL Server at `localhost:1433`, database `FUNewsManagementSBA301`, username `sa`, and an empty password. Set the actual credentials before starting:

```powershell
$env:DB_USERNAME = "sa"
$env:DB_PASSWORD = "<your SQL Server password>"
mvn spring-boot:run
```

Optional environment variables: `DB_URL` (full JDBC URL) and `SERVER_PORT` (defaults to `8080`). Hibernate uses `ddl-auto=validate`; create the schema with the SQL script rather than relying on automatic DDL.

## API

Base path: `/api/categories`

| Method | Path | Behavior |
|---|---|---|
| GET | `/api/categories?page=0&size=10&sort=categoryName,asc` | List and paginate |
| GET | `/api/categories?keyword=academic` | Search by category name, case-insensitive |
| GET | `/api/categories/{id}` | Get one category; `404` if missing |
| POST | `/api/categories` | Create; `201`, `400` for invalid input, `409` for duplicate name |
| PUT | `/api/categories/{id}` | Update; `404` if missing, `409` for duplicate name |
| DELETE | `/api/categories/{id}` | Delete; `204`, or `409` if a related record still references it |

The API allows the Vite development origin `http://localhost:5173` by default. Set
`CORS_ALLOWED_ORIGINS` to a comma-separated list of frontend origins when deploying,
then restart Spring Boot. A database reference violation during category deletion is
translated into HTTP 409 rather than an unhandled server error.

Example request body:

```json
{
  "categoryName": "Technology News",
  "categoryDescription": "Technology and software engineering news",
  "parentCategoryId": null,
  "active": true
}
```

Run tests with `mvn clean test`.

## Slot 23 React client

The JavaScript/Vite CRUD client is in `funews-react-client`. Start this backend first,
then run `npm install` and `npm run dev` from that directory. Open
`http://localhost:5173/categories`. The frontend README contains the Slot 23 workbook,
API lifecycle, evidence checklist, negative cases, and test matrix. The browser client
uses the real `/api/categories` endpoints and does not include API mocks.
