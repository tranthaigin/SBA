# SBA301 Slot 8 - Product REST API Design and Inspection Kit

## Scope
Practice Client-Server, HTTP, JSON, REST, Postman and DevTools.
This lab does NOT implement a real Spring Boot backend and does NOT teach Fetch/Axios in depth (which is covered in Slot 9).

## Requirements
- Node.js 21 or current LTS (v24+)
- npm
- Postman
- Chrome/Edge DevTools

## Install
```bash
npm install
```

## Run Mock API
```bash
npm run api
```
Base URL: `http://localhost:3001`
Endpoints available:
- `GET /products`
- `GET /products/:id`
- `POST /products`
- `PUT /products/:id`
- `PATCH /products/:id`
- `DELETE /products/:id`

## Run JSON Parse & Stringify Demo
```bash
npm run json-demo
```

## Reset Data
Stop json-server first (Ctrl+C), then execute:
```bash
npm run reset-db
npm run api
```

## API Contract & Postman Collection
- See `docs/api-contract.md` for complete REST contract details.
- Import `SBA301_Slot8_Product_API.postman_collection.json` into Postman to run all CRUD tests.
