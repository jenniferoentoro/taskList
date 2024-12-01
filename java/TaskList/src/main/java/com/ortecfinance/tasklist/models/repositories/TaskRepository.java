package com.ortecfinance.tasklist.models.repositories;

import com.ortecfinance.tasklist.models.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(long projectId);

    Optional<Task> findByProjectIdAndId(long projectId, long taskId);

    @Query("SELECT DISTINCT t.deadline FROM Task t ORDER BY t.deadline ASC")
    List<Date> findDistinctDeadline();


    List<Task> findByDeadline(Date deadline);

}
