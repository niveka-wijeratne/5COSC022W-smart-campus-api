# Smart Campus Sensor & Room Management API

**Module:** Client-Server Architectures (5COSC022W)  
**Student:** Niveka Wijeratne (w2120096 / 20240734)  

## API Overview

This is a REST API for managing campus rooms and sensors built using JAX-RS (Jersey) on Apache Tomcat. This coursework was implemented using only JAX-RS as required by the specification. The system provides a seamless interface for campus facilities managers and automated building systems to interact with the Smart Campus infrastructure. The API demonstrates RESTful architectural patterns, resource nesting, resilient error handling, and enterprise-grade logging practices.

## Technology Stack

- **Language:** Java 17+
- **Build Tool:** Maven
- **Framework:** JAX-RS (Jersey)
- **Web Server:** Apache Tomcat 9.0+
- **Storage:** In-memory (ConcurrentHashMap and ArrayList with thread-safe synchronization)
- **Architecture:** Single WAR deployment on Tomcat

## Build & Run Instructions

### Prerequisites
- JDK 17+
- Maven 3.6 or higher
- Apache Tomcat 9.0 or higher
- NetBeans IDE (optional, for development)

### Step 1: Clone the Repository
```bash
git clone https://github.com/niveka-wijeratne/5COSC022W-smart-campus-api.git
cd 5COSC022W-smart-campus-api
```

### Step 2: Build the Project
```bash
mvn clean package
```

This creates `target/smart-campus-api.war`

### Step 3: Deploy to Tomcat

**Option A: Using NetBeans**
1. Open the project in NetBeans
2. Right-click project → Deploy
3. Server will restart and deploy automatically

**Option B: Manual Deployment**
1. Copy `target/smart-campus-api.war` to `$TOMCAT_HOME/webapps/`
2. Start Tomcat: `$TOMCAT_HOME/bin/catalina.sh run`
3. API will be available after Tomcat initializes

### Step 4: Verify Deployment
```bash
curl http://localhost:8080/smart-campus-api/api/v1
```

Expected response:
```json
{
  "apiName": "Smart Campus Sensor & Room Management API",
  "apiVersion": "v1",
  "contactName": "Module Leader",
  "contactEmail": "module.leader@example.invalid",
  "resources": {
    "rooms": "http://localhost:8080/smart-campus-api/api/v1/rooms",
    "sensors": "http://localhost:8080/smart-campus-api/api/v1/sensors"
  }
}
```

## API Design Overview

### Resource Architecture
- **Rooms** (`/api/v1/rooms`) — Campus room entities with capacity and linked sensors
- **Sensors** (`/api/v1/sensors`) — Environmental sensors (temperature, CO2, occupancy)
- **Readings** (`/api/v1/sensors/{sensorId}/readings`) — Historical sensor measurements

### HTTP Methods & Status Codes
- **201 Created** — Successful resource creation (POST)
- **200 OK** — Successful retrieval (GET)
- **204 No Content** — Successful deletion (DELETE)
- **409 Conflict** — Business rule violation (delete room with sensors)
- **422 Unprocessable Entity** — Invalid linked reference in payload
- **403 Forbidden** — State constraint violation (sensor in maintenance)
- **415 Unsupported Media Type** — Invalid Content-Type header
- **404 Not Found** — Resource not found (returned as JSON)
- **500 Internal Server Error** — Unexpected error (no stack trace exposed)

## API Endpoints

### Discovery
```
GET /api/v1
```
Returns API metadata and resource collection links (HATEOAS).

### Rooms Management

**List all rooms**
```
GET /api/v1/rooms
```
Returns array of all room objects with full details (id, name, capacity, sensorIds).

**Create a room**
```
POST /api/v1/rooms
Content-Type: application/json

{
  "id": "LIB-301",
  "name": "Library Quiet Study",
  "capacity": 60
}
```
Returns 201 Created with room object.

**Get room by ID**
```
GET /api/v1/rooms/{roomId}
```
Returns 200 OK with room details.

**Delete room**
```
DELETE /api/v1/rooms/{roomId}
```
Returns 204 No Content on success, or 409 Conflict if room has active sensors.

### Sensors Management

**List all sensors**
```
GET /api/v1/sensors
```
Returns array of all sensor objects.

**Filter sensors by type**
```
GET /api/v1/sensors?type=CO2
```
Returns filtered list. Supports: CO2, Temperature, Occupancy, etc. Query parameter is case-insensitive.

**Create a sensor**
```
POST /api/v1/sensors
Content-Type: application/json

{
  "id": "TEMP-001",
  "type": "Temperature",
  "status": "ACTIVE",
  "currentValue": 22.5,
  "roomId": "LIB-301"
}
```
Returns 201 Created if roomId exists, or 422 Unprocessable Entity if roomId not found.

