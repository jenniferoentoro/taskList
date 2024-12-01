package com.ortecfinance.tasklist.DTO.Task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ortecfinance.tasklist.DTO.Project.ProjectResponse;

import java.util.Date;


public class TaskResponse extends TaskBaseDTO {


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date deadline;

    private ProjectResponse project;

    public TaskResponse() {
    }

    public TaskResponse(long id, String description, boolean done, Date deadline, ProjectResponse project) {
        super(id, description, done);
        this.deadline = deadline;
        this.project = project;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public ProjectResponse getProject() {
        return project;
    }

    public void setProject(ProjectResponse project) {
        this.project = project;
    }
}