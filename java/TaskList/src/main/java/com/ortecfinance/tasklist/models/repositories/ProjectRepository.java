package com.ortecfinance.tasklist.models.repositories;

import com.ortecfinance.tasklist.models.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProjectRepository extends JpaRepository<Project, Long> {
}
