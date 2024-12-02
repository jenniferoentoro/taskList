package com.ortecfinance.tasklist.controllers;

import com.ortecfinance.tasklist.DTO.Project.ProjectDTO;
import com.ortecfinance.tasklist.DTO.Project.ProjectDeadlineResponse;
import com.ortecfinance.tasklist.DTO.Project.ProjectResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskDeadlineResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskWithProjectResponse;
import com.ortecfinance.tasklist.services.ProjectService;
import com.ortecfinance.tasklist.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private ProjectController projectController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
    }

    @Test
    void findAllProjects_ShouldReturnProjects() throws Exception {
        // Given
        TaskWithProjectResponse taskWithProjectResponse = new TaskWithProjectResponse(1L, "Project 1", null);
        when(projectService.findAllTasksGroupedByProject()).thenReturn(Arrays.asList(taskWithProjectResponse));

        // When & Then
        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Project 1"));
    }

    @Test
    void findProjectById_ShouldReturnProject() throws Exception {
        // Given
        ProjectResponse projectResponse = new ProjectResponse(1L, "Project 1");
        when(projectService.findProjectById(1L)).thenReturn(projectResponse);

        // When & Then
        mockMvc.perform(get("/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Project 1"))
                .andExpect(jsonPath("$.description").value("Description"));
    }

    @Test
    void removeProject_ShouldReturnOkWithoutResults() throws Exception {
        // Given
        ProjectResponse projectResponse = new ProjectResponse(1L, "Project 1");
        when(projectService.removeProject(1L)).thenReturn(projectResponse);

        // When & Then
        mockMvc.perform(delete("/projects/1").param("includeResults", "false"))
                .andExpect(status().isOk());
    }

    @Test
    void removeProject_ShouldReturnOkWithResults() throws Exception {
        // Given
        ProjectResponse projectResponse = new ProjectResponse(1L, "Project 1");
        when(projectService.removeProject(1L)).thenReturn(projectResponse);

        // When & Then
        mockMvc.perform(delete("/projects/1").param("includeResults", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Project 1"));
    }

    @Test
    void addProject_ShouldReturnOkWithoutResults() throws Exception {
        // Given
        ProjectDTO projectDTO = new ProjectDTO("Project 1");
        ProjectResponse projectResponse = new ProjectResponse(1L, "Project 1");
        when(projectService.addProject(projectDTO)).thenReturn(projectResponse);

        // When & Then
        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Project 1\", \"description\": \"Description\"}")
                        .param("includeResults", "false"))
                .andExpect(status().isOk());
    }

    @Test
    void addProject_ShouldReturnOkWithResults() throws Exception {
        // Given
        ProjectDTO projectDTO = new ProjectDTO("Project 1");
        ProjectResponse projectResponse = new ProjectResponse(1L, "Project 1");
        when(projectService.addProject(projectDTO)).thenReturn(projectResponse);

        // When & Then
        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Project 1\", \"description\": \"Description\"}")
                        .param("includeResults", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Project 1"));
    }

//    @Test
//    void findAllTasksGroupedByProjectWithDeadlines_ShouldReturnTasksGroupedByProjectWithDeadlines() throws Exception {
//        // Given
//        ProjectDeadlineResponse projectDeadlineResponse = new ProjectDeadlineResponse("01-01-2025", null);
//        when(taskService.findAllTasksGroupedByProjectWithDeadlines(null)).thenReturn(Arrays.asList(projectDeadlineResponse));
//
//        // When & Then
//        mockMvc.perform(get("/projects/view_by_deadline"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].deadline").value("01-01-2025"));
//    }
//
//    @Test
//    void findAllTasksWithDeadlines_ShouldReturnTasksWithDeadlines() throws Exception {
//        // Given
//        TaskDeadlineResponse taskDeadlineResponse = new TaskDeadlineResponse("01-01-2025", null);
//        when(taskService.findAllTasksWithDeadlines(null)).thenReturn(Arrays.asList(taskDeadlineResponse));
//
//        // When & Then
//        mockMvc.perform(get("/projects/all_with_deadlines"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].deadline").value("01-01-2025"));
//    }
}
