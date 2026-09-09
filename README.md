# E-Commerce Backend API

A backend REST API for a simplified e-commerce system, built with Spring Boot and PostgreSQL. This project focuses on solid backend fundamentals — proper layering, correct REST conventions, and deliberate data-modeling decisions — rather than breadth of features.

Built as a hands-on rebuild/deepening of Java backend skills after some time away from full-time development, with an emphasis on understanding *why* each design decision was made, not just implementing it.

## Tech Stack

- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA** (Hibernate)
- **PostgreSQL**
- **Maven**
- **Lombok**

## Current Features (Phase 1)

- **Product management** — full CRUD (create, read, update, delete)
- Layered architecture: **Controller → Service → Repository → Database**
- Proper HTTP status codes (`201` on create, `204` on delete, etc.)
- Database-level data integrity constraints (unique email, non-null fields, precise `BigDecimal` currency handling)
- Environment-variable-based configuration for secrets (no credentials committed to source control)

## In Progress

- Custom exception handling (`@ControllerAdvice`) for clean, correct error responses (e.g. `404` instead of a generic `500` for not-found resources)
- User registration/login
- Cart and order management, including checkout logic (stock validation, price snapshotting, order status transitions)
- Spring Security with JWT authentication
- Unit tests (JUnit 5 + Mockito)
- Dockerization and cloud deployment

## Design Notes

A few deliberate decisions worth calling out:

- **`BigDecimal` for all currency fields** — avoids floating-point rounding errors that `double`/`float` would introduce in financial calculations.
- **No service-layer interfaces** (e.g. `ProductService` is a plain class, not `ProductService` + `ProductServiceImpl`) — with only one implementation and no current need for polymorphism, an interface would add navigation overhead without real benefit. Would introduce one if the project needed swappable implementations.
- **Constructor injection over field injection** — keeps dependencies `final`, makes missing dependencies fail loudly at construction time, and simplifies unit testing.
- **Fetch-then-update pattern** for updates/deletes, rather than blindly saving/deleting by ID — protects against overwriting fields not included in a request payload, and ensures a clear "not found" error rather than a silent no-op.

## Running Locally

### Prerequisites
- Java 17+
- Maven
- PostgreSQL running locally, with a database created (e.g. `ecommerce_db`)

### Setup

1. Clone the repo:
   ```
   git clone https://github.com/akhil900619/ecommerce-backend-api.git
   ```

2. Set the required environment variable (see `.env.example` for reference):
   ```
   DB_PASSWORD=your_postgres_password
   ```

3. Update `src/main/resources/application.properties` if your database name, username, or port differ from the defaults.

4. Run the application:
   ```
   mvn spring-boot:run
   ```

The API will be available at `http://localhost:8080`.

## API Endpoints (Current)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/products` | Create a new product |
| GET | `/api/products` | List all products |
| GET | `/api/products/{id}` | Get a single product by ID |
| PUT | `/api/products/{id}` | Update a product |
| DELETE | `/api/products/{id}` | Delete a product |

More endpoints (users, cart, orders) are being added as the project progresses.

## Author

**Akhil Sharma**
[LinkedIn](https://linkedin.com/in/akhilsharma) · akhil900619@gmail.com
