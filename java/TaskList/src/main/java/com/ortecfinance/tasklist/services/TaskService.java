package com.ortecfinance.tasklist.services;

import com.ortecfinance.tasklist.DTO.Project.ProjectDeadlineResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskDeadlineResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskRequest;
import com.ortecfinance.tasklist.DTO.Task.TaskResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskWithProjectResponse;

import java.util.List;

public interface TaskService {
    TaskResponse addTask(TaskRequest task, long projectId);

    TaskResponse removeTask(long projectId, long taskId);

    TaskResponse updateStateTask(long projectId, long taskId, Boolean done);

    TaskResponse setDeadline(long projectId, long taskId, String deadline);

    TaskResponse findTaskByProjectIdAndId(long projectId, long taskId);

    List<TaskResponse> findTasksByProjectId(long projectId);

    List<ProjectDeadlineResponse> findAllTasksGroupedByProjectWithDeadlines(String findDeadline);

    List<TaskDeadlineResponse> findAllTasksWithDeadlines(String findDeadline);


}
