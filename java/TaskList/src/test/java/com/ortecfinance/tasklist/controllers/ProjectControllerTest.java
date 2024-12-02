package com.ortecfinance.tasklist.controllers;

import com.ortecfinance.tasklist.DTO.Project.ProjectDTO;
import com.ortecfinance.tasklist.DTO.Project.ProjectDeadlineResponse;
import com.ortecfinance.tasklist.DTO.Project.ProjectResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskBaseDTO;
import com.ortecfinance.tasklist.DTO.Task.TaskDeadlineResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskWithProjectResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskWithProjectResponseNoDeadline;
import com.ortecfinance.tasklist.services.ProjectService;
import com.ortecfinance.tasklist.services.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private TaskService taskService;

    @Test
    void findAllProjects_ShouldReturnProjects() throws Exception {
        TaskWithProjectResponse taskWithProjectResponse = new TaskWithProjectResponse(1L, "Project 1", null);
        when(projectService.findAllTasksGroupedByProject()).thenReturn(Arrays.asList(taskWithProjectResponse));

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Project 1"));
    }

    @Test
    void removeProject_ShouldReturnOkWithoutResults() throws Exception {
        ProjectResponse projectResponse = new ProjectResponse(1L, "Project 1");
        when(projectService.removeProject(1L)).thenReturn(projectResponse);

        mockMvc.perform(delete("/projects/1").param("includeResults", "false"))
                .andExpect(status().isOk());
    }

    @Test
    void removeProject_ShouldReturnOkWithResults() throws Exception {
        ProjectResponse projectResponse = new ProjectResponse(1L, "Project 1");
        when(projectService.removeProject(1L)).thenReturn(projectResponse);

        mockMvc.perform(delete("/projects/1").param("includeResults", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Project 1"));
    }

    @Test
    void addProject_ShouldReturnOkWithoutResults() throws Exception {
        ProjectDTO projectDTO = new ProjectDTO("Project 1");
        ProjectResponse projectResponse = new ProjectResponse(1L, "Project 1");
        when(projectService.addProject(projectDTO)).thenReturn(projectResponse);

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Project 1\", \"description\": \"Description\"}")
                        .param("includeResults", "false"))
                .andExpect(status().isOk());
    }

    @Test
    void addProject_ShouldReturnOkWithResults() throws Exception {
        ProjectDTO projectDTO = new ProjectDTO("Project 1");
        ProjectResponse projectResponse = new ProjectResponse(1L, "Project 1");
        when(projectService.addProject(projectDTO)).thenReturn(projectResponse);

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Project 1\", \"description\": \"Description\"}")
                        .param("includeResults", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void addProject_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        // Missing required fields or invalid data should trigger a bad request
        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}")) // Invalid name
                .andExpect(status().isBadRequest());
    }


    @Test
    void findProjectById_ShouldReturnProject() throws Exception {
        TaskWithProjectResponse taskWithProjectResponse = new TaskWithProjectResponse(1L, "Project 1", null);
        when(projectService.findProjectById(1L)).thenReturn(taskWithProjectResponse);

        mockMvc.perform(get("/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Project 1"));
    }

    @Test
    void findAllTasksGroupedByProjectWithDeadlines_ShouldReturnTasksWithDeadlines() throws Exception {
        TaskBaseDTO taskBaseDTO = new TaskBaseDTO(1L, "Description 1", false);
        List<TaskBaseDTO> taskList = Arrays.asList(taskBaseDTO);

        TaskDeadlineResponse taskDeadlineResponse = new TaskDeadlineResponse(new Date(), taskList);

        when(taskService.findAllTasksWithDeadlines(null)).thenReturn(Arrays.asList(taskDeadlineResponse));

        mockMvc.perform(get("/projects/all_with_deadlines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].deadline").exists());
    }

    @Test
    void findAllTasksGroupedByProjectWithDeadlines_ShouldReturnTasksGroupedByProjectWithDeadlines() throws Exception {

        TaskBaseDTO taskBaseDTO = new TaskBaseDTO(1L, "Description 1", false);
        List<TaskBaseDTO> taskList = Arrays.asList(taskBaseDTO);
        TaskWithProjectResponseNoDeadline taskWithProjectResponseNoDeadline = new TaskWithProjectResponseNoDeadline(1L, "Description 1", taskList);
        List<TaskWithProjectResponseNoDeadline> taskWithProjectResponseNoDeadlineList = Arrays.asList(taskWithProjectResponseNoDeadline);

        ProjectDeadlineResponse projectDeadlineResponse = new ProjectDeadlineResponse(new Date(), taskWithProjectResponseNoDeadlineList);
        List<ProjectDeadlineResponse> projectDeadlineResponseList = Arrays.asList(projectDeadlineResponse);

        when(taskService.findAllTasksGroupedByProjectWithDeadlines(null)).thenReturn(projectDeadlineResponseList);

        mockMvc.perform(get("/projects/view_by_deadline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].deadline").exists());
    }


    @Test
    void addProject_ShouldReturnOkWithoutResults_WhenIncludeResultsIsFalse() throws Exception {
        ProjectDTO projectDTO = new ProjectDTO("Project 1");
        ProjectResponse projectResponse = new ProjectResponse(1L, "Project 1");

        when(projectService.addProject(projectDTO)).thenReturn(projectResponse);

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Project 1\", \"description\": \"Description\"}")
                        .param("includeResults", "false"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }







}
