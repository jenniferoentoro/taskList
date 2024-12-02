# taskList

## Assignment 4

### Features Added:

- Converted to a REST API application.

Postman collection can be found at: [Postman Collection](https://www.postman.com/payload-geoscientist-32253328/jennifer-ortec/collection/tkpuklj/project-tasklist?action=share&creator=26131816)

#### Database Integration:

- Added a PostgreSQL database.
- A `docker-compose` directory is included, allowing you to run the database with a single command (`docker-compose up`).
- Alternatively, the database is already configured in the IntelliJ run configurations. Simply select and run it `Start DB`.

#### Available Endpoints:

1. **Project:**

   - `(GET)` `/projects` = Get all projects with task details.
   - `(GET)` `/projects/{projectId}` = Get a project by ID.
   - `(DELETE)` `/projects?includeResults={boolean}` = Delete a project. Optional param: `includeResults` (set to `true` or `false` to show the result).
   - `(POST)` `/projects?includeResults={boolean}` = Add a new project. Optional param: `includeResults` (set to `true` or `false` to show the result).
   - `(GET)` `/projects/view_by_deadline?date={date}` = Find all tasks grouped by project with deadlines. Optional param (without it, all data is considered): `date` (format: `dd-MM-YYYY`, or use `today`).
   - `(GET)` `/projects/all_with_deadlines?date={date}` = Find all tasks with deadlines grouped. Optional param (without it, all data is considered): `date` (format: `dd-MM-YYYY`, or use `today`).

2. **Task:**
   - `(POST)` `/projects/{projectId}/tasks?includeResults={boolean}` = Add a new task to a project. Optional param: `includeResults` (set to `true` or `false` to show the result).
   - `(PUT)` `/projects/{projectId}/tasks/{taskId}?deadline={date}&done={boolean}&includeResults={boolean}` = Update a task's deadline or state. Optional param: `includeResults` (set to `true` or `false` to show the result).
   - `(GET)` `/projects/{projectId}/tasks/` = Find tasks by project ID.
   - `(GET)` `/projects/{projectId}/tasks/{taskId}` = Find a task by project ID and task ID.
   - `(DELETE)` `/projects/{projectId}/tasks/{taskId}?includeResults={boolean}` = Delete a task. Optional param: `includeResults` (set to `true` or `false` to show the result).

### Tests:

The project includes a JaCoCo test report.

To generate the report, follow these steps:

1. Run the tests: mvn test
2. Generate the JaCoCo report: mvn jacoco:report

The report will be available at:  
`target/site/jacoco/index.html`
