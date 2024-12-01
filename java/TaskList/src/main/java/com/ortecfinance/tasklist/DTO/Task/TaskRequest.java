package com.ortecfinance.tasklist.DTO.Task;

import jakarta.validation.constraints.NotBlank;

public class TaskRequest {
    @NotBlank(message = "Description is required")
    private String description;

    private Boolean done;

    private String deadline;

    public TaskRequest() {
    }

    public TaskRequest(String description) {
        this.description = description;
        // default value for done is false
        this.done = false;
    }

    public TaskRequest(String description, Boolean done, String deadline) {
        this.description = description;
        this.done = done;
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }


}
