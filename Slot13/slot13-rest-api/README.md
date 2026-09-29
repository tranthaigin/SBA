# News REST API with 3-Layer Architecture - SBA301 Slot 13

## Environment
- Java 21 / 25 LTS
- Spring Boot 3.3.4
- Spring Web + Lombok
- Maven

## Architecture Flow
```text
HTTP Request
     ↓
DispatcherServlet / Spring MVC
     ↓
NewsController (Input binding: @PathVariable, @RequestParam, @RequestBody)
     ↓
NewsService (Business rules, existence validation)
     ↓
NewsRepository (ConcurrentHashMap in-memory store)
     ↓
GlobalExceptionHandler (@ControllerAdvice + @ExceptionHandler for 404 ApiError)
```

## Running the Application
```bash
./mvnw spring-boot:run
# on Windows:
mvnw.cmd spring-boot:run
```

## Postman Testing
Import `SBA301_Slot13_News_API.postman_collection.json` into Postman to test:
- `GET /api/hello?name=Student`
- `GET /api/news`
- `GET /api/news/1`
- `GET /api/news/999` (404 Not Found response)
- `POST /api/news` (201 Created with Location header)
- `PUT /api/news/2` (200 OK update)
- `GET /api/news?keyword=spring` (Search)
- `DELETE /api/news/1` (204 No Content)
