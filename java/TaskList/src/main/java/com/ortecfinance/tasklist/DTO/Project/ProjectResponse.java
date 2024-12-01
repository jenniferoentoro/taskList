package com.ortecfinance.tasklist.DTO.Project;

public class ProjectResponse {

    private long id;

    private String name;

    public ProjectResponse() {
    }

    public ProjectResponse(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
