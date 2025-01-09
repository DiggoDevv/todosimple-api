package com.rodrigo.todosimple.repositories;

import java.util.List;

import com.rodrigo.todosimple.models.projection.TaskProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rodrigo.todosimple.models.Task;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<TaskProjection> findByUser_Id(Long id);
}
