package com.ortecfinance.tasklist.DTO.Task;

import java.util.List;

public class TaskWithProjectResponse extends TaskWithProjectResponseBase {
    List<TaskResponse> tasks;

    public TaskWithProjectResponse() {
    }

    public TaskWithProjectResponse(Long id, String name, List<TaskResponse> tasks) {
        super(id, name);
        this.tasks = tasks;
    }

    public List<TaskResponse> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskResponse> tasks) {
        this.tasks = tasks;
    }
}
