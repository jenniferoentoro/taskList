package com.ortecfinance.tasklist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public final class TaskList implements Runnable {
    private static final String QUIT = "quit";

    private final Map<String, List<Task>> tasks = new LinkedHashMap<>();
    private final BufferedReader in;
    private final PrintWriter out;

    private long lastId = 0;


    public static void startConsole() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);
        new TaskList(in, out).run();
    }

    public TaskList(BufferedReader reader, PrintWriter writer) {
        this.in = reader;
        this.out = writer;
    }

    public void run() {
        out.println("Welcome to TaskList! Type 'help' for available commands.");
        while (true) {
            out.print("> ");
            out.flush();
            String command;

            try {
                command = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (command.equals(QUIT)) {
                break;
            }
            execute(command);
        }
    }

    private void execute(String commandLine) {
        String[] commandRest = commandLine.split(" ", 2);
        String command = commandRest[0];
        switch (command) {
            case "show" -> show();
            case "add" -> add(commandRest[1]);
            case "deadline" -> deadline(commandRest[1]);
            case "today" -> showToday();
            case "check" -> checkUnCheck(commandRest[1], "true");
            case "uncheck" -> checkUnCheck(commandRest[1], "false");
            case "help" -> help();
            case "view-by-deadline" -> viewByDeadline();
            case "view-by-deadline-group" -> viewByDeadlineGroup();
            default -> error(command);
        }
    }

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    private static final String todayDate = dateFormatter.format(new Date());

    /***
     * View all tasks by deadline (Group tasks by deadline)
     * Print tasks with no deadline at the end
     */
    private void viewByDeadline() {

        // Group tasks by deadline
        Map<String, List<Task>> groupedTasks = tasks.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.groupingBy(
                        // Group by deadline if available, else group by "No deadline"
                        task -> task.getDeadline() != null
                                ? dateFormatter.format(task.getDeadline())
                                : "No deadline",
                        () -> new TreeMap<>((key1, key2) -> {
                            // Sort by deadline, with "No deadline" at the end
                            if ("No deadline".equals(key1)) return 1;
                            if ("No deadline".equals(key2)) return -1;
                            try {
                                // Parse and compare dates
                                return dateFormatter.parse(key1).compareTo(dateFormatter.parse(key2));
                            } catch (ParseException e) {
                                throw new RuntimeException("Invalid date format", e);
                            }
                        }),
                        // Collect tasks
                        Collectors.toList()));

        // Print tasks grouped by deadline (as desired output)
        groupedTasks.forEach((deadline, tasks) -> {
            out.printf("%s:%n", deadline);
            tasks.forEach(task -> out.printf("    [%c] %d: %s%n", (task.isDone() ? 'x' : ' '), task.getId(), task.getDescription()));
        });
        out.println();
    }

    /***
     * View all tasks by deadline and project
     * Print tasks with no deadline at the end
     */
    private void viewByDeadlineGroup() {
        // Group tasks by deadline and project
        Map<String, Map<String, List<Task>>> groupedTasks = tasks.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().map(task -> Map.entry(entry.getKey(), task)))
                .collect(Collectors.groupingBy(
                        // Group by deadline if available, else group by "No deadline"
                        entry -> entry.getValue().getDeadline() != null
                                ? dateFormatter.format(entry.getValue().getDeadline())
                                : "No deadline",
                        () -> new TreeMap<>((key1, key2) -> {
                            // Sort by deadline, with "No deadline" at the end
                            if ("No deadline".equals(key1)) return 1;
                            if ("No deadline".equals(key2)) return -1;
                            try {
                                // Parse and compare dates
                                return dateFormatter.parse(key1).compareTo(dateFormatter.parse(key2));
                            } catch (ParseException e) {
                                throw new RuntimeException("Invalid date format", e);
                            }
                        }),
                        Collectors.groupingBy(Map.Entry::getKey,
                                Collectors.mapping(Map.Entry::getValue, Collectors.toList()))
                ));

        // Print tasks grouped by deadline and project (as desired output)
        groupedTasks.forEach((deadline, projects) -> {
            out.printf("%s:%n", deadline);
            projects.forEach((project, tasks) -> {
                out.printf("    %s:%n", project);
                tasks.forEach(task -> out.printf("    [%c] %d: %s%n", (task.isDone() ? 'x' : ' '), task.getId(), task.getDescription()));
            });
        });
        out.println();
    }


    private void printTask(Task task) {
        if (task.getDeadline() != null) {
            String formattedDeadline = dateFormatter.format(task.getDeadline());
            out.printf("    [%c] %d: %s | Deadline: %s%n", (task.isDone() ? 'x' : ' '), task.getId(), task.getDescription(), formattedDeadline);
        } else {
            out.printf("    [%c] %d: %s%n", (task.isDone() ? 'x' : ' '), task.getId(), task.getDescription());
        }
    }

    private void show() {
        tasks.forEach((projectName, taskList) -> {
            out.println(projectName);
            taskList.forEach(this::printTask);
            out.println();
        });
    }

    private void showToday() {
        tasks.forEach((projectName, taskList) -> {
            taskList.stream()
                    .filter(task -> task.getDeadline() != null && todayDate.equals(dateFormatter.format(task.getDeadline())))
                    .findFirst()
                    .ifPresent(task -> {
                        //print project key if there is a task with deadline today
                        out.println(projectName);
                        taskList.stream()
                                .filter(t -> t.getDeadline() != null && todayDate.equals(dateFormatter.format(t.getDeadline())))
                                .forEach(this::printTask);
                        out.println();
                    });
        });
    }


    private Date parseDate(String date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            formatter.setLenient(false);
            return formatter.parse(date);
        } catch (ParseException e) {
            out.println("Invalid date format.");
            return null;
        }
    }

    private String[] splitCommandLine(String commandLine) {
        return commandLine.split(" ", 2);
    }

    private void add(String commandLine) {
        String[] subcommandRest = splitCommandLine(commandLine);

        switch (subcommandRest[0].toLowerCase()) {
            case "project" -> addProject(subcommandRest[1]);
            case "task" -> {
                String[] projectTask = subcommandRest[1].split(" ", 2);
                addTask(projectTask[0], projectTask[1]);
            }
        }
    }

    private void deadline(String commandLine) {
        String[] subcommandRest = splitCommandLine(commandLine);
        updateTask(subcommandRest[0], "deadline", subcommandRest[1]);
    }

    private void addProject(String name) {
        tasks.put(name, new ArrayList<Task>());
    }

    private void addTask(String project, String description) {
        List<Task> projectTasks = tasks.get(project);
        if (projectTasks == null) {
            out.printf("Could not find a project with the name \"%s\".", project);
            out.println();
            return;
        }
        projectTasks.add(new Task(nextId(), description, false));
    }

    private void checkUnCheck(String idString, String isDone) {
        updateTask(idString, "done", isDone);
    }


    private void updateTask(String idString, String action, String dateOrDone) {
        int id = Integer.parseInt(idString);
        for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
            for (Task task : project.getValue()) {
                if (task.getId() == id) {
                    switch (action.toLowerCase()) {
                        case "deadline" -> {
                            task.setDeadline(parseDate(dateOrDone));
                            return;
                        }
                        case "done" -> {
                            task.setDone(Boolean.parseBoolean(dateOrDone));
                            return;
                        }
                        default -> {
                            out.println("Unknown action: " + action);
                            return;
                        }
                    }
                }
            }
        }
        out.printf("Could not find a task with an ID of %d.%n", id);
    }

    private void help() {
        out.println("Commands:");
        out.println("  show");
        out.println("  add project <project name>");
        out.println("  add task <project name> <task description>");
        out.println("  check <task ID>");
        out.println("  uncheck <task ID>");
        out.println("  today {show all tasks for today}");
        out.println("  deadline <task ID> <date> {add a task with deadline}");
        out.println("  view-by-deadline {show all tasks by deadline}");
        out.println("  view-by-deadline-group {show all tasks by deadline and project}");
        out.println("  quit");
        out.println();
    }

    private void error(String command) {
        out.printf("I don't know what the command \"%s\" is.", command);
        out.println();
    }

    private long nextId() {
        return ++lastId;
    }
}
