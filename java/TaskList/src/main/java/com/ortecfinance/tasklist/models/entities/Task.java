package com.ortecfinance.tasklist.models.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
public final class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Boolean done;

    private Date deadline;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public Task() {

    }

    public Task(long id, String description, Boolean done, Date deadline, Project project) {
        this.id = id;
        this.description = description;
        this.done = done;
        this.deadline = deadline;
        this.project = project;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
