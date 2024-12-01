package com.ortecfinance.tasklist.DTO.Project;

import jakarta.validation.constraints.NotBlank;

public class ProjectDTO {

    @NotBlank(message = "Name is required")
    private String name;

    public ProjectDTO() {
    }

    public ProjectDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}