# Warsaw Beauty Explorer

A full-stack web application for browsing and managing beauty salons in Warsaw. Real salon data is fetched from OpenStreetMap through the Overpass API, stored in PostgreSQL, cached with Redis, and served to an Angular frontend.

---

## Features

- Browse 120+ real Warsaw beauty salons fetched from OpenStreetMap
- Filter by district and service type on the backend
- Filter by name, minimum rating, and price range on the frontend
- Sort results by rating or review count
- Real-time deduplication during data import
- Last-successful salon snapshot fallback when Overpass is unavailable
- Server-side Redis caching
- JWT authentication with access and refresh tokens
- Role-based access control: only admins can create, update, or delete salon details
- User registration and login via dedicated frontend pages
- Edit salon details with form validation on both frontend and backend
- Unit tests for service and controller layers

---

## How To Run

### Prerequisites

- Java 17+
- Node.js 18+ and npm 10+
- Docker and Docker Compose
- Internet connection for fresh Overpass imports. If a previous snapshot exists, the backend can restore salon data from that snapshot when Overpass is unavailable.

### 1. Start PostgreSQL and Redis

```bash
docker-compose up -d
```

This starts PostgreSQL and Redis locally. Database data persists across restarts unless volumes are removed.

### 2. Start the backend

From the repository root:

```bash
./backend/mvnw -f backend/pom.xml spring-boot:run
```

On Windows PowerShell:

```powershell
.\backend\mvnw.cmd -f backend\pom.xml spring-boot:run
```

The backend starts on `http://localhost:8080`. On first startup Flyway runs the database migrations, then the app fetches live salon data from Overpass and seeds the database.

Example successful import logs:

```text
Fetched 120 unique salons from OpenStreetMap.
Saved 120 salons from Overpass. Snapshot saved.
```

A successful import also writes a generated last-known-good snapshot to:

```text
backend/data/salons_snapshot.json
```

On later restarts:

- If normal salon data already exists, the backend skips the fetch.
- If the database was restored from a snapshot fallback, the backend keeps serving that data but retries Overpass on startup.

### 3. Start the frontend

```bash
cd frontend
npm install
npm start
```

The frontend starts on `http://localhost:4200`.

### Resetting data

To wipe PostgreSQL data and re-fetch fresh data from Overpass:

```bash
docker-compose down -v
docker-compose up -d
./backend/mvnw -f backend/pom.xml spring-boot:run
```

If Overpass is unavailable after a reset, the backend tries to restore from:

```text
backend/data/salons_snapshot.json
```

When snapshot data is restored, the app creates:

```text
backend/data/salons_snapshot_restored.flag
```

On the next startup, that marker tells the app to retry Overpass and replace the snapshot-restored data with fresh imported data when possible.

---

## Technical Solution

### Backend - Spring Boot

| Tool | Purpose |
|---|---|
| Spring Boot 3.5 | Application framework |
| Java 17 | Language version |
| Spring Web | REST API |
| Spring Data JPA | Database access and repository layer |
| PostgreSQL | Main relational database |
| Flyway | Versioned database migrations |
| Spring Validation | Request body validation |
| Spring Security | Authentication, authorization, and CORS |
| JWT | Access and refresh token implementation |
| Spring Cache + Redis | Server-side response caching |
| Redis | Cache store with 5-minute TTL |
| RestTemplate | Overpass HTTP client with connect/read timeouts |
| Lombok | Reduces boilerplate |
| ModelMapper | Entity/DTO mapping |
| Docker Compose | Local PostgreSQL and Redis runtime |

The backend follows a standard layered architecture:

```text
Controller -> Service -> Repository -> Entity
```

### API Endpoints

- `POST /api/auth/register` - register a new user
- `POST /api/auth/login` - login and receive access + refresh tokens
- `POST /api/auth/refresh` - exchange a refresh token for a new access token
- `GET /api/salons?district=&service=` - filtered salon list, public
- `GET /api/salons/{id}` - full salon detail, public
- `PUT /api/salons/{id}` - update salon fields, admin only
- `POST /api/salons` - create a salon, admin only
- `DELETE /api/salons/{id}` - delete a salon, admin only

### Authentication

Authentication uses JWT. On login, the backend issues a short-lived access token and a longer-lived refresh token. The frontend attaches the access token to API requests and uses the refresh token to obtain a new access token when needed. Salon write endpoints are protected and reject non-admin users.

