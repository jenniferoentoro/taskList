package com.ortecfinance.tasklist.DTO.Task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ortecfinance.tasklist.DTO.Project.ProjectResponse;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;


public class TaskBaseDTO {

    private long id;
    private String description;

    private boolean done;

    public TaskBaseDTO() {
    }

    public TaskBaseDTO(long id, String description, boolean done) {
        this.id = id;
        this.description = description;
        this.done = done;

    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}