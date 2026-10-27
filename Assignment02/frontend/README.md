# Slot 23 - FUNews Category React CRUD Client

React + Vite client for the real Slot 22 Category REST API. This project has no mock data or API fallback: all list/detail/create/update/delete operations call `VITE_API_URL`.

## 6.0. Contract baseline

**Objective:** confirm that the backend contract is available before debugging React.

**Action / command**

From the repository root, start Slot 22 with its configured SQL Server credentials, then run:

```powershell
Invoke-RestMethod "http://localhost:8080/api/categories?page=0&size=10&sort=categoryName,asc"
```

The equivalent browser/Postman/curl URL is `http://localhost:8080/api/categories?page=0&size=10`.

**Contract / code:** list returns a Spring `Page<CategoryResponse>` (`content`, `number`, `size`, `totalElements`, `totalPages`, `first`, `last`); detail is 200/404; create is 201/400/409; update is 200/400/404/409; delete is 204/404/409. DTO fields use `categoryDescription`, not the SQL column spelling `CategoryDesciption`.

**Verification / evidence:** PASS when port 8080 responds 200 and JSON contains `content` with SQL Server seed rows. Record the status and response. FAIL means stop here, start/fix Slot 22 and SQL Server, and do not diagnose React against an unavailable API.

Baseline response example:

```json
{
  "categoryId": 1,
  "categoryName": "Academic news",
  "categoryDescription": "Academic announcements",
  "parentCategoryId": null,
  "active": true
}
```

## 6.1. Vite project and Node/npm

**Objective:** create a JavaScript React SPA in its own directory.

**Action / code:** this directory was scaffolded from the Vite JavaScript React template. Confirm `node -v`, `npm -v`, `package.json`, and `node_modules`.

**Commands** (run from this directory):

```powershell
npm install
npm run dev
```

Open `http://localhost:5173`.

**Verification / evidence:** PASS when the Vite React app starts and the browser has no platform/runtime errors. FAIL if dependencies cannot install or Vite cannot start; save terminal output before proceeding.

## 6.2. CRUD client dependencies

**Objective:** install the HTTP, routing, and UI libraries.

**Action / code:** `package.json` includes Axios, React Router, React-Bootstrap, and Bootstrap.

**Command:** `npm ls axios react-router-dom react-bootstrap bootstrap`

**Verification / evidence:** PASS when all four packages resolve in the dependency tree. FAIL if any is missing.

## 6.3. Frontend structure and responsibilities

```text
src/
├── api/
│   ├── apiError.js
│   ├── axiosClient.js
│   └── categoryApi.js
├── components/
│   ├── AppNavBar.jsx
│   ├── CategoryForm.jsx
│   └── PaginationBar.jsx
├── pages/
│   ├── CategoryList.jsx
│   └── EditCategory.jsx
├── utils/
│   └── categoryForm.js
├── App.jsx
├── main.jsx
└── index.css
```

**Objective / action:** API files own HTTP communication; shared components own reusable UI; pages own page-level state and use cases; `App.jsx` only composes navigation and routes.

**Verification / evidence:** PASS when no page contains a hard-coded backend URL and no CRUD request is hidden in `App.jsx`. FAIL if a page calls Axios directly or treats a Page response as an array.

## 6.4. API environment configuration

**Objective:** keep the backend base URL configurable without putting server secrets in a browser bundle.

**Code/config:** `.env` contains:

```dotenv
VITE_API_URL=http://localhost:8080/api
```

Vite exposes `VITE_*` values in the browser. Never put SQL Server credentials, JWT signing secrets, private keys, or server secrets here. `.env` is ignored by Git.

**Action / command:** inspect `import.meta.env.VITE_API_URL` in the browser console during local learning; after changing `.env`, restart Vite (`Ctrl+C`, then `npm run dev`).

**Verification / evidence:** PASS when the Network request targets `localhost:8080/api`. FAIL if the request still uses the previous URL; restart Vite and inspect `.env`.

## 6.5. Axios client

