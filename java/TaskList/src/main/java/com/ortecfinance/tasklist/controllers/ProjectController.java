package com.ortecfinance.tasklist.controllers;

import com.ortecfinance.tasklist.DTO.Project.ProjectDTO;
import com.ortecfinance.tasklist.DTO.Project.ProjectDeadlineResponse;
import com.ortecfinance.tasklist.DTO.Project.ProjectResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskDeadlineResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskWithProjectResponse;
import com.ortecfinance.tasklist.services.ProjectService;
import com.ortecfinance.tasklist.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final TaskService taskService;

    ProjectController(ProjectService projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskWithProjectResponse>> findAllProjects() {
        return ResponseEntity.ok(projectService.findAllTasksGroupedByProject());
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> findProjectById(@PathVariable("projectId") long projectId) {
        return ResponseEntity.ok(projectService.findProjectById(projectId));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> removeProject(@PathVariable("projectId") long projectId, @RequestParam(value = "includeResults", required = false) boolean includeResults) {
        ProjectResponse projectResponse = projectService.removeProject(projectId);
        if (includeResults) {
            return ResponseEntity.ok(projectResponse);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> addProject(@Valid @RequestBody ProjectDTO project, @RequestParam(value = "includeResults", required = false) boolean includeResults) {
        ProjectResponse projectResponse = projectService.addProject(project);
        if (includeResults) {
            return ResponseEntity.ok(projectResponse);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/view_by_deadline")
    public ResponseEntity<List<ProjectDeadlineResponse>> findAllTasksGroupedByProjectWithDeadlines(@RequestParam(value = "date", required = false) String date) {
        List<ProjectDeadlineResponse> response = taskService.findAllTasksGroupedByProjectWithDeadlines(date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all_with_deadlines")
    public ResponseEntity<List<TaskDeadlineResponse>> findAllTasksWithDeadlines(@RequestParam(value = "date", required = false) String date) {
        return ResponseEntity.ok(taskService.findAllTasksWithDeadlines(date));
    }

}
