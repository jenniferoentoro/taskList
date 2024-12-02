# taskList

## Assignment 3

### Features Added:

The codebase has been refactored to support multiple interfaces and follow a clean architecture.

#### Changes Made:

1. **Database Integration:**

   - Added a PostgreSQL database.
   - A `docker-compose` directory is included, allowing you to run the database with a single command (`docker-compose up`).
   - Alternatively, the database is already configured in the IntelliJ run configurations. Simply select and run it `Start DB`.

2. **Separation of Concerns:**

   - The codebase is now structured into distinct layers:
     - **Model:** Represents the application's data structure.
     - **Repository:** Handles data persistence and retrieval.
     - **Service:** Contains the core business logic.
     - **Controller:** Manages communication between the service layer and external interfaces (e.g., CLI, REST API).

3. **Support for DTOs:**
   - Data Transfer Objects (DTOs) have been introduced to streamline data exchange between layers.
