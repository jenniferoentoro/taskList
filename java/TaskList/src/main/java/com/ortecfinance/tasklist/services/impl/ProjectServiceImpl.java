package com.ortecfinance.tasklist.services.impl;

import com.ortecfinance.tasklist.DTO.Project.ProjectDTO;
import com.ortecfinance.tasklist.DTO.Project.ProjectResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskWithProjectResponse;
import com.ortecfinance.tasklist.helper.execptions.CustomException;
import com.ortecfinance.tasklist.models.entities.Project;
import com.ortecfinance.tasklist.models.repositories.ProjectRepository;
import com.ortecfinance.tasklist.services.ProjectService;
import com.ortecfinance.tasklist.services.TaskService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public ProjectResponse addProject(ProjectDTO project) {
        Project projectEntity = projectRepository.save(modelMapper.map(project, Project.class));
        return modelMapper.map(projectEntity, ProjectResponse.class);
    }

    @Override
    public ProjectResponse removeProject(long projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent()) {
            projectRepository.delete(project.get());
            return modelMapper.map(project.get(), ProjectResponse.class);
        }
        throw new CustomException("Project not found");
    }


    @Override
    public TaskWithProjectResponse findProjectById(long projectId) {
        Optional<Project> project = projectRepository.findById(projectId);

        if(project.isPresent()) {
            List<TaskResponse> taskResponses = taskService.findTasksByProjectId(projectId);
            return new TaskWithProjectResponse(projectId, project.get().getName(), taskResponses);
        }

        throw new CustomException("Project not found");
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