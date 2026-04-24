## Smart Campus API (JAX-RS on Apache Tomcat)

This repository contains a **Java Maven WAR** project implementing the **Smart Campus Sensor & Room Management REST API** for **5COSC022W Client-Server Architectures**.

The API is built with **JAX-RS** using **Jersey** as the JAX-RS implementation, deployed to **Apache Tomcat** as a standard servlet-based web application.

### What the API manages

- **Rooms** (`/api/v1/rooms`)
- **Sensors** (`/api/v1/sensors`)
- **Sensor readings (nested sub-resource)** (`/api/v1/sensors/{sensorId}/readings`)

### Coursework specification compliance (high level)

- **JAX-RS only** (Jersey). **No Spring Boot** and no other web frameworks.
- **No database technologies** (no SQL Server / JDBC persistence). Data is stored **in-memory** using `ConcurrentHashMap` and `List`.
- **Versioned API entry point**: `@ApplicationPath("/api/v1")`
- **Discovery endpoint**: `GET /api/v1` returns JSON metadata + resource links (HATEOAS-style navigation).
- **Rooms**:
  - `GET /rooms` list
  - `POST /rooms` create (**201 Created** + **Location** header + JSON body)
  - `GET /rooms/{id}` detail
  - `DELETE /rooms/{id}` delete with safety rule: **409 Conflict** if the room still has sensors assigned
- **Sensors**:
  - `GET /sensors` list with optional `?type=` filter (**case-insensitive**)
  - `POST /sensors` validates `roomId` exists; otherwise **422 Unprocessable Entity**
  - Successful sensor creation also updates the parent room’s `sensorIds` list
- **Readings (nested)**:
  - Implemented via **sub-resource locator** from `SensorResource` to `SensorReadingResource`
  - `POST` appends a reading and updates the parent sensor’s `currentValue`
  - `POST` is blocked with **403 Forbidden** if sensor `status` is `MAINTENANCE`
- **Error handling (leak-proof)**:
  - Custom exceptions + `ExceptionMapper` for **409 / 422 / 403**
  - Global `ExceptionMapper<Throwable>` for **500** with a generic JSON body (no stack traces)
  - JSON error shape: `{ "status": ..., "error": "...", "message": "..." }`
- **Observability**:
  - `ContainerRequestFilter` + `ContainerResponseFilter` logging HTTP method/URI and final status using `java.util.logging.Logger`

### Technology notes (important for markers)

- This project uses **`javax.*` namespaces** (JAX-RS 2.x / Java EE servlet model), which aligns with **Tomcat 9** deployments.
- If you deploy to **Tomcat 10+**, you must use **Jakarta EE** (`jakarta.*`) instead — this project is intentionally **`javax`**.

### Project layout (key files)

- `pom.xml` — Maven WAR packaging + Jersey dependencies
- `src/main/webapp/WEB-INF/web.xml` — Jersey `ServletContainer` wiring
- `src/main/java/.../SmartCampusApplication.java` — registers resources/providers
- `src/main/java/.../resources/*` — JAX-RS resources
- `src/main/java/.../mappers/*` — exception mappers
- `src/main/java/.../filters/ApiLoggingFilter.java` — request/response logging
- `src/main/java/.../store/InMemoryStore.java` — singleton in-memory store (+ seed data)

### Seed data (for quick demos)

On startup, the in-memory store includes:

- Rooms: `LIB-301`, `SCI-200`
- Sensor: `CO2-001` linked to `LIB-301` (also present in `LIB-301.sensorIds`)

### Run in NetBeans with Apache Tomcat (recommended)

1. Open `Smart_Campus_API` as a **Maven Project** in NetBeans.
2. Configure **Apache Tomcat 9** in NetBeans Services (recommended for `javax.*`).
3. **Clean and Build** the project.
4. **Run** / **Deploy** to Tomcat.

Typical base URL after deployment:

- `http://localhost:8080/smart-campus-api/api/v1`

> The context path `/smart-campus-api` comes from the WAR filename (`smart-campus-api.war`).

### Postman demonstration checklist (matches rubric expectations)

Run these in order for a clean narrative:

1. **Discovery**: `GET /api/v1` → **200** JSON metadata + links
2. **Create room**: `POST /rooms` → **201** + `Location` + JSON body
3. **List rooms**: `GET /rooms` → **200**
4. **Get room**: `GET /rooms/{id}` → **200**
5. **Delete empty room**: `DELETE /rooms/{id}` (no sensors) → **204**
6. **Delete room with sensors**: `DELETE /rooms/{id}` (has sensors) → **409** JSON error
7. **Invalid sensor roomId**: `POST /sensors` with bad `roomId` → **422** JSON error
8. **Valid sensor**: `POST /sensors` → **201** + `Location`
9. **Filter sensors**: `GET /sensors?type=CO2` then change `type` → list updates (**200**)
10. **Nested readings**:
    - `GET /sensors/{id}/readings` → **200**
    - `POST /sensors/{id}/readings` → **201**
    - `GET /sensors/{id}` → confirm `currentValue` updated (**200**)
11. **Maintenance blocked reading**:
    - create sensor with `status=MAINTENANCE`
    - `POST /sensors/{id}/readings` → **403** JSON error
12. **Global safety net**:
    - send malformed JSON to a JSON-consuming endpoint → **500** JSON (no stack trace)
13. **Media type enforcement** (good extra demo):
    - `POST /sensors` with `Content-Type: text/plain` → **415 Unsupported Media Type**

### Example `curl` commands (Windows-friendly)

Replace host/port/context if your Tomcat deployment differs.

Discovery:

```bash
curl -i http://localhost:8080/smart-campus-api/api/v1
```

Create a room:

```bash
curl -i -X POST http://localhost:8080/smart-campus-api/api/v1/rooms ^
  -H "Content-Type: application/json" ^
  -d "{\"id\":\"LTB-301\",\"name\":\"Library Teaching Bay\",\"capacity\":60}"
```

List rooms:

```bash
curl -i http://localhost:8080/smart-campus-api/api/v1/rooms
```

Create a sensor linked to an existing room:

```bash
curl -i -X POST http://localhost:8080/smart-campus-api/api/v1/sensors ^
  -H "Content-Type: application/json" ^
  -d "{\"id\":\"TEMP-001\",\"type\":\"Temperature\",\"status\":\"ACTIVE\",\"currentValue\":0,\"roomId\":\"LTB-301\"}"
```

Filter sensors by type (case-insensitive):

```bash
curl -i "http://localhost:8080/smart-campus-api/api/v1/sensors?type=co2"
```

Nested readings + side effect (`currentValue` update):

```bash
curl -i http://localhost:8080/smart-campus-api/api/v1/sensors/TEMP-001/readings
```

```bash
curl -i -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/TEMP-001/readings ^
  -H "Content-Type: application/json" ^
  -d "{\"value\":21.5}"
```

```bash
curl -i http://localhost:8080/smart-campus-api/api/v1/sensors/TEMP-001
```

Attempt to delete a room that still has sensors (expected **409**):

```bash
curl -i -X DELETE http://localhost:8080/smart-campus-api/api/v1/rooms/LTB-301
```

### Coursework report questions (README report section)

This module expects the written answers to the coursework questions to be included in `README.md` as well (per Blackboard/GitHub instructions). Paste your answers under headings:

- **Part 1.1** — JAX-RS resource lifecycle + concurrency for in-memory maps/lists
- **Part 1.2** — HATEOAS / hypermedia benefits vs static documentation
- **Part 2.1** — returning IDs vs full room objects (bandwidth + client processing trade-offs)
- **Part 2.2** — DELETE idempotency justification for your implementation
- **Part 3.1** — `@Consumes(JSON)` mismatch handling (415) and why it matters
- **Part 3.2** — `@QueryParam` filtering vs path-based filtering
- **Part 4.1** — sub-resource locator benefits vs a monolithic controller
- **Part 5.2** — why **422** can be more accurate than **404** for invalid references inside JSON
- **Part 5.4** — cybersecurity risks of leaking stack traces + what attackers learn
- **Part 5.5** — why filters beat per-method logging duplication

### Logging

- `ApiLoggingFilter` logs **HTTP method + request URI** for every incoming request and the **final HTTP status** for every outgoing response.
- Additional `Logger` fields are used across classes for basic traceability during development.