Access tokens are not stored in the database. Refresh tokens are stored in PostgreSQL so they can be expired, rotated, and revoked.

### Caching

Caching is implemented with Spring Cache backed by Redis. `GET /api/salons` and `GET /api/salons/{id}` responses are cached with a 5-minute TTL. Cache keys include the filter parameters `district` and `service`. Any salon write operation (`POST`, `PUT`, or `DELETE`) evicts cached salon entries so subsequent reads reflect the latest data.

Measured locally with 120 salons:

```text
Cache MISS (first request, hits PostgreSQL):  ~415ms
Cache HIT  (subsequent request, hits Redis):  ~18ms
```

That is approximately a 23x speedup on repeated identical requests.

### Data Collection

Data collection is handled by `OverpassDataClient`. It queries the Overpass API, a read-only OpenStreetMap API, for all nodes and ways tagged `shop=beauty` or `shop=hairdresser` inside Warsaw's administrative boundary.

This is similar to a small ETL pipeline:

- Extract raw OSM elements from Overpass
- Transform messy OSM tags into the local `Salon` model
- Load cleaned salon records into PostgreSQL

During transformation:

- Elements missing a name or coordinates are discarded
- Duplicates are detected using normalized `name + rounded lat/lon`
- Node coordinates use `lat/lon`; way coordinates use the Overpass `center`
- District is resolved from OSM tags first, then inferred from coordinate bounding boxes
- Services are parsed from `shop`, `beauty`, `colour/color`, and related OSM tags
- Address is assembled from `addr:street`, `addr:housenumber`, `addr:postcode`, and `addr:city`
- Phone and website are read from both direct and `contact:*` tags

The Overpass HTTP client has:

- 5-second connect timeout
- 20-second read timeout

This prevents startup from waiting indefinitely on the external API.

### Snapshot Fallback

After a successful Overpass import, `SalonSnapshotService` saves a generated snapshot of the transformed salon data to:

```text
backend/data/salons_snapshot.json
```

The snapshot intentionally omits database-generated fields such as `id`, `createdAt`, and `updatedAt`, so PostgreSQL can generate fresh values when the snapshot is restored.

If the database is empty and Overpass fails or returns no salons, the backend loads the snapshot and marks the database as snapshot-restored with:

```text
backend/data/salons_snapshot_restored.flag
```

On the next startup, if that marker exists, the app tries Overpass again and replaces the snapshot-restored data with fresh imported data when possible.

### Note On Mock Data

The Overpass API does not provide ratings, review counts, or price ranges. These fields are randomly generated at import time for demonstration purposes:

- rating: 3.5 to 5.0
- reviews: 5 to 300
- price range: random enum value

In production, these fields would be removed or replaced with data from a trusted licensed source.

### Frontend - Angular 21

| Tool | Purpose |
|---|---|
| Angular 21 | Standalone component framework |
| Angular Router | Client-side routing and route guards |
| Angular Signals | Reactive auth state |
| HttpClient | REST API calls with auth interceptor |
| RxJS | Reactive streams |
| TypeScript 5.9 | Type safety |

Pages include `SalonList`, `SalonDetail`, `SalonEdit`, `Login`, and a shared `Navbar`.

Filtering is split intentionally:

- District and service filters are sent to the backend as query parameters.
- Name search, minimum rating, price range, and sorting are applied on the frontend against the already-fetched results.

---

## What I'd Improve With More Time

### Data

- Replace mocked ratings and price ranges with real data from a trusted source
- Store raw Overpass responses and import status in the database for better auditability and reprocessing
- Replace bounding boxes with official Warsaw district polygons or PostGIS
- Add stronger fuzzy duplicate detection using name, address, phone, website, and distance

### Backend

- Add optimistic locking (`@Version`) on `Salon` to prevent lost updates when two admins edit the same salon simultaneously

### Frontend

- Persist filter state in URL query params so results are shareable
- Migrate remaining `ChangeDetectorRef` workarounds to cleaner Angular state handling
- Improve the Angular project structure and code readability

---

## TODO

- [x] Add authentication with JWT login
- [x] Add Redis caching to `GET /api/salons` and `GET /api/salons/{id}`
- [x] Add admin-only create, update, and delete endpoints
- [x] Add generated salon snapshot fallback for Overpass failures
- [ ] Add pagination to salon list
- [ ] Add optimistic locking (`@Version`) to prevent lost updates
