# Campus Event Navigator - Slot 07

## Course
SBA301 - Integrate Single Page Application with Spring Boot
Slot 07 - Handling Navigation / React Router DOM

## Route Architecture
```text
BrowserRouter
 └── App
      ├── AppNavbar
      ├── Routes
      │    ├── /               -> Home (with programmatic useNavigate)
      │    ├── /events         -> EventsPage (search & category filters)
      │    ├── /events/:id     -> EventDetail (dynamic route parameter, resource-not-found)
      │    ├── /about          -> About
      │    └── *               -> NotFound (404 catch-all)
      └── AppFooter
```

## Running the Application
```bash
npm install
npm run dev
```

## Production Build
```bash
npm run build
npm run preview
```
