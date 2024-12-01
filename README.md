# taskList

## Assignment 2

### Features Added:

- **View Tasks by Deadline**

  - **Command:** `view-by-deadline`
  - **Function:** Display all tasks grouped by their deadline in chronological order (tasks without a deadline are listed under a "No deadline" block at the end).
  - **Example Output:**
    ```
    11-11-2021:
          1: Eat more donuts.
          4: Four Elements of Simple Design
    13-11-2021:
          3: Interaction-Driven Design
    No deadline:
          2: Refactor the codebase
    ```

- **Bonus: Group by Project**
  - **Command:** `view-by-deadline-group`
  - **Function:** Display all tasks grouped by their project and deadline in chronological order.
  - **Example Output:**
    ```
    11-11-2021:
        Secrets:
            1: Eat more donuts.
        Training:
            4: Four Elements of Simple Design
    13-11-2021:
        Training:
            3: Interaction-Driven Design
    No deadline:
        Training:
            2: Refactor the codebase
    ```

### Tests:

- Added test cases to verify tasks are correctly grouped by deadline.
- Verified the chronological order and ensured the "No deadline" block appears last.
- Validated the grouping by project within each deadline for the bonus feature.
- Confirmed no existing functionality is broken.
