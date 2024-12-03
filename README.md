# taskList

## Assignment 3

### Features Added:

The codebase has been refactored to support multiple interfaces and follow a clean architecture.
The console application will start automatically after the Spring Boot application has booted up.

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

4. **Testing:**
   - It uses h2 database for testing.
   - Available for the console and service layers.
### List of Commands:
- `show`: Display all tasks.
- `add project <project name>`: Add a new project.
- `add task <project name> <task description>`: Add a task to a specific project.
- `check <task ID>`: Mark a task as completed.
- `uncheck <task ID>`: Mark a task as incomplete.
- `today`: Show all tasks for today.
- `deadline <task ID> <date>`: Add a deadline to a task.
- `view-by-deadline`: Show all tasks sorted by their deadline.
- `view-by-deadline-group`: Show all tasks grouped by both deadline and project.
- `quit`: Exit the application.


