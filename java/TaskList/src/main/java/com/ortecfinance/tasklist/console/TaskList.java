package com.ortecfinance.tasklist.console;

import com.ortecfinance.tasklist.DTO.Project.ProjectDTO;
import com.ortecfinance.tasklist.DTO.Project.ProjectDeadlineResponse;
import com.ortecfinance.tasklist.DTO.Project.ProjectResponse;
import com.ortecfinance.tasklist.DTO.Task.*;
import com.ortecfinance.tasklist.services.ProjectService;
import com.ortecfinance.tasklist.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class TaskList implements Runnable {

    private static final String QUIT = "quit";

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy");

    private final BufferedReader in;
    private final PrintWriter out;
    private final ProjectService projectService;
    private final TaskService taskService;

    @Autowired
    public TaskList(ProjectService projectService, TaskService taskService) {
        this.in = new BufferedReader(new InputStreamReader(System.in));
        this.out = new PrintWriter(System.out);
        this.projectService = projectService;
        this.taskService = taskService;
    }

    public static void startConsole(TaskList taskList) {
        taskList.run();
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
            case "check" -> checkUnCheck(commandRest[1], true);
            case "uncheck" -> checkUnCheck(commandRest[1], false);
            case "help" -> help();
            case "view-by-deadline" -> viewByDeadline();
            case "view-by-deadline-group" -> viewByDeadlineGroup();
            default -> error(command);
        }
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

    private String formatDeadline(Date deadline) {
        return deadline != null ? DATE_FORMATTER.format(deadline) : "No deadline";
    }

    private void viewByDeadline() {
        taskService.findAllTasksWithDeadlines(null).forEach(taskDeadlineResponse -> {
            String deadlineText = formatDeadline(taskDeadlineResponse.getDeadline());
            out.println(deadlineText);

            taskDeadlineResponse.getTask().forEach(task -> printTask(task, deadlineText));
            out.println();
        });
    }


    private void viewByDeadlineGroup() {
        taskService.findAllTasksGroupedByProjectWithDeadlines(null).forEach(projectDeadlineResponse -> {
            String deadlineText = formatDeadline(projectDeadlineResponse.getDeadline());
            out.println(deadlineText);

            projectDeadlineResponse.getProject().forEach(taskWithProjectResponse -> {
                out.println("  " + taskWithProjectResponse.getName());
                taskWithProjectResponse.getTasks().forEach(task -> printTask(task, deadlineText));
            });
            out.println();
        });
    }

    private void show() {
        projectService.findAllTasksGroupedByProject().forEach(taskWithProjectResponse -> {
            out.println(taskWithProjectResponse.getName());
            taskWithProjectResponse.getTasks().forEach(task -> printTask(task, null));
            out.println();
        });
    }

    private void showToday() {
        taskService.findAllTasksGroupedByProjectWithDeadlines("today").forEach(projectDeadlineResponse -> {
            String deadlineText = formatDeadline(projectDeadlineResponse.getDeadline());
            out.println(deadlineText);

            projectDeadlineResponse.getProject().forEach(taskWithProjectResponse -> {
                out.println("  " + taskWithProjectResponse.getName());
                taskWithProjectResponse.getTasks().forEach(task -> printTask(task, deadlineText));
            });
            out.println();
        });
    }

    private void printTask(TaskBaseDTO task, String deadline) {
        if (task instanceof TaskResponse) {
            deadline = ((TaskResponse) task).getDeadline() != null
                    ? DATE_FORMATTER.format(((TaskResponse) task).getDeadline())
                    : "No deadline";
        }

        if (deadline.equals("No deadline")) {
            out.printf("    [%c] %d: %s%n",
                    (task.isDone() ? 'x' : ' '), task.getId(), task.getDescription());
        } else {
            out.printf("    [%c] %d: %s | Deadline: %s%n",
                    (task.isDone() ? 'x' : ' '), task.getId(), task.getDescription(), deadline);
        }
    }

    private String[] splitCommandLine(String commandLine) {
        return commandLine.split(" ", 2);
    }

    private void add(String commandLine) {
        String[] subcommandRest = splitCommandLine(commandLine);
        switch (subcommandRest[0].toLowerCase()) {
            case "project" -> {
                addProject(subcommandRest[1]);
            }
            case "task" -> {
                String[] projectTask = subcommandRest[1].split(" ", 2);
                addTask(projectTask[0], projectTask[1]);
            }
        }
    }

    private void addProject(String projectName) {
        ProjectResponse projectResponse = projectService.findProjectByName(projectName);

        if (projectResponse != null) {
            out.println("Project with the same name already exists");
            return;
        }

        projectService.addProject(new ProjectDTO(projectName));
    }

    private void deadline(String commandLine) {
        String[] subcommandRest = splitCommandLine(commandLine);
        long id = Long.parseLong(subcommandRest[0]);
        TaskResponse taskResponse = taskService.findTaskById(id);

        if (taskResponse == null) {
            out.println("Task not found");
            return;
        }

        taskService.setDeadline(Long.parseLong(subcommandRest[0]), subcommandRest[1]);

    }

    private void addTask(String project, String description) {
        ProjectResponse projectResponse = projectService.findProjectByName(project);
        if (projectResponse == null) {
            out.printf("Project %s not found%n", project);
            return;
        }
        taskService.addTask(new TaskRequest(description), projectResponse.getId());
    }


    private void checkUnCheck(String idString, boolean isDone) {
        long id = Long.parseLong(idString);
        TaskResponse taskResponse = taskService.findTaskById(id);
        if (taskResponse == null) {
            out.println("Task not found");
            return;
        }
        taskService.updateStateTask(id, isDone);
    }

}
