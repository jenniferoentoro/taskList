package com.ortecfinance.tasklist.DTO.Project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ortecfinance.tasklist.DTO.Task.TaskWithProjectResponseNoDeadline;

import java.util.Date;
import java.util.List;

public class ProjectDeadlineResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date deadline;

    private List<TaskWithProjectResponseNoDeadline> project;

    public ProjectDeadlineResponse() {
    }

    public ProjectDeadlineResponse(Date deadline, List<TaskWithProjectResponseNoDeadline> project) {
        this.deadline = deadline;
        this.project = project;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public List<TaskWithProjectResponseNoDeadline> getProject() {
        return project;
    }

    public void setProject(List<TaskWithProjectResponseNoDeadline> project) {
        this.project = project;
    }
}