**Objective:** centralize HTTP configuration.

**Code:** `src/api/axiosClient.js` creates one Axios instance using `import.meta.env.VITE_API_URL`, JSON headers, and a 10-second timeout.

**Action / verification:** inspect imports in the API module; PASS when pages/components do not repeat the backend base URL. FAIL if request URLs are hard-coded outside configuration.

## 6.6. Category API module

**Objective:** expose the backend operations through a small API boundary.

**Code:** `src/api/categoryApi.js` exports `getCategories`, `getCategoryById`, `createCategory`, `updateCategory`, and `deleteCategory`. List query defaults are keyword empty, page 0, size 10, and `categoryName,asc`.

**Verification / evidence:** PASS when list parsing uses `response.data.content`, and Network shows requests such as:

```text
GET /api/categories?page=0&size=10&sort=categoryName,asc
GET /api/categories?keyword=academic&page=0&size=10&sort=categoryName,asc
```

FAIL if code maps over `response.data` or relies on database column names.

## 6.7. Bootstrap and router entry point

**Objective:** install global Bootstrap styles once and make routing available to the app.

**Code:** `src/main.jsx` imports Bootstrap CSS and wraps `App` in `React.StrictMode` and `BrowserRouter`.

**Verification / evidence:** PASS when Bootstrap components render and direct navigation to `/categories` works. FAIL if a refresh on that route produces an app-side routing error.

## 6.8. Routing in `App.jsx`

**Objective:** keep route composition separate from CRUD state.

**Code:** `/` redirects to `/categories`; `/categories` renders `CategoryList`; `/categories/:id/edit` renders `EditCategory`; unknown paths redirect to the list.

**Verification / evidence:** open `http://localhost:5173/categories`; PASS when the category page renders. FAIL if `App.jsx` owns list/form/API state.

## 6.9. Navigation bar

**Objective:** provide reusable navigation back to category management.

**Code:** `src/components/AppNavBar.jsx` uses React-Bootstrap and router Links.

**Verification / evidence:** PASS when both brand and Categories links navigate to `/categories` without a full-page reload.

## 6.10. READ, search, loading/error/empty state

**Objective:** render the backend Page contract and give feedback through the full request lifecycle.

**Code:** `src/pages/CategoryList.jsx` loads on mount and when page, keyword, or refresh changes; renders `content`, total records, Active/Inactive, parent ID, and actions. Requests are cancellable on effect cleanup. Search trims its keyword and resets page to zero; Clear removes the filter.

**Action:** use DevTools → Network → Fetch/XHR and reload `/categories`.

**Verification / evidence:** PASS when GET returns 200 and rows match SQL Server; loading, empty, and API error states render without a runtime crash. FAIL when a failed request is hidden or the UI renders stale data as though loading succeeded.

## 6.11. Pagination component

**Objective:** connect Spring Page metadata to Previous/Next controls.

**Code:** `src/components/PaginationBar.jsx` receives `page`, `totalPages`, and `onPageChange`; it hides for zero/one page and disables boundary buttons.

**Action / verification:** with enough database rows, click Next and Previous. PASS when the URL changes page number and the shown page/rows match the response. FAIL if search does not return to page 0 or buttons permit invalid page changes.

## 6.12. CREATE modal/form

**Objective:** validate and submit the CategoryRequest contract.

**Code:** `CategoryForm.jsx` controls the modal, pending state, and 400/409 feedback. `utils/categoryForm.js` validates required/maximum lengths, converts blank parent ID to `null`, converts a supplied parent ID to a number, and constrains it to the backend `Short` range.

**Action:** submit a valid unique category:

```json
{
  "categoryName": "Technology News",
  "categoryDescription": "Technology and software engineering news",
  "parentCategoryId": null,
  "active": true
}
```

**Verification / evidence:** PASS when Network shows POST `/api/categories`, response 201, modal closes, list refetches, and the SQL Server row appears. Save request payload, response, UI before/after, and SQL query/result. FAIL if the UI claims success without the API response.

## 6.13. UPDATE page

