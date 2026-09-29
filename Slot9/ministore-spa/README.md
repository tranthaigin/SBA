# MiniStore SPA - Slot 09 React Router Demo

## Overview
Comprehensive Single Page Application demonstration for SBA301 Slot 09 showcasing:
- Declarative client-side routing with `react-router-dom` v6
- **Layout Routes & `<Outlet />`**: Shared navigation header and footer across all pages
- **Nested Routes**: Nested `/dashboard` with `index` (`DashboardHome`), `profile`, and `orders` sub-views
- **URL State**: Filters (`q` search keyword & `category`) encoded directly into `useSearchParams` URL query string
- **Dynamic Routing**: `/products/:id` with `useParams` and resource-not-found handling
- **Programmatic Navigation**: `useNavigate` with relative history navigation (`navigate(-1)`)
- **404 Handling**: Catch-all wildcard `*` route rendered inside `MainLayout`

## Running the Application
```bash
npm install
npm run dev
```

## Production Build & Preview
```bash
npm run build
npm run preview
```
