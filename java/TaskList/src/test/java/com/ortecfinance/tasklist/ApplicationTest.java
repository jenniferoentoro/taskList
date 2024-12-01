package com.ortecfinance.tasklist;

import org.junit.jupiter.api.*;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.lang.System.lineSeparator;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class ApplicationTest {
    public static final String PROMPT = "> ";
    private final PipedOutputStream inStream = new PipedOutputStream();
    private final PrintWriter inWriter = new PrintWriter(inStream, true);

    private final PipedInputStream outStream = new PipedInputStream();
    private final BufferedReader outReader = new BufferedReader(new InputStreamReader(outStream));

    private Thread applicationThread;

    public ApplicationTest() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new PipedInputStream(inStream)));
        PrintWriter out = new PrintWriter(new PipedOutputStream(outStream), true);
        TaskList taskList = new TaskList(in, out);
        applicationThread = new Thread(taskList);
    }

    @BeforeEach
    public void start_the_application() throws IOException {
        applicationThread.start();
        readLines("Welcome to TaskList! Type 'help' for available commands.");
    }

    @AfterEach
    public void kill_the_application() throws InterruptedException {
        if (!stillRunning()) {
            return;
        }

        Thread.sleep(1000);
        if (!stillRunning()) {
            return;
        }

        applicationThread.interrupt();
        throw new IllegalStateException("The application is still running.");
    }

    @Test
    void it_helps() throws IOException {
        execute("help");
        readLines(
                "Commands:",
                "  show",
                "  add project <project name>",
                "  add task <project name> <task description>",
                "  check <task ID>",
                "  uncheck <task ID>",
                "  today {show all tasks for today}",
                "  deadline <task ID> <date> {add a task with deadline}",
                "  quit",
                ""
        );

        execute("quit");
    }

    @Test
    void it_handles_invalid_commands() throws IOException {
        execute("unknown");
        readLines("I don't know what the command \"unknown\" is.");
        execute("quit");
    }

    @Test
    void it_adds_projects_and_displays_them() throws IOException {
        execute("add project secrets");
        execute("add project training");
        execute("show");
        readLines(
                "secrets",
                "",
                "training",
                ""
        );
        execute("quit");
    }

    @Test
    void it_adds_project_and_tasks() throws IOException {
        execute("show");

        execute("add project secrets");
        execute("add task secrets Eat more donuts.");
        execute("add task secrets Destroy all humans.");

        execute("show");
        readLines(
                "secrets",
                "    [ ] 1: Eat more donuts.",
                "    [ ] 2: Destroy all humans.",
                ""
        );
        execute("quit");
    }

    @Test
    void it_rejects_tasks_for_non_existent_project() throws IOException {
        execute("add task NonExistent description");
        readLines("Could not find a project with the name \"NonExistent\".");
        execute("quit");
    }

    @Test
    void it_marks_tasks_as_checked() throws IOException {
        execute("add project training");
        execute("add task training Four Elements of Simple Design");
        execute("add task training SOLID");
        execute("check 1");
        execute("check 2");
        execute("show");
        readLines(
                "training",
                "    [x] 1: Four Elements of Simple Design",
                "    [x] 2: SOLID",
                ""
        );
        execute("quit");
    }

    @Test
    void it_marks_tasks_as_unchecked() throws IOException {
        execute("add project training");
        execute("add task training Four Elements of Simple Design");
        execute("add task training SOLID");
        execute("uncheck 1");
        execute("uncheck 2");
        execute("show");
        readLines(
                "training",
                "    [ ] 1: Four Elements of Simple Design",
                "    [ ] 2: SOLID",
                ""
        );
        execute("quit");
    }

    @Test
    void it_marks_tasks_as_checked_and_unchecked() throws IOException {
        execute("add project training");
        execute("add task training Four Elements of Simple Design");
        execute("add task training SOLID");
        execute("check 1");
        execute("check 2");
        execute("uncheck 2");
        execute("show");
        readLines(
                "training",
                "    [x] 1: Four Elements of Simple Design",
                "    [ ] 2: SOLID",
                ""
        );
        execute("quit");
    }

    @Test
    void it_check_when_task_id_is_invalid() throws IOException {
        execute("add project training");
        execute("add task training Four Elements of Simple Design");
        execute("add task training SOLID");
        execute("check 3");
        readLines("Could not find a task with an ID of 3.");
        execute("quit");
    }

    @Test
    void it_displays_tasks_with_today_deadline() throws IOException {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        execute("add project training");
        execute("add task training Four Elements of Simple Design");
        execute("add task training SOLID");
        execute("deadline 1 " + today);
        execute("today");
        readLines(
                "training",
                "    [ ] 1: Four Elements of Simple Design | Deadline: " + today,
                ""
        );

        execute("add project secrets");
        execute("add task secrets Eat more donuts.");
        execute("deadline 3 " + today);

        execute("today");

        readLines(
                "training",
                "    [ ] 1: Four Elements of Simple Design | Deadline: " + today,
                "",
                "secrets",
                "    [ ] 3: Eat more donuts. | Deadline: " + today,
                ""
        );
        execute("quit");
    }

    @Test
    void it_handles_tasks_with_non_existent_deadline_today() throws IOException {
        execute("add project training");
        execute("add task training Four Elements of Simple Design");
        execute("add task training SOLID");
        execute("deadline 1 20-11-2024");
        execute("today");
        execute("quit");
    }

    @Test
    void it_handles_non_existent_deadline_today() throws IOException {
        execute("today");
        execute("quit");
    }

    @Test
    void it_display_tasks_with_various_deadlines() throws IOException {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        execute("add project training");
        execute("add task training Four Elements of Simple Design");
        execute("add task training SOLID");
        execute("add task training Coupling and Cohesion");
        execute("deadline 1 20-10-2024");
        execute("deadline 2 " + today);
        execute("deadline 3 01-01-2025");
        execute("show");
        readLines(
                "training",
                "    [ ] 1: Four Elements of Simple Design | Deadline: 20-10-2024",
                "    [ ] 2: SOLID | Deadline: " +today,
                "    [ ] 3: Coupling and Cohesion | Deadline: 01-01-2025",
                ""
        );
        execute("quit");
    }

    @Test
    void it_handles_invalid_deadlines() throws IOException {
        execute("add project training");
        execute("add task training Four Elements of Simple Design");
        execute("add task training SOLID");
        execute("deadline 1 invalid-date");
        readLines("Invalid date format.");
        execute("quit");
    }

    @Test
    void it_handles_invalid_date_deadlines() throws IOException {
        execute("add project training");
        execute("add task training Four Elements of Simple Design");
        execute("add task training SOLID");
        execute("deadline 1 22-22-2024");
        readLines("Invalid date format.");
        execute("quit");
    }

    @Test
    void it_complete_works() throws IOException {
        // auto detect today's date
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        execute("show");

        // Add project secrets and the tasks
        execute("add project secrets");
        execute("add task secrets Eat more donuts.");
        execute("add task secrets Destroy all humans.");

        // Show the tasks
        execute("show");
        readLines(
                "secrets",
                "    [ ] 1: Eat more donuts.",
                "    [ ] 2: Destroy all humans.",
                ""
        );

        // Add project training and the tasks
        execute("add project training");
        execute("add task training Four Elements of Simple Design");
        execute("add task training SOLID");

        // Add deadline for task 3
        execute("deadline 3 20-10-2024");

        execute("show");
        readLines(
                "secrets",
                "    [ ] 1: Eat more donuts.",
                "    [ ] 2: Destroy all humans.",
                "",
                "training",
                "    [ ] 3: Four Elements of Simple Design | Deadline: 20-10-2024",
                "    [ ] 4: SOLID",
                ""
        );

        // Add more tasks to training project
        execute("add task training Coupling and Cohesion");
        execute("add task training Primitive Obsession");
        execute("add task training Outside-In TDD");
        execute("add task training Interaction-Driven Design");

        // Check few tasks
        execute("check 1");
        execute("check 3");
        execute("check 5");
        execute("check 6");

        //Show the tasks that have today's deadline
        execute("today"); // no tasks with deadline today yet

        // Setting deadline for task 1 and 6 to today
        execute("deadline 1 " + today);
        execute("deadline 6 " + today);

        // Setting deadline for task 5 to 30-12-2024
        execute("deadline 5 30-12-2024");

        // Uncheck task 5
        execute("uncheck 5");

        // Show the tasks
        execute("show");
        readLines(
                "secrets",
                "    [x] 1: Eat more donuts. | Deadline: " + today,
                "    [ ] 2: Destroy all humans.",
                "",
                "training",
                "    [x] 3: Four Elements of Simple Design | Deadline: 20-10-2024",
                "    [ ] 4: SOLID",
                "    [ ] 5: Coupling and Cohesion | Deadline: 30-12-2024",
                "    [x] 6: Primitive Obsession | Deadline: " + today,
                "    [ ] 7: Outside-In TDD",
                "    [ ] 8: Interaction-Driven Design",
                ""
        );

        // Show the tasks that have today's deadline
        execute("today");
        readLines(
                "secrets",
                "    [x] 1: Eat more donuts. | Deadline: " + today,
                "",
                "training",
                "    [x] 6: Primitive Obsession | Deadline: " + today,
                ""
        );
        execute("quit");
    }

    private void execute(String command) throws IOException {
        read(PROMPT);
        write(command);
    }

    private void read(String expectedOutput) throws IOException {
        int length = expectedOutput.length();
        char[] buffer = new char[length];
        outReader.read(buffer, 0, length);
        assertThat(String.valueOf(buffer), is(expectedOutput));
    }

    private void readLines(String... expectedOutput) throws IOException {
        for (String line : expectedOutput) {
            read(line + lineSeparator());
        }
    }

    private void write(String input) {
        inWriter.println(input);
    }

    private boolean stillRunning() {
        return applicationThread != null && applicationThread.isAlive();
    }
}
