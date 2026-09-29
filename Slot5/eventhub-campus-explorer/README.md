# EventHub - Campus Event Explorer (Slot 05)

## Course
SBA301 - Integrate Single Page Application with Spring Boot
Slot 05 - Integrated React Lab 01

## Architecture & Data Flow
- **Data Source**: `src/data/events.js` (8 campus event records).
- **Collection Rendering**: `EventList.jsx` maps over `filteredEvents` with `event.id` key.
- **Reusable Component**: `EventCard.jsx` receives `event` object via Props.
- **Local State Interaction**: Each `EventCard` manages its own `showDetail` boolean state triggering a React-Bootstrap `Modal`.
- **Search & Filter**: Combined keyword search, category filter, and featured switch producing derived `filteredEvents`.

## Running the Project
```bash
npm install
npm run dev
```

## Production Build
```bash
npm run build
npm run preview
```
