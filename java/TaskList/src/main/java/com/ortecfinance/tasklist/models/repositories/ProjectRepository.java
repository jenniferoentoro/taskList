package com.ortecfinance.tasklist.models.repositories;

import com.ortecfinance.tasklist.models.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {


}
