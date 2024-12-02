package com.ortecfinance.tasklist.services;

import com.ortecfinance.tasklist.DTO.Project.ProjectDTO;
import com.ortecfinance.tasklist.DTO.Project.ProjectResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskWithProjectResponse;

import java.util.List;

public interface ProjectService {
    ProjectResponse addProject(ProjectDTO project);

    ProjectResponse removeProject(long projectId);

    TaskWithProjectResponse findProjectById(long projectId);

    List<TaskWithProjectResponse> findAllTasksGroupedByProject();

}