**Objective:** load a selected category, prefill a controlled form, then update it.

**Code:** `EditCategory.jsx` reads `id` from `useParams`, calls GET detail, handles 404, validates the request, calls PUT, and navigates back on success.

**Action:** open `/categories/1/edit`, change fields, and save.

**Verification / evidence:** PASS when GET detail is 200, the form is prefilled, PUT is 200, and the list/database show updated DTO values. For a missing ID (for example `/categories/32767/edit`), PASS when the page shows an error and a back action, not a blank screen. FAIL if the ID is omitted or any database-only column name is sent.

## 6.14. DELETE and business conflicts

**Objective:** wait for backend confirmation before changing displayed rows.

**Code:** list rows are not removed optimistically. The delete button is disabled while a delete is pending. Success triggers a refetch; 404 and 409 have distinct messages. A 409 displays the server message or the referenced-category explanation.

**Action / verification:** delete an unused category (expect 204, row gone after refetch); then use a category referenced by a NewsArticle (expect 409, row remains, conflict shown).

**Evidence:** Network status, row before/after, and SQL query/result. PASS only when the 409 does not remove the row. FAIL if every delete is treated as successful or a full-page reload masks state behavior.

Slot 22 translates a database reference constraint into HTTP 409. The client deliberately does not simulate that rule or substitute local/mock data.

## 6.15. Negative test cases

| ID | Break condition | Expected API/browser result | Expected UI/evidence |
|---|---|---|---|
| N01 | Stop backend | Network failure | Useful alert; app remains usable |
| N02 | Set wrong `VITE_API_URL` | Connection failure | Configuration/network alert |
| N03 | Submit blank name | No POST | Client validation message |
| N04 | Submit blank description | No POST | Client validation message |
| N05 | Submit invalid backend payload directly | 400 | Validation alert |
| N06 | Create duplicate name | 409 | Conflict alert |
| N07 | Edit missing ID | 404 | Not-found state and back action |
| N08 | Delete referenced category | 409 | Conflict; row remains |
| N09 | Remove allowed CORS origin | Browser CORS failure | Network/console evidence; useful connection guidance |
| N10 | Double-click delete | One pending mutation | Button disabled while pending |
| N11 | Treat Spring Page as array | Render/type error | Inspect JSON; use `response.data.content` |
| N12 | Use the wrong DTO property | Invalid/missing field | Inspect payload; use `categoryDescription` |

For N05, use Postman/curl to bypass client validation so the backend validation path is tested.

## 6.16. End-to-end lifecycle trace

**READ:** `CategoryList` effect → `loadCategories` → `getCategories` → Axios client → `GET /api/categories` → Controller → Service → Repository → SQL Server → Spring `Page<CategoryResponse>` → `response.data.content` → state → table render.

**CREATE:** modal submit → client validation → `createCategory(payload)` → POST → Controller → Service duplicate check → repository save → SQL commit → 201 response → close modal/refetch → React render.

**Required evidence:** Browser Network request and payload/response, backend log/debugger, SQL query/result, and UI before/after. PASS when evidence shows one coherent request-to-database lifecycle; FAIL if relying only on a screenshot or client-side state.

## 6.17. Break-It → Diagnose → Fix workshop

| Break | Symptom / diagnosis evidence | Fix |
|---|---|---|
| Map over `response.data` | `.map is not a function`; inspect Page JSON | Map over `response.data.content` |
| Incorrect effect dependencies | Repeated GETs in Network | Keep effect dependencies aligned with load callback |
| Hard-coded URL in a page | URL differs by environment; inspect source | Use Axios instance and `.env` |
| Change `.env` without restart | Network still targets old URL | Restart Vite |
| Send parent ID as `""` | Payload does not match nullable numeric DTO | Convert blank to `null`, value to `Number` |
| Ignore 409 | Generic error despite conflict status | Branch on `error.response.status` |
| No refetch after DELETE | Stale row remains | Refetch after confirmed 204 |
| Search without page reset | Empty/out-of-range page | Set page to 0 on submit/clear |
| Hard-code edit ID | GET/PUT target is wrong | Use `useParams()` |
| Misspell DTO field / use SQL name | Wrong payload or empty description | Use `categoryDescription` |