### Readings (Sub-Resource Nesting)

**Get reading history for a sensor**
```
GET /api/v1/sensors/{sensorId}/readings
```
Returns array of all readings (id, timestamp, value) for the sensor.

**Add a new reading**
```
POST /api/v1/sensors/{sensorId}/readings
Content-Type: application/json

{
  "value": 23.7
}
```
Returns 201 Created with reading object (id and timestamp auto-generated). Updates parent sensor's currentValue. Returns 403 Forbidden if sensor status is MAINTENANCE.

## Sample curl Commands

The following commands demonstrate all major API functionality:

### 1. Get Discovery Endpoint
```bash
curl http://localhost:8080/smart-campus-api/api/v1
```

### 2. Get All Rooms
```bash
curl http://localhost:8080/smart-campus-api/api/v1/rooms
```

### 3. Create a Room
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"HALL-01","name":"Main Hall","capacity":200}'
```

### 4. Get Room by ID
```bash
curl http://localhost:8080/smart-campus-api/api/v1/rooms/LIB-301
```

### 5. Create Valid Sensor (201)
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"CO2-001","type":"CO2","status":"ACTIVE","currentValue":400,"roomId":"LIB-301"}'
```

### 6. Create Sensor with Invalid roomId (422)
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"BAD-001","type":"CO2","status":"ACTIVE","currentValue":0,"roomId":"ROOM-DOES-NOT-EXIST"}'
```

### 7. Filter Sensors by Type
```bash
curl "http://localhost:8080/smart-campus-api/api/v1/sensors?type=CO2"
```

### 8. Add Reading to Sensor
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":23.5}'
```

### 9. Get Reading History
```bash
curl http://localhost:8080/smart-campus-api/api/v1/sensors/TEMP-001/readings
```

### 10. Verify currentValue Updated After Reading
```bash
curl http://localhost:8080/smart-campus-api/api/v1/sensors/TEMP-001
```

### 11. Delete Empty Room (204)
```bash
curl -X DELETE http://localhost:8080/smart-campus-api/api/v1/rooms/HALL-01
```

### 12. Try Delete Room with Sensors (409 Conflict)
```bash
curl -X DELETE http://localhost:8080/smart-campus-api/api/v1/rooms/LIB-301
```

### 13. Try POST Reading to MAINTENANCE Sensor (403)
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/MAINT-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":123}'
```

### 14. Send Wrong Content-Type (415)
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
  -H "Content-Type: text/plain" \
  -d "hello"
```

## Error Handling & Responses

All error responses follow a consistent JSON structure with no exposed stack traces or implementation details:

```json
{
  "code": "ERROR_CODE",
  "message": "Human-readable error explanation",
  "status": 400
}
```

### Error Scenarios

**409 Conflict — Room Cannot Be Deleted**
```json
{
  "code": "ROOM_NOT_EMPTY",
  "message": "Room 'LIB-301' cannot be deleted because it still has 1 sensor(s) assigned.",
  "status": 409
}
```

**422 Unprocessable Entity — Invalid Linked Reference**
```json
{
  "code": "LINKED_RESOURCE_NOT_FOUND",
  "message": "Linked resource not found: roomId='ROOM-DOES-NOT-EXIST'",
  "status": 422
}
```

**403 Forbidden — Sensor Unavailable**
```json
{
  "code": "SENSOR_UNAVAILABLE",
  "message": "Sensor 'MAINT-001' cannot accept readings while status is 'MAINTENANCE'.",
  "status": 403
}
```

**415 Unsupported Media Type — Wrong Content-Type**
```json
{
  "code": "HTTP_415",
  "message": "HTTP 415 Unsupported Media Type",
  "status": 415
}
```

**404 Not Found — Resource Missing**
```json
{
  "code": "ROOM_NOT_FOUND",
  "message": "Room not found: ROOM-XYZ",
  "status": 404
}
```

**500 Internal Server Error — Unexpected Error**
```json
{
  "code": "INTERNAL_SERVER_ERROR",
  "message": "An unexpected error occurred.",
  "status": 500
}
```

## Implementation Details

### Thread Safety & Concurrency
- All shared data structures (Rooms, Sensors, Readings) stored in InMemoryStore singleton using ConcurrentHashMap
- Compound operations synchronized using locks on affected objects to prevent race conditions
- Reading history stored in synchronized lists to prevent corruption during concurrent POST operations

### Resource Lifecycle
- Resource classes use default JAX-RS per-request lifecycle
- New instance created per HTTP request
- Shared state accessed via singleton InMemoryStore
- No instance-level state retained between requests

### Exception Handling
- Custom exceptions: RoomNotEmptyException, LinkedResourceNotFoundException, SensorUnavailableException
- Exception mappers return appropriate HTTP status codes and clean JSON error bodies
- Global ExceptionMapper<Throwable> catches unexpected errors and prevents stack trace exposure
- All errors logged server-side via java.util.logging.Logger

