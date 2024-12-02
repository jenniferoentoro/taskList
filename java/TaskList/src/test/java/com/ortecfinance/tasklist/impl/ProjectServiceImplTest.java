package com.ortecfinance.tasklist.impl;

import com.ortecfinance.tasklist.DTO.Project.ProjectDTO;
import com.ortecfinance.tasklist.DTO.Project.ProjectResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskResponse;
import com.ortecfinance.tasklist.DTO.Task.TaskWithProjectResponse;
import com.ortecfinance.tasklist.helper.execptions.CustomException;
import com.ortecfinance.tasklist.models.entities.Project;
import com.ortecfinance.tasklist.models.repositories.ProjectRepository;
import com.ortecfinance.tasklist.services.TaskService;
import com.ortecfinance.tasklist.services.impl.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {
    @InjectMocks
    private ProjectServiceImpl projectService;

    @Mock
    private TaskService taskService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ModelMapper modelMapper;
    private ProjectDTO projectDTO;
    private Project projectEntity;
    private ProjectResponse projectResponse;

    @BeforeEach
    void setUp() {
        projectDTO = new ProjectDTO("Test Project");
        projectEntity = new Project();
        projectEntity.setId(1L);
        projectEntity.setName("Test Project");

        projectResponse = new ProjectResponse(1L, "Test Project");
    }

    @Test
    void testAddProject() {
        when(modelMapper.map(projectDTO, Project.class)).thenReturn(projectEntity);
        when(projectRepository.save(projectEntity)).thenReturn(projectEntity);
        when(modelMapper.map(projectEntity, ProjectResponse.class)).thenReturn(projectResponse);

        ProjectResponse result = projectService.addProject(projectDTO);

        assertNotNull(result);
        assertEquals(projectResponse.getId(), result.getId());
        assertEquals(projectResponse.getName(), result.getName());
    }


    @Test
    void testRemoveProject_ProjectExists() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(projectEntity));
        when(modelMapper.map(projectEntity, ProjectResponse.class)).thenReturn(projectResponse);

        ProjectResponse result = projectService.removeProject(1L);

        assertNotNull(result);
        assertEquals(projectResponse.getId(), result.getId());
        assertEquals(projectResponse.getName(), result.getName());
        verify(projectRepository, times(1)).delete(projectEntity);
    }

    @Test
    void testRemoveProject_ProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> projectService.removeProject(1L));
    }

    @Test
    void testFindProjectById_ProjectExists() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(projectEntity));
        when(taskService.findTasksByProjectId(1L)).thenReturn(List.of());

        TaskWithProjectResponse result = projectService.findProjectById(1L);

        assertNotNull(result);
        assertEquals(projectEntity.getId(), result.getId());
        assertEquals(projectEntity.getName(), result.getName());
        assertTrue(result.getTasks().isEmpty());
    }

    @Test
    void testFindProjectById_ProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> projectService.findProjectById(1L));

    }


    @Test
    void testFindAllTasksGroupedByProject() {
        long projectId1 = 1L;
        long projectId2 = 2L;
        String projectName1 = "Project 1";
        String projectName2 = "Project 2";

        Project project1 = new Project();
        project1.setId(projectId1);
        project1.setName(projectName1);

        Project project2 = new Project();
        project2.setId(projectId2);
        project2.setName(projectName2);

        TaskResponse taskResponse1 = new TaskResponse();
        TaskResponse taskResponse2 = new TaskResponse();

        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));
        when(taskService.findTasksByProjectId(projectId1)).thenReturn(List.of(taskResponse1));
        when(taskService.findTasksByProjectId(projectId2)).thenReturn(List.of(taskResponse2));

        List<TaskWithProjectResponse> result = projectService.findAllTasksGroupedByProject();

        assertEquals(2, result.size());
        assertEquals(projectName1, result.get(0).getName());
        assertEquals(1, result.get(0).getTasks().size());
        assertEquals(projectName2, result.get(1).getName());
        assertEquals(1, result.get(1).getTasks().size());
    }

    @Test
    void testFindAllTasksGroupedByProject_emptyProjects() {
        when(projectRepository.findAll()).thenReturn(List.of());

        List<TaskWithProjectResponse> result = projectService.findAllTasksGroupedByProject();

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllTasksGroupedByProject_noTasksForSomeProjects() {
        long projectId1 = 1L, projectId2 = 2L;
        Project project1 = new Project();
        project1.setId(projectId1);
        project1.setName("Project 1");

        Project project2 = new Project();
        project2.setId(projectId2);
        project2.setName("Project 2");

        TaskResponse taskResponse = new TaskResponse();

        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));
        when(taskService.findTasksByProjectId(projectId1)).thenReturn(List.of(taskResponse));
        when(taskService.findTasksByProjectId(projectId2)).thenReturn(List.of());

        List<TaskWithProjectResponse> result = projectService.findAllTasksGroupedByProject();

        assertEquals(2, result.size());
        assertTrue(result.get(1).getTasks().isEmpty());
    }

    @Test
    void testFindAllTasksGroupedByProject_taskServiceInteraction() {
        long projectId = 1L;
        Project project = new Project();
        project.setId(projectId);
        project.setName("Project 1");

        TaskResponse taskResponse1 = new TaskResponse();
        TaskResponse taskResponse2 = new TaskResponse();

        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(taskService.findTasksByProjectId(projectId)).thenReturn(List.of(taskResponse1, taskResponse2));

        List<TaskWithProjectResponse> result = projectService.findAllTasksGroupedByProject();

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getTasks().size());
    }


}