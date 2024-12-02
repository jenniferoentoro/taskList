package com.ortecfinance.tasklist.impl;

import com.ortecfinance.tasklist.DTO.Project.ProjectDeadlineResponse;
import com.ortecfinance.tasklist.DTO.Project.ProjectResponse;
import com.ortecfinance.tasklist.DTO.Task.*;
import com.ortecfinance.tasklist.helper.execptions.CustomException;
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

import java.text.SimpleDateFormat;
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
    public void testAddTask() {
        long projectId = 1L;
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setDone(false);
        taskRequest.setDeadline("01-12-2024");
        Task taskEntity = new Task();
        taskEntity.setProject(new Project());
        taskEntity.setDone(false);
        taskEntity.setDeadline(new Date());
        TaskResponse taskResponse = new TaskResponse();

        when(modelMapper.map(taskRequest, Task.class)).thenReturn(taskEntity);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);
        when(modelMapper.map(taskEntity, TaskResponse.class)).thenReturn(taskResponse);

        TaskResponse result = taskService.addTask(taskRequest, projectId);

        assertEquals(taskResponse, result);
        verify(taskRepository, times(1)).save(taskEntity);
    }



    @Test
    void addTask_shouldSetDoneToFalseWhenDoneIsNull() {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setDone(null);
        taskRequest.setDeadline("10-12-2024");

        Task task = new Task();
        task.setDone(false);

        Project project = new Project();
        project.setId(1L);
        task.setProject(project);

        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setDone(false);

        when(modelMapper.map(taskRequest, Task.class)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(modelMapper.map(task, TaskResponse.class)).thenReturn(taskResponse);

        TaskResponse result = taskService.addTask(taskRequest, 1L);

        assertNotNull(result);
        assertFalse(taskRequest.isDone());

        verify(taskRepository, times(1)).save(task);
        verify(modelMapper, times(1)).map(taskRequest, Task.class);
        verify(modelMapper, times(1)).map(task, TaskResponse.class);
    }


    @Test
    public void testRemoveTask_success() {
        long projectId = 1L;
        long taskId = 1L;
        Task task = new Task();
        TaskResponse taskResponse = new TaskResponse();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(modelMapper.map(task, TaskResponse.class)).thenReturn(taskResponse);

        TaskResponse result = taskService.removeTask(projectId, taskId);

        assertEquals(taskResponse, result);
        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    public void testRemoveTask_taskNotFound() {
        long projectId = 1L;
        long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> taskService.removeTask(projectId, taskId));
    }

    @Test
    void testFindTaskByProjectIdAndId_success() {
        long projectId = 1L;
        long taskId = 1L;

        Task task = new Task();
        task.setId(taskId);
        task.setProject(new Project());
        task.getProject().setId(projectId);

        TaskResponse taskResponse = new TaskResponse();
        when(taskRepository.findByProjectIdAndId(projectId, taskId)).thenReturn(Optional.of(task));
        when(modelMapper.map(task, TaskResponse.class)).thenReturn(taskResponse);

        TaskResponse result = taskService.findTaskByProjectIdAndId(projectId, taskId);

        assertEquals(taskResponse, result);
        verify(taskRepository, times(1)).findByProjectIdAndId(projectId, taskId);
        verify(modelMapper, times(1)).map(task, TaskResponse.class);
    }

    @Test
    void testFindTaskByProjectIdAndId_taskNotFound() {
        long projectId = 1L;
        long taskId = 1L;

        when(taskRepository.findByProjectIdAndId(projectId, taskId)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> taskService.findTaskByProjectIdAndId(projectId, taskId));
    }



    @Test
    public void testUpdateStateTask_success() {
        long projectId = 1L;
        long taskId = 1L;
        Boolean done = true;
        Task task = new Task();
        task.setDone(done);
        TaskResponse taskResponse = new TaskResponse();

        when(taskRepository.findByProjectIdAndId(projectId, taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(modelMapper.map(task, TaskResponse.class)).thenReturn(taskResponse);

        TaskResponse result = taskService.updateStateTask(projectId, taskId, done);

        assertEquals(taskResponse, result);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    public void testUpdateStateTask_taskNotFound() {
        long projectId = 1L;
        long taskId = 1L;
        Boolean done = true;

        when(taskRepository.findByProjectIdAndId(projectId, taskId)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> taskService.updateStateTask(projectId, taskId, done));
    }

    @Test
    public void testSetDeadline_success() {
        long projectId = 1L;
        long taskId = 1L;
        String deadline = "01-12-2024";
        Task task = new Task();
        task.setDeadline(new Date());
        TaskResponse taskResponse = new TaskResponse();

        when(taskRepository.findByProjectIdAndId(projectId, taskId)).thenReturn(Optional.of(task));
        when(modelMapper.map(task, TaskResponse.class)).thenReturn(taskResponse);

        TaskResponse result = taskService.setDeadline(projectId, taskId, deadline);

        assertEquals(taskResponse, result);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    public void testSetDeadline_taskNotFound() {
        long projectId = 1L;
        long taskId = 1L;
        String deadline = "01-12-2024";

        when(taskRepository.findByProjectIdAndId(projectId, taskId)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> taskService.setDeadline(projectId, taskId, deadline));
    }

    @Test
    public void testSetDeadline_invalidDateFormat() {
        long projectId = 1L;
        long taskId = 1L;
        String invalidDeadline = "invalid-date-format";

        assertThrows(CustomException.class, () -> taskService.setDeadline(projectId, taskId, invalidDeadline));
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
    public void testParseDate_validDate() {
        Date date = taskService.parseDate("15-10-2024");
        assertNotNull(date);
        assertEquals("15-10-2024", new SimpleDateFormat("dd-MM-yyyy").format(date));
    }

    @Test
    public void testParseDate_today(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String today = sdf.format(new Date());

        Date todayDate = taskService.parseDate("today");
        assertNotNull(todayDate);
        assertEquals(today, new SimpleDateFormat("dd-MM-yyyy").format(todayDate));
    }

    @Test
    public void testParseDate_invalidDateFormat() {
        assertThrows(CustomException.class, () -> taskService.parseDate("invalid-date-format"));
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