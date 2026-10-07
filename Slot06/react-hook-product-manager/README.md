# React Hook Product Manager - Slot 06

## Course
SBA301 - Integrate Single Page Application with Spring Boot
Slot 06 - Chapter 05: React Hooks + Lab 01

## Features
- **Frontend CRUD**: Add, edit, delete product records with immutable updates (`spread` and `filter/map`).
- **Controlled Form & Validation**: Robust form validation checking required fields, positive price, and non-negative quantity.
- **Search & Category Filter**: Derived data filtering combining case-insensitive search and category selection without redundant state.
- **Product Statistics**: Auto-calculated product types count, total units, and total inventory value using `reduce`.
- **Custom Hook `useLocalStorage`**: Persistent browser storage synchronization for product data and theme.
- **ThemeContext & Toggle**: Light / Dark mode toggling via `useContext` and custom `useTheme` hook.
- **useRef DOM Focus**: Auto-focus on search input upon initial load and on demand via Focus button.
- **Side Effects**: Document title synchronization with product count and temporary feedback notification with timer cleanup.

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
