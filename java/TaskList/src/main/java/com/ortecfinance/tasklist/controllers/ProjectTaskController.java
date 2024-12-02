package com.ortecfinance.tasklist.controllers;

import com.ortecfinance.tasklist.DTO.Task.TaskRequest;
import com.ortecfinance.tasklist.DTO.Task.TaskResponse;
import com.ortecfinance.tasklist.services.ProjectService;
import com.ortecfinance.tasklist.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/tasks")
public class ProjectTaskController {

    private final TaskService taskService;

    private final ProjectService projectService;

    ProjectTaskController(TaskService taskService, ProjectService projectService) {
        this.taskService = taskService;
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<?> addTask(@Valid @RequestBody TaskRequest task, @PathVariable("projectId") long projectId, @RequestParam(value = "includeResults", required = false) boolean includeResults) {
        if (projectService.findProjectById(projectId) == null) {
            return ResponseEntity.badRequest().body("Project not found");
        }
        TaskResponse taskResponse = taskService.addTask(task, projectId);
        if (includeResults) {
            return ResponseEntity.ok(taskResponse);
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(
            @PathVariable("projectId") long projectId,
            @PathVariable("taskId") long taskId,
            @RequestParam(value = "deadline", required = false) String deadline,
            @RequestParam(value = "done", required = false) Boolean isDone, @RequestParam(value = "includeResults", required = false) boolean includeResults) {

        if (projectService.findProjectById(projectId) == null) {
            return ResponseEntity.badRequest().body("Project not found");
        }

        if (deadline == null && isDone == null) {
            return ResponseEntity.badRequest().body("Either 'deadline' or 'done' must be provided.");
        }
        TaskResponse taskResponse = new TaskResponse();
        if (deadline != null) {
            taskResponse = taskService.setDeadline(projectId, taskId, deadline);
        }
        if (isDone != null) {
            taskResponse = taskService.updateStateTask(projectId, taskId, isDone);
        }
        if (includeResults) {
            return ResponseEntity.ok(taskResponse);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> findTasksByProjectId(@PathVariable("projectId") long projectId) {
        if (projectService.findProjectById(projectId) == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(taskService.findTasksByProjectId(projectId));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> findTaskByProjectIdAndId(@PathVariable("projectId") long projectId, @PathVariable("taskId") long taskId) {
        return ResponseEntity.ok(taskService.findTaskByProjectIdAndId(projectId, taskId));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> removeTask(@PathVariable("projectId") long projectId, @PathVariable("taskId") long taskId, @RequestParam(value = "includeResults", required = false) boolean includeResults) {
        TaskResponse taskResponse = taskService.removeTask(projectId, taskId);
        if (includeResults) {
            return ResponseEntity.ok(taskResponse);
        }
        return ResponseEntity.ok().build();
    }

}