Students should reproduce at least three breaks, capture symptom/diagnosis, then repair them.

## 6.18. Topic exercises

1. READ: capture GET 200 and rendered SQL-backed rows.
2. SEARCH: capture keyword URL and filtered result.
3. PAGINATION: capture changed page parameter and metadata.
4. CREATE: capture request payload, 201, list update, and DB row.
5. UPDATE: capture GET prefill and PUT before/after.
6. DELETE: capture 204 and row removal.
7. BUSINESS DELETE: capture 409 and retained row.
8. VALIDATION: capture required/max-length client rejection.
9. ERROR UX: demonstrate 400/404/409 alerts.
10. API MODULE: show pages use `categoryApi`, not raw URLs.
11. TRACE: document READ and CREATE request lifecycles.
12. BREAK-IT: record at least three failure → diagnosis → fix examples.

## 6.19. System assignment

Deliver a Category SPA with list, category-name search, pagination, create, edit, delete, Active/Inactive, parent ID, validation, loading/error/empty/pending states, 400/404/409 handling, API module separation, and routing. PASS only when requests reach the real Slot 22 endpoint and no mock API/data path exists.

## 6.20. Integration gate

| Gate | PASS evidence |
|---|---|
| G1 Vite run/build | `npm run dev` and `npm run build` succeed |
| G2 API config | `.env` has the local API base URL; no secrets |
| G3 backend contract | GET categories returns 200 and Page JSON |
| G4 page parsing | UI reads `response.data.content` |
| G5 READ | SQL-backed list renders |
| G6 SEARCH | keyword query filters rows |
| G7 PAGINATION | page changes through backend metadata |
| G8 CREATE | POST returns 201 and list refreshes |
| G9 UPDATE | PUT returns 200 and values persist |
| G10 DELETE | successful delete returns 204 and list refreshes |
| G11 VALIDATION | client and backend 400 paths are handled |
| G12 NOT FOUND | missing detail shows 404 state |
| G13 CONFLICT | duplicate/reference 409 paths show conflict |
| G14 BUSINESS RULE | referenced category remains after 409 |
| G15 STATE | loading/error/empty/pending states are visible |
| G16 ARCHITECTURE | page → API module → backend separation |
| G17 TRACE | browser → controller → service → repository → SQL |
| G18 EXPLAIN | student can explain the full-stack lifecycle |

## 6.21. Test matrix

| ID | Frontend action | Backend expectation | UI expectation |
|---|---|---|---|
| T01 | Load Category page | GET 200 | Table renders |
| T02 | Search academic | GET 200 | Filtered rows |
| T03 | Next page | GET 200 | New page |
| T04 | Open existing edit | GET 200 | Prefilled form |
| T05 | Open missing edit | GET 404 | Error state |
| T06 | Create valid | POST 201 | New row |
| T07 | Create invalid | POST not sent / backend 400 when bypassing UI | Validation message |
| T08 | Create duplicate | POST 409 | Conflict message |
| T09 | Update existing | PUT 200 | Updated row |
| T10 | Update missing | PUT 404 | Error state |
| T11 | Delete free category | DELETE 204 | Row removed after refetch |
| T12 | Delete referenced category | DELETE 409 | Row retained |
| T13 | Stop backend | Network failure | App survives with alert |
| T14 | Block CORS | Browser CORS failure | Debug guidance and console evidence |
| T15 | Double mutation | One effective in-flight action | Pending button disabled |

## Running the client

Start Slot 22 first. In a second terminal:

```powershell
cd funews-react-client
npm install
npm run dev
```

Open `http://localhost:5173/categories`. Vite variables are read at startup; restart Vite after changing `.env`. The backend allows the Vite origin by default; configure `CORS_ALLOWED_ORIGINS` for deployed frontend origins and restart Spring Boot.
