package com.ortecfinance.tasklist.DTO.Task;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class TaskDeadlineResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date deadline;
    private List<TaskBaseDTO> task;
    public TaskDeadlineResponse() {
    }
    public TaskDeadlineResponse(Date deadline, List<TaskBaseDTO> task) {
        this.deadline = deadline;
        this.task = task;
    }



    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public List<TaskBaseDTO> getTask() {
        return task;
    }

    public void setTask(List<TaskBaseDTO> task) {
        this.task = task;
    }
}
