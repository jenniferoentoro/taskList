package com.ortecfinance.tasklist.services.impl;

import com.ortecfinance.tasklist.DTO.Project.ProjectDeadlineResponse;
import com.ortecfinance.tasklist.DTO.Task.*;
import com.ortecfinance.tasklist.helper.execptions.CustomException;
import com.ortecfinance.tasklist.models.entities.Project;
import com.ortecfinance.tasklist.models.entities.Task;
import com.ortecfinance.tasklist.models.repositories.TaskRepository;
import com.ortecfinance.tasklist.services.TaskService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;


    private final ModelMapper modelMapper;

    TaskServiceImpl(TaskRepository taskRepository, ModelMapper modelMapper) {
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void addTask(TaskRequest taskRequest, long projectId) {
        Task task = modelMapper.map(taskRequest, Task.class);
        Project project = new Project();
        project.setId(projectId);
        task.setProject(project);
        if (taskRequest.getDeadline() != null) {
            task.setDeadline(parseDate(taskRequest.getDeadline()));
        }
        taskRepository.save(task);
    }


    @Override
    public Boolean updateStateTask(long taskId, Boolean done) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent()) {
            Task taskFind = task.get();
            taskFind.setDone(done);
            taskRepository.save(taskFind);
            return true;
        }
        return false;
    }

    public Date parseDate(String date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            formatter.setLenient(false);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            if (date.equals("today")) {
                return formatter.parse(formatter.format(new Date()));
            }
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new CustomException("Invalid date format");
        }
    }


    @Override
    public Boolean setDeadline(long taskId, String deadline) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent()) {
            Task taskFind = task.get();
            taskFind.setDeadline(parseDate(deadline));
            taskRepository.save(taskFind);
            return true;
        }
        return false;
    }


    @Override
    public TaskResponse findTaskById(long taskId) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent()) {
            return modelMapper.map(task.get(), TaskResponse.class);
        }
        throw new CustomException("Task not found");
    }


    @Override
    public List<TaskResponse> findTasksByProjectId(long projectId) {
        List<Task> taskResponse = taskRepository.findByProjectId(projectId);
        return Arrays.asList(modelMapper.map(taskResponse, TaskResponse[].class));
    }


    @Override
    public List<ProjectDeadlineResponse> findAllTasksGroupedByProjectWithDeadlines(String findDeadline) {
        List<Date> deadlines = getDeadlines(findDeadline);

        List<ProjectDeadlineResponse> projectDeadlineResponses = new ArrayList<>();

        for (Date deadline : deadlines) {
            List<Task> tasks = taskRepository.findByDeadline(deadline);
            Map<Long, List<Task>> tasksGroupedByProject = groupTasksByProject(tasks);

            List<TaskWithProjectResponseNoDeadline> projectList = tasksGroupedByProject.entrySet().stream()
                    .map(entry -> createTaskWithProjectResponse(entry))
                    .collect(Collectors.toList());

            projectDeadlineResponses.add(new ProjectDeadlineResponse(deadline, projectList));
        }

        return projectDeadlineResponses;
    }

    @Override
    public List<TaskDeadlineResponse> findAllTasksWithDeadlines(String findDeadline) {
        List<Date> deadlines = getDeadlines(findDeadline);
        List<TaskDeadlineResponse> taskDeadlineResponses = new ArrayList<>();

        for (Date deadline : deadlines) {
            List<Task> tasks = taskRepository.findByDeadline(deadline);
            List<TaskBaseDTO> taskResponses = tasks.stream()
                    .map(task -> modelMapper.map(task, TaskBaseDTO.class))
                    .collect(Collectors.toList());

            taskDeadlineResponses.add(new TaskDeadlineResponse(deadline, taskResponses));
        }

        return taskDeadlineResponses;
    }

    private List<Date> getDeadlines(String findDeadline) {
        if (findDeadline != null) {
            return Collections.singletonList(parseDate(findDeadline));
        } else {
            return taskRepository.findDistinctDeadline();
        }
    }

    private Map<Long, List<Task>> groupTasksByProject(List<Task> tasks) {
        return tasks.stream()
                .collect(Collectors.groupingBy(task -> task.getProject().getId()));
    }

    private TaskWithProjectResponseNoDeadline createTaskWithProjectResponse(Map.Entry<Long, List<Task>> entry) {
        List<Task> projectTasks = entry.getValue();
        Project project = projectTasks.get(0).getProject();

        List<TaskBaseDTO> taskResponses = projectTasks.stream()
                .map(task -> modelMapper.map(task, TaskBaseDTO.class))
                .collect(Collectors.toList());

        return new TaskWithProjectResponseNoDeadline(project.getId(), project.getName(), taskResponses);
    }


}
