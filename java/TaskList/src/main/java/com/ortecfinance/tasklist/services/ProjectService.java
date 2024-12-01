package com.ortecfinance.tasklist.services;

import com.ortecfinance.tasklist.DTO.Project.ProjectDTO;
import com.ortecfinance.tasklist.DTO.Project.ProjectResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskWithProjectResponse;

import java.util.List;

public interface ProjectService {
    Boolean addProject(ProjectDTO project);

    ProjectResponse findProjectByName(String projectName);

    List<TaskWithProjectResponse> findAllTasksGroupedByProject();

}
