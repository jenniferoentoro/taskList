package com.ortecfinance.tasklist.services;

import com.ortecfinance.tasklist.DTO.Project.ProjectDeadlineResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskDeadlineResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskRequest;
import com.ortecfinance.tasklist.DTO.Task.TaskResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskWithProjectResponse;

import java.util.List;

public interface TaskService {
    void addTask(TaskRequest task, long projectId);

    Boolean updateStateTask(long taskId, Boolean done);

    Boolean setDeadline(long taskId, String deadline);

    List<TaskResponse>  findTasksByProjectId(long projectId);

    List<ProjectDeadlineResponse> findAllTasksGroupedByProjectWithDeadlines(String findDeadline);

    List<TaskDeadlineResponse> findAllTasksWithDeadlines(String findDeadline);


}
