# Orchid Router Demo – SBA301 Slot 10

## Environment
- Node.js 21 / v24 LTS
- React 18
- React Router DOM 6.26+
- React Bootstrap 2.10+

## Running the Application
```bash
npm install
npm run dev
```

## Route Map
| URL | Page / Component | Notes |
|---|---|---|
| `/` | `HomePage` | Index route inside `MainLayout` |
| `/orchids` | `OrchidsPage` | Catalog list with `useSearchParams` category filtering |
| `/orchids/:id` | `OrchidDetailPage` | Dynamic param route with fallback & `useLocation` state |
| `/about` | `AboutPage` | Static informational page |
| `/contact` | `ContactPage` | Form submitting and programmatic redirect |
| `/home` | Redirect | Declarative `Navigate to="/" replace` |
| `/dashboard` | `DashboardLayout` | Nested route parent |
| `/dashboard` (index) | `DashboardHome` | Overview tab |
| `/dashboard/favorites` | `FavoritesPage` | Starred favorites sub-view |
| `/dashboard/profile` | `ProfilePage` | User profile sub-view |
| `*` | `NotFoundPage` | 404 Catch-all route inside `MainLayout` |

## Production Build & Preview
```bash
npm run build
npm run preview
```
