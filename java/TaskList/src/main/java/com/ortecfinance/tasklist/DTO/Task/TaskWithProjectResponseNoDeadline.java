package com.ortecfinance.tasklist.DTO.Task;

import java.util.List;

public class TaskWithProjectResponseNoDeadline extends TaskWithProjectResponseBase{

    List<TaskBaseDTO> tasks;

    public TaskWithProjectResponseNoDeadline() {
    }

    public TaskWithProjectResponseNoDeadline(Long id, String name, List<TaskBaseDTO> tasks) {
        super(id, name);
        this.tasks = tasks;
    }

    public List<TaskBaseDTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskBaseDTO> tasks) {
        this.tasks = tasks;
    }
}
