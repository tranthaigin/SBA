# Product API Contract - Slot 08

Base URL: `http://localhost:3001`

| Method | Endpoint | Description | Request Body | Success Response | Expected Status |
|---|---|---|---|---|---|
| GET | `/products` | Retrieve all products | None | `Product[]` | `200 OK` |
| GET | `/products/{id}` | Retrieve product by id | None | `Product` | `200 OK` / `404 Not Found` |
| POST | `/products` | Create a new product | `ProductCreate JSON` | Created `Product` with generated id | `201 Created` |
| PUT | `/products/{id}` | Replace product entirely | Full `Product JSON` | Updated `Product` | `200 OK` |
| PATCH | `/products/{id}` | Partially update product | Partial `Product JSON` | Updated `Product` | `200 OK` |
| DELETE | `/products/{id}` | Remove product by id | None | None | `200 OK` / `204 No Content` |

## Product Schema
```json
{
  "id": 1,
  "name": "iPhone 15",
  "category": "Phone",
  "price": 25000000,
  "quantity": 10,
  "active": true
}
```

## Error Observations
- **Resource id does not exist** (e.g. `GET /products/999999`): Returns HTTP 404 Not Found with an empty body or error message.
- **Malformed JSON body**: Triggers HTTP 400 Bad Request or JSON parse error.
- **Server down / Connection refused**: Client network-level error, distinct from an HTTP status code returned by an active web server.
