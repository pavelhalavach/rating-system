# Rating System

A Java-based REST API for managing sellers, comments, ratings, and games.

Implemented functionality

- Seller registration and approval workflow
- Comment submission and moderation
- Rating calculation for sellers
- Top sellers by game categories
- Filtering sellers by games and rating ranges
- User registration with email confirmation (uses Redis)
- Password reset flow
- Full REST API for sellers, comments, and game objects

Technologies

- Java
- Spring Boot (likely — check project files)
- Redis (for email confirmation tokens)
- SMTP for email delivery

Quick start

1. Clone the repository

   git clone https://github.com/pavelhalavach/rating-system.git
   cd rating-system

2. Build the project

   Use the project's build tool (Maven or Gradle). For Maven:

   mvn clean package

3. Configuration

   The application requires configuration for Redis and email SMTP. Check `src/main/resources/application.properties` or `application.yml` for exact property names. Common environment variables / properties:

   - spring.redis.host (or REDIS_HOST)
   - spring.redis.port (or REDIS_PORT)
   - spring.mail.host (SMTP_HOST)
   - spring.mail.port (SMTP_PORT)
   - spring.mail.username (SMTP_USERNAME)
   - spring.mail.password (SMTP_PASSWORD)
   - server.port (APP_PORT)

   You can set these as environment variables or in a local `application-local.properties` for development.

4. Run

   Run with Maven:

   mvn spring-boot:run

   Or run the packaged jar:

   java -jar target/*.jar

5. API overview

   Endpoints (inspect controller classes for exact paths and request/response shapes):

   - /api/sellers — register, list, get, update, approve
   - /api/comments — create, list, moderate
   - /api/games — list games and categories
   - /api/auth — register, login, confirm-email, password-reset

   The API exposes filtering by game and rating range, and endpoints for top sellers by game category.

Testing

- Run unit and integration tests with the build tool (e.g., `mvn test`).

Development notes

- Redis is used for temporary tokens (email confirmation). Ensure Redis is available during integration testing.
- Email delivery requires valid SMTP settings; for local development consider using a service like MailHog or Mailtrap.
- Ratings are aggregated server-side — review the rating calculation logic and database queries for performance when scaling.

Contributing

Contributions are welcome. Please open issues or pull requests. Follow existing code style and add tests for new features.

License

No license file is present in the repository. Add a LICENSE file if you plan to publish this project publicly.

---

If you want, I can further tailor this README by reading project files (pom.xml or build.gradle and application.properties/yml) and adding exact build/run instructions and endpoint examples. Would you like me to inspect those files and update the README with concrete commands and configuration keys?