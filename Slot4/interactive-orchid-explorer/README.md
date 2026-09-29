# Interactive Orchid Explorer - Slot 04

## Overview
Interactive Orchid Explorer is a ReactJS application built for SBA301 Slot 04 consolidating:
- Reusable `OrchidCard` configured via **Props**
- Interactive detail **Modal** driven by local `useState`
- **Favorite toggle** driven by independent local `useState`
- Search filter & Special switch managing collection state with **derived `visibleOrchids`**
- App-level learner information shared via **React Context (`UserContext`)**

## Component Hierarchy
```text
App
 ├── UserContext.Provider
 │    ├── AppNavbar (consumes UserContext)
 │    ├── HeroSection
 │    ├── OrchidExplorer (owns searchText & specialOnly states)
 │    │    ├── OrchidCard (owns showDetail & isFavorite states)
 │    │    └── ...
 │    └── AppFooter (consumes UserContext)
```

## Running the Project
```bash
npm install
npm run dev
```

## Building Production
```bash
npm run build
npm run preview
```
