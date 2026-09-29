# FUNewsManagementSystem - REST API Design & Contract (Slot 12)

## 1. Resource Catalog
| Resource | Collection URI | Item URI | Description |
|---|---|---|---|
| **News Article** | `/api/news` | `/api/news/{id}` | Core news publication entity with category, tags, and status |
| **Category** | `/api/categories` | `/api/categories/{id}` | News category (active/inactive status) |
| **User / Account**| `/api/users` | `/api/users/{id}` | System account with roles (Admin / Staff) |
| **Tag** | `/api/tags` | `/api/tags/{id}` | Descriptive label linking multiple news articles |

---

## 2. API Endpoints Table

### News Resource
| Method | URI | Description | Request Body | Status Code | Response Body |
|---|---|---|---|---|---|
| `GET` | `/api/news` | List news articles | None | `200 OK` | `[ News ]` |
| `GET` | `/api/news?keyword={kw}` | Search news by title | None | `200 OK` | `[ News ]` |
| `GET` | `/api/news/{id}` | Get news by ID | None | `200 OK` / `404 Not Found` | `News` / `ApiError` |
| `POST` | `/api/news` | Create news article | `NewsCreateRequest` | `201 Created` | `News` + `Location` |
| `PUT` | `/api/news/{id}` | Full update news | `NewsUpdateRequest` | `200 OK` / `404 Not Found` | `News` / `ApiError` |
| `DELETE` | `/api/news/{id}` | Delete news article | None | `204 No Content` / `404` | None / `ApiError` |

### Category Resource
| Method | URI | Description | Request Body | Status Code | Response Body |
|---|---|---|---|---|---|
| `GET` | `/api/categories` | List all categories | None | `200 OK` | `[ Category ]` |
| `GET` | `/api/categories/{id}` | Get category by ID | None | `200 OK` / `404` | `Category` / `ApiError` |
| `POST` | `/api/categories` | Create category | `CategoryRequest` | `201 Created` | `Category` + `Location` |
| `PUT` | `/api/categories/{id}` | Update category | `CategoryRequest` | `200 OK` / `404` | `Category` / `ApiError` |
| `DELETE` | `/api/categories/{id}` | Delete category | None | `204 No Content` / `404` | None / `ApiError` |

### Sub-resource: Comments of a News Article
| Method | URI | Description | Request Body | Status Code | Response Body |
|---|---|---|---|---|---|
| `GET` | `/api/news/{id}/comments` | List comments for news | None | `200 OK` | `[ Comment ]` |
| `POST` | `/api/news/{id}/comments` | Add comment to news | `CommentRequest` | `201 Created` | `Comment` + `Location` |

---

## 3. Representation Schemas (JSON)

### News JSON Response Example (`GET /api/news/10`)
```json
{
  "id": 10,
  "title": "FPT University inaugurates advanced AI laboratory",
  "content": "Comprehensive report on state-of-the-art AI development facilities.",
  "categoryId": 2,
  "categoryName": "Technology",
  "status": "PUBLISHED",
  "tags": ["AI", "Innovation", "FPTU"],
  "authorId": 101,
  "createdAt": "2026-09-20T08:30:00Z"
}
```

### Standard Error Representation (`ApiError`)
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "News article with id 999 does not exist.",
  "path": "/api/news/999"
}
```

---

## 4. REST Constraints Analysis & Rationale
1. **Client-Server Separation**: Frontend (React SPA) handles user presentation and interaction; backend (Spring Boot) handles business logic, domain rules, and data persistence.
2. **Statelessness**: Every request contains all necessary parameters, credentials, or tokens. Server retains no client HTTP session state between calls.
3. **Cacheability**: Responses explicitly specify freshness policies (e.g. category list is highly cacheable; real-time news updates use `must-revalidate`).
4. **Uniform Interface**: Consistent resource-oriented URIs using plural nouns (`/api/news`, `/api/categories`), standardized HTTP verbs (`GET`, `POST`, `PUT`, `DELETE`), and standard HTTP status codes.
5. **Layered System**: The client communicates transparently with the API regardless of intermediaries (reverse proxies, API gateways, load balancers).
