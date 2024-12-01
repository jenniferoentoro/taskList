package com.ortecfinance.tasklist.services;

import com.ortecfinance.tasklist.DTO.Project.ProjectDTO;
import com.ortecfinance.tasklist.DTO.Project.ProjectResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskWithProjectResponse;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    Boolean addProject(ProjectDTO project);

    Optional<ProjectResponse> findProjectByName(String projectName);

    List<TaskWithProjectResponse> findAllTasksGroupedByProject();

}
