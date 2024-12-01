package com.ortecfinance.tasklist.services.impl;

import com.ortecfinance.tasklist.DTO.Project.ProjectDTO;
import com.ortecfinance.tasklist.DTO.Project.ProjectResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskWithProjectResponse;
import com.ortecfinance.tasklist.models.entities.Project;
import com.ortecfinance.tasklist.models.repositories.ProjectRepository;
import com.ortecfinance.tasklist.services.ProjectService;
import com.ortecfinance.tasklist.services.TaskService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    private final TaskService taskService;

    ProjectServiceImpl(ProjectRepository projectRepository, ModelMapper modelMapper, TaskService taskService) {
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
        this.taskService = taskService;
    }

    @Override
    public Boolean addProject(ProjectDTO project) {
        ProjectResponse projectResponse = findProjectByName(project.getName());
        if (projectResponse != null) {
            return false;
        }
        projectRepository.save(modelMapper.map(project, Project.class));

        return true;
    }


    @Override
    public ProjectResponse findProjectByName(String projectName) {
        Optional<Project> project = projectRepository.findByName(projectName);
        return project.map(value -> modelMapper.map(value, ProjectResponse.class)).orElse(null);
    }

    @Override
    public List<TaskWithProjectResponse> findAllTasksGroupedByProject() {
        List<Project> projects = projectRepository.findAll();

        List<TaskWithProjectResponse> taskWithProjectResponses = new ArrayList<>();

        projects.forEach(project -> {
            List<TaskResponse> taskResponses = taskService.findTasksByProjectId(project.getId());

            TaskWithProjectResponse taskWithProjectResponse = new TaskWithProjectResponse(project.getId(), project.getName(), taskResponses);
            taskWithProjectResponses.add(taskWithProjectResponse);
        });

        return taskWithProjectResponses;

    }
}
