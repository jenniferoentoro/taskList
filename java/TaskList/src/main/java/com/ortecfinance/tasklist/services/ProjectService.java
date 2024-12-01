package com.ortecfinance.tasklist.services;

import com.ortecfinance.tasklist.DTO.Project.ProjectDTO;
import com.ortecfinance.tasklist.DTO.Project.ProjectResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskWithProjectResponse;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    ProjectResponse addProject(ProjectDTO project);

    ProjectResponse removeProject(long projectId);

    ProjectResponse findProjectById(long projectId);

    List<TaskWithProjectResponse> findAllTasksGroupedByProject();

}
