# Warsaw Beauty Explorer

A full-stack web application for browsing and managing beauty salons in Warsaw. Real salon data is fetched from OpenStreetMap on startup and served through a REST API to an Angular frontend.

---

## Features

- Browse 120+ real Warsaw beauty salons fetched from OpenStreetMap
- Filter by district and service type (resolved server-side)
- Filter by name, minimum rating, and price range (applied client-side)
- Sort results by rating or review count
- Real-time deduplication during data import
- Edit salon details with form validation on both frontend and backend
- In-memory salon list cache to avoid redundant API calls when navigating
- Unit tests for service and controller layers

---

## How to Run

### Prerequisites

- Java 17+
- Node.js 18+ and npm 10+
- Docker and Docker Compose
- Internet connection on first startup (required for the Overpass API data fetch)

### 1. Start the database

```bash
docker-compose up -d
```

This starts a PostgreSQL instance locally. Data persists across restarts.

### 2. Start the backend

```bash
cd backend
./mvnw spring-boot:run
```

The backend starts on `http://localhost:8080`. On first startup Flyway runs the database migration, then the app fetches live salon data from the Overpass API and seeds the database. You will see this in the console when ready:

```
Fetched 120 unique salons from OpenStreetMap.
Successfully saved 120 salons to the database!
```

On subsequent restarts the fetch is skipped since the data is already in the database.

### 3. Start the frontend

```bash
cd frontend
npm install
npm start
```

The frontend starts on `http://localhost:4200`.

### Resetting data

To wipe the database and re-fetch fresh data from Overpass:

```bash
docker-compose down -v
docker-compose up -d
./mvnw spring-boot:run
```

---

## Technical Solution

### Backend — Spring Boot

| Tool | Purpose |
|---|---|
| Spring Boot 3.5 | Application framework |
| Spring Data JPA | Database access and repository layer |
| Spring Security | CORS and security configuration |
| Spring Validation | Request body validation (`@NotBlank`, `@Pattern`) |
| PostgreSQL | Relational database |
| Flyway | Database migration management |
| Docker | Running PostgreSQL locally |
| Lombok | Reduces boilerplate |
| ModelMapper | Entity ↔ DTO mapping |
| Java 17 | Language version |

Standard layered architecture — `Controller → Service → Repository → Entity`. Three endpoints are exposed:

- `GET /api/salons?district=&service=` — filtered salon list
- `GET /api/salons/{id}` — full salon detail
- `PUT /api/salons/{id}` — update salon fields, validated with Bean Validation

**Data collection** is handled by `OverpassDataClient`, which queries the [Overpass API](https://overpass-api.de) — a read-only OpenStreetMap API — for all nodes and ways tagged `shop=beauty` or `shop=hairdresser` within Warsaw's administrative boundary. Each result is then processed:

- Elements missing a name or coordinates are discarded
- Duplicates are detected using a composite key of `name + rounded lat/lon (~111m precision)`, since the same salon can appear as both a node and a way in OSM
- District is resolved from OSM address tags first; if missing, inferred by matching coordinates against hardcoded bounding boxes for all 18 Warsaw districts
- Services are parsed from the `shop` and `beauty` OSM tags and mapped to human-readable names
- Address is assembled from individual OSM address tags (`addr:street`, `addr:housenumber`, etc.)

**Note on mock data:** The Overpass API does not provide ratings, review counts, or price ranges. These fields are randomly generated at import time (rating: 3.5–5.0, reviews: 5–300, price range: random enum value) purely for demonstration purposes and would be replaced with real data in production.

### Frontend — Angular 21

| Tool | Purpose |
|---|---|
| Angular 21 | Standalone component framework |
| Angular Router | Client-side routing |
| Angular Forms | Template-driven forms with validation |
| HttpClient | REST API calls |
| RxJS | Reactive streams and in-memory list caching |
| TypeScript 5.9 | Type safety |

Three pages (`SalonList`, `SalonDetail`, `SalonEdit`) and a shared `Navbar`. The service layer caches the salon list in memory to avoid redundant requests when navigating back from a detail view.

**Filtering is split intentionally between backend and frontend.** District and service type filters hit the backend as query params — they narrow the dataset at the database level. Name search, minimum rating, and price range are applied in-memory on the frontend against the already-fetched results, keeping the interaction instant without extra API calls.

---

## What I'd Improve With More Time

### Authentication & Authorization
- JWT based login to protect edit actions
- Role distinction between read-only users and admins

### Data
- Replace mocked ratings and price ranges with real data from Google Places API or a user contribution model
- Cache the Overpass response to a file so the app can start without depending on the external API being available

### Backend
- Add pagination to `GET /api/salons` so the API scales as the dataset grows

### Frontend
- Migrate to Angular Signals (remove ChangeDetectorRef workarounds)
- Persist filter state in URL query params so results are shareable and the back button restores the previous filter state
- Add pagination to the salon list
- Improve the Angular project structure and code readability

  ## TODO
- [ ] Add authentication (JWT login)
- [ ] Add pagination to salon list