### Sub-Resource Pattern
- SensorResource provides sub-resource locator for /sensors/{sensorId}/readings
- Delegated to separate SensorReadingResource class
- Clean separation of concerns
- Each class independently responsible for its resource scope

### Logging & Observability
- ApiLoggingFilter implements ContainerRequestFilter and ContainerResponseFilter
- Logs every incoming request: HTTP method and URI
- Logs every outgoing response: HTTP status code
- Centralized logging reduces code duplication and guarantees coverage

### HATEOAS (Hypermedia)
- Discovery endpoint returns API metadata and resource links
- Enables client self-discovery of available endpoints
- Reduces coupling between client and server URL structures

## Data Models

### Room
```json
{
  "id": "LIB-301",
  "name": "Library Quiet Study",
  "capacity": 60,
  "sensorIds": ["TEMP-001", "CO2-001"]
}
```

### Sensor
```json
{
  "id": "TEMP-001",
  "type": "Temperature",
  "status": "ACTIVE",
  "currentValue": 22.5,
  "roomId": "LIB-301"
}
```

### Sensor Reading
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": 1713880234567,
  "value": 23.7
}
```

## Coursework Report Answers

### Part 1.1 — JAX-RS Resource Lifecycle & Concurrency

By default, JAX-RS resource classes use a per-request lifecycle, meaning a new resource object can be created for each incoming HTTP request unless explicitly configured as a singleton. This is useful because request-specific data is not shared between users. However, in my project, the main application data (rooms, sensors, and readings) is stored in shared in-memory collections. Because multiple users could access the API at the same time, thread safety is important.

To manage this, I used thread-safe collections such as ConcurrentHashMap for storing rooms and sensors. This helps prevent issues such as race conditions, lost updates, or inconsistent data when multiple requests happen simultaneously.

Although ConcurrentHashMap protects individual read and write operations, multi-step actions still need careful handling. For example, creating a sensor and then linking that sensor to a room should happen consistently. Otherwise, a failure between steps could leave incomplete or mismatched data. This shows why compound operations also need proper synchronization or controlled update logic.

### Part 1.2 — HATEOAS & Discovery Endpoint

Hypermedia (HATEOAS) means an API response includes links or routes to related resources so the client can discover what actions are available next. This is considered an advanced RESTful design feature because the API becomes more self-descriptive instead of relying only on external documentation.

In my project, the discovery endpoint returns API information together with links to the main resources such as /rooms and /sensors. This benefits client developers because they can quickly understand available endpoints without hard-coding every path. If routes change later, the discovery endpoint can guide users.

Traditional static documentation can become outdated when endpoints are renamed or expanded, whereas hypermedia links returned directly by the API remain current and guide clients dynamically. This improves usability, maintainability, and flexibility.

### Part 2.1 — Returning IDs vs Full Room Objects

When returning a list of rooms, there are two common approaches. Returning only room IDs gives a smaller response size and uses less bandwidth. However, clients then need extra requests to get room details (N+1 problem).

Returning full room objects provides more useful information immediately, such as room id, room name, capacity, and linked sensors. In my API, I return full room objects because it is more practical for clients. A dashboard or frontend can display room data immediately without sending multiple extra requests. Although the response is slightly larger, it improves efficiency overall.

### Part 2.2 — DELETE Idempotency

Yes, the DELETE operation in my implementation is idempotent. Idempotent means repeating the same request multiple times should leave the server in the same final state.

If an empty room is deleted successfully, the first request removes the room and the second request finds the room already gone. The final result is still the same: the room does not exist. If the room still contains sensors, DELETE returns 409 Conflict. Repeating the request will continue giving the same result until sensors are removed. Therefore, the operation follows idempotent behavior.

### Part 3.1 — @Consumes(MediaType.APPLICATION_JSON) Effect

I used @Consumes(MediaType.APPLICATION_JSON) on POST methods to specify that the endpoint only accepts JSON request bodies. If a client sends another format such as text/plain or application/xml, JAX-RS will reject the request automatically and return 415 Unsupported Media Type.

In JAX-RS, the framework uses a message body reader to convert incoming JSON into Java objects before the resource method executes. When the Content-Type header does not match @Consumes, no suitable message body reader can be found for the conversion, so the framework returns 415 before the method executes.

This is useful because it clearly tells the client the format is wrong, prevents invalid parsing, and ensures consistent input handling.

### Part 3.2 — Query Parameters vs Path Segments for Filtering

I used filtering such as GET /sensors?type=CO2. This is better than using a path like /sensors/type/CO2 because query parameters are designed for filtering collections.

Query parameters are naturally optional, composable, and extensible. For example, /sensors?type=CO2&status=ACTIVE would be harder to manage using path segments. Path segments represent resource hierarchy, while query parameters represent optional refinement of a collection. Using path segments for filters incorrectly implies that "type/CO2" is a distinct resource, not a filter. Query parameters are more suitable for searching and filtering collections.

### Part 4.1 — Sub-Resource Locator Pattern Benefits

The sub-resource locator pattern helps organise nested resources into separate classes. In my project, SensorResource handles sensors while SensorReadingResource handles /sensors/{sensorId}/readings.

This improves the design because each class has a clear responsibility, code is easier to maintain and test, and it avoids one very large controller class. As APIs grow larger, splitting resources like this makes the project cleaner and easier to manage. Instead of one massive SensorResource class with 20+ methods, we have SensorResource focused on sensor operations and SensorReadingResource focused on reading history.

### Part 5.2 — Why 422 is Better Than 404 for Missing References

A 404 Not Found usually means the requested URL does not exist. However, in this case the endpoint exists, but the JSON contains an invalid linked reference. For example, posting a sensor with a roomId that does not exist.

The request format is valid, but the data inside it cannot be processed. Therefore, 422 Unprocessable Entity is more accurate than 404. It tells the client that the request reached the correct endpoint but the supplied data is invalid. This provides clearer feedback about the nature of the error.

### Part 5.4 — Risks of Exposing Java Stack Traces

Showing internal stack traces to users can create security risks. Exposed stack traces may reveal package names, frameworks, or server paths, which could help attackers identify weaknesses.

Attackers may learn package names, class names, server structure, framework details, file paths, and internal logic flow. This information could help someone target vulnerabilities. For example, if an error response exposes a framework version like "Jersey 3.1.3", an attacker may search CVE databases for known security issues affecting that version and attempt to exploit them.

To prevent this, my project uses a global exception mapper that returns a clean JSON error response with generic messages. The real error details stay only in server logs, where developers can review them safely.

### Part 5.5 — Why Logging Filters are Better Than Manual Logger Statements

Using JAX-RS filters for logging is better than writing Logger.info() inside every method. Filters provide automatic coverage, log every request automatically, log every response status automatically, keep resource classes clean, avoid repeated code, and are easy to maintain.

In my project, the logging filter records incoming requests and outgoing responses centrally. This is much cleaner than manually writing logs in every endpoint. The DRY principle is preserved, and logging format changes can be made in one place rather than updating dozens of methods.

## Project Structure

```
Smart_Campus_API/
├── src/
│   ├── main/
│   │   ├── java/uk/ac/westminster/cosc022w/smartcampus/api/
│   │   │   ├── SmartCampusApplication.java
│   │   │   ├── models/
│   │   │   │   ├── DiscoveryResponse.java
│   │   │   │   ├── ErrorMessage.java
│   │   │   │   ├── Room.java
│   │   │   │   ├── Sensor.java
│   │   │   │   └── SensorReading.java
│   │   │   ├── resources/
│   │   │   │   ├── DiscoveryResource.java
│   │   │   │   ├── RoomResource.java
│   │   │   │   ├── SensorResource.java
│   │   │   │   └── SensorReadingResource.java
│   │   │   ├── store/
│   │   │   │   └── InMemoryStore.java
│   │   │   ├── exceptions/
│   │   │   │   ├── RoomNotEmptyException.java
│   │   │   │   ├── LinkedResourceNotFoundException.java
│   │   │   │   └── SensorUnavailableException.java
│   │   │   ├── mappers/
│   │   │   │   ├── GenericThrowableMapper.java
│   │   │   │   ├── LinkedResourceNotFoundMapper.java
│   │   │   │   ├── RoomNotEmptyMapper.java
│   │   │   │   ├── SensorUnavailableMapper.java
│   │   │   │   └── WebApplicationExceptionMapper.java
│   │   │   └── filters/
│   │   │       └── ApiLoggingFilter.java
│   │   └── webapp/
│   │       └── WEB-INF/
│   │           └── web.xml
├── pom.xml
├── .gitignore
└── README.md
```

## Testing & Validation

The API has been tested with 17 comprehensive Postman test cases covering:

- CRUD operations: Create (201), Read (200), Delete (204)
- Business logic constraints: Room deletion with sensors (409)
- Data validation: Invalid linked references (422)
- State constraints: Maintenance sensor restrictions (403)
- Content negotiation: Wrong Content-Type rejection (415)
- Query filtering: Sensor type filtering with optional parameters
- Sub-resource nesting: Reading history under sensors
- Side effects: Parent sensor currentValue updates after reading POST
- Error scenarios: 404 for missing resources, 500 for unexpected errors
- Logging observability: All requests/responses logged at framework level

All tests pass successfully and demonstrate correct HTTP status codes, JSON response bodies, and error handling.

---

