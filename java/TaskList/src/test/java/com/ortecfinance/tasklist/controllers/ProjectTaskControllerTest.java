package com.ortecfinance.tasklist.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ortecfinance.tasklist.DTO.Project.ProjectResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskRequest;
import com.ortecfinance.tasklist.DTO.Task.TaskResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskWithProjectResponse;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ProjectTaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;


    @MockBean
    private ProjectService projectService;

    @Test
    void addTask_ShouldReturnTaskResponse_WhenProjectExists() throws Exception {
        ProjectResponse project = new ProjectResponse(1L, "Project 1");


        TaskRequest taskRequest = new TaskRequest("Task 1", false, null);
        TaskResponse taskResponse = new TaskResponse(1L, "Task 1", false, null, project);
        TaskWithProjectResponse taskWithProjectResponse = new TaskWithProjectResponse(1L, "Project 1", Arrays.asList(taskResponse));

        when(projectService.findProjectById(1L)).thenReturn(taskWithProjectResponse);
        when(taskService.addTask(any(), eq(1L))).thenReturn(taskResponse);

        mockMvc.perform(post("/projects/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(taskRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void addTask_ShouldReturnBadRequest_WhenProjectNotFound() throws Exception {
        TaskRequest taskRequest = new TaskRequest("Task 1", false, null);

        when(projectService.findProjectById(1L)).thenReturn(null);

        mockMvc.perform(post("/projects/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(taskRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTask_ShouldReturnTaskResponse_WhenProjectExists() throws Exception {
        TaskRequest taskRequest = new TaskRequest("Updated Task", false, null);
        TaskResponse taskResponse = new TaskResponse(1L, "Updated Task", false, null, null);
        TaskWithProjectResponse taskWithProjectResponse = new TaskWithProjectResponse(1L, "Project 1", Arrays.asList(taskResponse));

        when(projectService.findProjectById(1L)).thenReturn(taskWithProjectResponse);
        when(taskService.setDeadline(eq(1L), eq(1L), any())).thenReturn(taskResponse);

        mockMvc.perform(put("/projects/1/tasks/1")
                        .param("deadline", "12-31-2024")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(taskRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void updateTask_ShouldReturnBadRequest_WhenProjectNotFound() throws Exception {
        TaskRequest taskRequest = new TaskRequest("Updated Task", false, null);

        when(projectService.findProjectById(1L)).thenReturn(null);

        mockMvc.perform(put("/projects/1/tasks/1")
                        .param("deadline", "2024-12-31")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(taskRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findTasksByProjectId_ShouldReturnTaskList_WhenProjectExists() throws Exception {
        ProjectResponse project = new ProjectResponse(1L, "Project 1");
        TaskResponse taskResponse = new TaskResponse(1L, "Task 1", false, null, project);
        TaskWithProjectResponse taskWithProjectResponse = new TaskWithProjectResponse(1L, "Project 1", List.of(taskResponse));

        when(projectService.findProjectById(1L)).thenReturn(taskWithProjectResponse);
        when(taskService.findTasksByProjectId(eq(1L))).thenReturn(List.of(taskResponse));

        mockMvc.perform(get("/projects/1/tasks"))
                .andExpect(status().isOk());
    }

    @Test
    void findTasksByProjectId_ShouldReturnBadRequest_WhenProjectNotFound() throws Exception {
        when(projectService.findProjectById(1L)).thenReturn(null);

        mockMvc.perform(get("/projects/1/tasks"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findTaskByProjectIdAndId_ShouldReturnTask_WhenTaskExists() throws Exception {
        TaskResponse taskResponse = new TaskResponse(1L, "Task 1", false, null, null);

        when(taskService.findTaskByProjectIdAndId(eq(1L), eq(1L))).thenReturn(taskResponse);

        mockMvc.perform(get("/projects/1/tasks/1"))
                .andExpect(status().isOk());
    }

    @Test
    void removeTask_ShouldReturnOk_WhenTaskRemoved() throws Exception {
        TaskResponse taskResponse = new TaskResponse(1L, "Task 1", false, null, null);

        when(taskService.removeTask(eq(1L), eq(1L))).thenReturn(taskResponse);

        mockMvc.perform(delete("/projects/1/tasks/1"))
                .andExpect(status().isOk());
    }




}