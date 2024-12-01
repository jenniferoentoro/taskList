package com.ortecfinance.tasklist.impl;

import com.ortecfinance.tasklist.DTO.Project.ProjectDeadlineResponse;
import com.ortecfinance.tasklist.DTO.Task.*;
import com.ortecfinance.tasklist.models.entities.Project;
import com.ortecfinance.tasklist.models.entities.Task;
import com.ortecfinance.tasklist.models.repositories.TaskRepository;
import com.ortecfinance.tasklist.services.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void testAddTask_whenDeadlineIsNotNull() {
        long projectId = 1L;
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setDone(false);
        taskRequest.setDeadline("01-12-2024");

        Task taskEntity = new Task();
        taskEntity.setProject(new Project());
        taskEntity.setDone(false);
        taskEntity.setDeadline(new Date());

        when(modelMapper.map(taskRequest, Task.class)).thenReturn(taskEntity);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);

        taskService.addTask(taskRequest, projectId);

        verify(taskRepository, times(1)).save(taskEntity);
        verify(modelMapper, times(1)).map(taskRequest, Task.class);
        assertNotNull(taskEntity.getDeadline());
    }

    @Test
    void testAddTask_whenDeadlineIsNull() {
        long projectId = 1L;
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setDone(false);
        taskRequest.setDeadline(null);

        Task taskEntity = new Task();
        taskEntity.setProject(new Project());
        taskEntity.setDone(false);
        taskEntity.setDeadline(null);

        when(modelMapper.map(taskRequest, Task.class)).thenReturn(taskEntity);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);

        taskService.addTask(taskRequest, projectId);

        verify(taskRepository, times(1)).save(taskEntity);
        verify(modelMapper, times(1)).map(taskRequest, Task.class);
        assertNull(taskEntity.getDeadline());
    }


    @Test
    void testUpdateStateTask_taskExists() {
        long taskId = 1L;
        Boolean done = true;
        Task task = new Task();
        task.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        Boolean result = taskService.updateStateTask(taskId, done);

        assertTrue(result);
        assertEquals(done, task.getDone());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void testUpdateStateTask_taskExists_done_false() {
        long taskId = 1L;
        Boolean done = false;
        Task task = new Task();
        task.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        Boolean result = taskService.updateStateTask(taskId, done);

        assertTrue(result);
        assertEquals(done, task.getDone());
        verify(taskRepository, times(1)).save(task);
    }


    @Test
    void testUpdateStateTask_taskNotFound() {
        long taskId = 1L;
        Boolean done = true;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Boolean result = taskService.updateStateTask(taskId, done);

        assertFalse(result);
        verify(taskRepository, times(0)).save(any());
    }

    @Test
    void testSetDeadline_taskExists() {
        long taskId = 1L;
        String deadline = "15-12-2024";
        Task task = new Task();
        task.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        Boolean result = taskService.setDeadline(taskId, deadline);

        assertTrue(result);
        assertNotNull(task.getDeadline());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void testSetDeadline_taskNotFound() {
        long taskId = 1L;
        String deadline = "15-12-2024";

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Boolean result = taskService.setDeadline(taskId, deadline);

        assertFalse(result);
        verify(taskRepository, times(0)).save(any());
    }

    @Test
    void testFindTasksByProjectId() {
        long projectId = 1L;
        List<Task> tasks = new ArrayList<>();
        Task task = new Task();
        task.setProject(new Project());
        task.getProject().setId(projectId);
        tasks.add(task);

        TaskResponse taskResponse = new TaskResponse();
        when(taskRepository.findByProjectId(projectId)).thenReturn(tasks);
        when(modelMapper.map(tasks, TaskResponse[].class)).thenReturn(new TaskResponse[]{taskResponse});

        List<TaskResponse> result = taskService.findTasksByProjectId(projectId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findByProjectId(projectId);
        verify(modelMapper, times(1)).map(tasks, TaskResponse[].class);
    }


    @Test
    void testFindAllTasksGroupedByProjectWithDeadlines_whenFindDeadlineIsNull() {
        String findDeadline = null;

        Date deadline1 = taskService.parseDate("01-12-2024");
        Date deadline2 = taskService.parseDate("02-12-2024");
        List<Date> deadlines = Arrays.asList(deadline1, deadline2);

        Task task1 = new Task();
        task1.setDeadline(deadline1);
        Project project1 = new Project();
        project1.setId(1L);
        task1.setProject(project1);

        Task task2 = new Task();
        task2.setDeadline(deadline2);
        Project project2 = new Project();
        project2.setId(2L);
        task2.setProject(project2);

        List<Task> tasks1 = Arrays.asList(task1);
        List<Task> tasks2 = Arrays.asList(task2);

        when(taskRepository.findDistinctDeadline()).thenReturn(deadlines);
        when(taskRepository.findByDeadline(deadline1)).thenReturn(tasks1);
        when(taskRepository.findByDeadline(deadline2)).thenReturn(tasks2);

        List<ProjectDeadlineResponse> result = taskService.findAllTasksGroupedByProjectWithDeadlines(findDeadline);

        assertEquals(2, result.size());
        assertEquals(deadline1, result.get(0).getDeadline());
        assertEquals(deadline2, result.get(1).getDeadline());

        assertFalse(result.get(0).getProject().isEmpty());
        assertFalse(result.get(1).getProject().isEmpty());
    }

    @Test
    void testFindAllTasksGroupedByProjectWithDeadlines_whenFindDeadlineIsNotNull() {
        String findDeadline = "01-12-2024";

        Date deadline = taskService.parseDate(findDeadline);

        Task task1 = new Task();
        task1.setDeadline(deadline);
        Project project1 = new Project();
        project1.setId(1L);
        task1.setProject(project1);

        List<Task> tasks = Arrays.asList(task1);

        when(taskRepository.findByDeadline(deadline)).thenReturn(tasks);

        List<ProjectDeadlineResponse> result = taskService.findAllTasksGroupedByProjectWithDeadlines(findDeadline);

        assertEquals(1, result.size());
        assertEquals(deadline, result.get(0).getDeadline());
        assertFalse(result.get(0).getProject().isEmpty());
    }


    @Test
    void testFindAllTasksGroupedByProjectWithDeadlines_whenNoTasksForDeadline() {
        String findDeadline = "03-12-2024";

        Date deadline = taskService.parseDate(findDeadline);

        when(taskRepository.findByDeadline(deadline)).thenReturn(Collections.emptyList());

        List<ProjectDeadlineResponse> result = taskService.findAllTasksGroupedByProjectWithDeadlines(findDeadline);

        assertEquals(1, result.size());
        assertEquals(deadline, result.get(0).getDeadline());
        assertTrue(result.get(0).getProject().isEmpty());
    }

    @Test
    void testFindAllTasksGroupedByProjectWithDeadlines_whenNoDistinctDeadlines() {
        String findDeadline = null;

        when(taskRepository.findDistinctDeadline()).thenReturn(Collections.emptyList());

        List<ProjectDeadlineResponse> result = taskService.findAllTasksGroupedByProjectWithDeadlines(findDeadline);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllTasksGroupedByProjectWithDeadlines_whenOneDeadline() {
        String findDeadline = null;

        Date deadline = taskService.parseDate("01-12-2024");

        Task task1 = new Task();
        task1.setDeadline(deadline);
        Project project1 = new Project();
        project1.setId(1L);
        task1.setProject(project1);

        List<Task> tasks = Arrays.asList(task1);

        when(taskRepository.findDistinctDeadline()).thenReturn(Collections.singletonList(deadline));
        when(taskRepository.findByDeadline(deadline)).thenReturn(tasks);

        List<ProjectDeadlineResponse> result = taskService.findAllTasksGroupedByProjectWithDeadlines(findDeadline);

        assertEquals(1, result.size());
        assertEquals(deadline, result.get(0).getDeadline());
        assertFalse(result.get(0).getProject().isEmpty());
    }

    @Test
    void testFindAllTasksGroupedByProjectWithDeadlines_whenMultipleTasksForProject() {
        String findDeadline = null;

        Date deadline = taskService.parseDate("01-12-2024");

        Task task1 = new Task();
        task1.setDeadline(deadline);
        Project project = new Project();
        project.setId(1L);
        task1.setProject(project);

        Task task2 = new Task();
        task2.setDeadline(deadline);
        task2.setProject(project);

        List<Task> tasks = Arrays.asList(task1, task2);

        when(taskRepository.findDistinctDeadline()).thenReturn(Collections.singletonList(deadline));
        when(taskRepository.findByDeadline(deadline)).thenReturn(tasks);

        List<ProjectDeadlineResponse> result = taskService.findAllTasksGroupedByProjectWithDeadlines(findDeadline);

        assertEquals(1, result.size());
        assertEquals(deadline, result.get(0).getDeadline());
        assertEquals(1, result.get(0).getProject().size());
    }

    @Test
    void testFindAllTasksGroupedByProjectWithDeadlines_whenTasksBelongToDifferentProjects() {
        String findDeadline = null;

        Date deadline = taskService.parseDate("01-12-2024");

        Task task1 = new Task();
        task1.setDeadline(deadline);
        Project project1 = new Project();
        project1.setId(1L);
        task1.setProject(project1);

        Task task2 = new Task();
        task2.setDeadline(deadline);
        Project project2 = new Project();
        project2.setId(2L);
        task2.setProject(project2);

        List<Task> tasks = Arrays.asList(task1, task2);

        when(taskRepository.findDistinctDeadline()).thenReturn(Collections.singletonList(deadline));
        when(taskRepository.findByDeadline(deadline)).thenReturn(tasks);

        List<ProjectDeadlineResponse> result = taskService.findAllTasksGroupedByProjectWithDeadlines(findDeadline);

        assertEquals(1, result.size());
        assertEquals(deadline, result.get(0).getDeadline());
        assertEquals(2, result.get(0).getProject().size());
    }

    @Test
    void testFindAllTasksWithDeadlines_whenFindDeadlineIsNull() {
        String findDeadline = null;

        Date deadline1 = taskService.parseDate("01-12-2024");
        Date deadline2 = taskService.parseDate("02-12-2024");
        List<Date> deadlines = Arrays.asList(deadline1, deadline2);

        Task task1 = new Task();
        task1.setDeadline(deadline1);
        Project project1 = new Project();
        project1.setId(1L);
        task1.setProject(project1);

        Task task2 = new Task();
        task2.setDeadline(deadline2);
        Project project2 = new Project();
        project2.setId(2L);
        task2.setProject(project2);

        List<Task> tasks1 = Arrays.asList(task1);
        List<Task> tasks2 = Arrays.asList(task2);

        when(taskRepository.findDistinctDeadline()).thenReturn(deadlines);
        when(taskRepository.findByDeadline(deadline1)).thenReturn(tasks1);
        when(taskRepository.findByDeadline(deadline2)).thenReturn(tasks2);

        List<TaskDeadlineResponse> result = taskService.findAllTasksWithDeadlines(findDeadline);

        assertEquals(2, result.size());
        assertEquals(deadline1, result.get(0).getDeadline());
        assertEquals(deadline2, result.get(1).getDeadline());

        assertFalse(result.get(0).getTask().isEmpty());
        assertFalse(result.get(1).getTask().isEmpty());
    }

    @Test
    void testFindAllTasksWithDeadlines_whenFindDeadlineIsNotNull() {
        String findDeadline = "01-12-2024";

        Date deadline = taskService.parseDate(findDeadline);

        Task task1 = new Task();
        task1.setDeadline(deadline);
        Project project1 = new Project();
        project1.setId(1L);
        task1.setProject(project1);

        List<Task> tasks = Arrays.asList(task1);

        when(taskRepository.findByDeadline(deadline)).thenReturn(tasks);

        List<TaskDeadlineResponse> result = taskService.findAllTasksWithDeadlines(findDeadline);

        assertEquals(1, result.size());
        assertEquals(deadline, result.get(0).getDeadline());
        assertFalse(result.get(0).getTask().isEmpty());
    }

    @Test
    void testFindAllTasksWithDeadlines_whenNoTasksForDeadline() {
        String findDeadline = "03-12-2024";

        Date deadline = taskService.parseDate(findDeadline);

        when(taskRepository.findByDeadline(deadline)).thenReturn(Collections.emptyList());

        List<TaskDeadlineResponse> result = taskService.findAllTasksWithDeadlines(findDeadline);

        assertEquals(1, result.size());
        assertEquals(deadline, result.get(0).getDeadline());
        assertTrue(result.get(0).getTask().isEmpty());
    }

    @Test
    void testFindAllTasksWithDeadlines_whenNoDistinctDeadlines() {
        String findDeadline = null;

        when(taskRepository.findDistinctDeadline()).thenReturn(Collections.emptyList());

        List<TaskDeadlineResponse> result = taskService.findAllTasksWithDeadlines(findDeadline);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllTasksWithDeadlines_whenOneDeadline() {
        String findDeadline = null;

        Date deadline = taskService.parseDate("01-12-2024");

        Task task1 = new Task();
        task1.setDeadline(deadline);
        Project project1 = new Project();
        project1.setId(1L);
        task1.setProject(project1);

        List<Task> tasks = Arrays.asList(task1);

        when(taskRepository.findDistinctDeadline()).thenReturn(Collections.singletonList(deadline));
        when(taskRepository.findByDeadline(deadline)).thenReturn(tasks);

        List<TaskDeadlineResponse> result = taskService.findAllTasksWithDeadlines(findDeadline);

        assertEquals(1, result.size());
        assertEquals(deadline, result.get(0).getDeadline());
        assertFalse(result.get(0).getTask().isEmpty());
    }

    @Test
    void testFindAllTasksWithDeadlines_whenMultipleTasksForDeadline() {
        String findDeadline = null;

        Date deadline = taskService.parseDate("01-12-2024");

        Task task1 = new Task();
        task1.setDeadline(deadline);
        Project project1 = new Project();
        project1.setId(1L);
        task1.setProject(project1);

        Task task2 = new Task();
        task2.setDeadline(deadline);
        task2.setProject(project1);

        List<Task> tasks = Arrays.asList(task1, task2);

        when(taskRepository.findDistinctDeadline()).thenReturn(Collections.singletonList(deadline));
        when(taskRepository.findByDeadline(deadline)).thenReturn(tasks);

        List<TaskDeadlineResponse> result = taskService.findAllTasksWithDeadlines(findDeadline);

        assertEquals(1, result.size());
        assertEquals(deadline, result.get(0).getDeadline());
        assertEquals(2, result.get(0).getTask().size());
    }

    @Test
    void testFindAllTasksWithDeadlines_whenTasksBelongToDifferentProjects() {
        String findDeadline = null;

        Date deadline = taskService.parseDate("01-12-2024");

        Task task1 = new Task();
        task1.setDeadline(deadline);
        Project project1 = new Project();
        project1.setId(1L);
        task1.setProject(project1);

        Task task2 = new Task();
        task2.setDeadline(deadline);
        Project project2 = new Project();
        project2.setId(2L);
        task2.setProject(project2);

        List<Task> tasks = Arrays.asList(task1, task2);

        when(taskRepository.findDistinctDeadline()).thenReturn(Collections.singletonList(deadline));
        when(taskRepository.findByDeadline(deadline)).thenReturn(tasks);

        List<TaskDeadlineResponse> result = taskService.findAllTasksWithDeadlines(findDeadline);

        assertEquals(1, result.size());
        assertEquals(deadline, result.get(0).getDeadline());
        assertEquals(2, result.get(0).getTask().size());
    }


}