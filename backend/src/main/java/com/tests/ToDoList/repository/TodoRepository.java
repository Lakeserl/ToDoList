package com.tests.ToDoList.repository;

import com.tests.ToDoList.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    Page<Todo> findByCompleted(Boolean completed, Pageable pageable);

    Page<Todo> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Todo> findByCompletedAndTitleContainingIgnoreCase(Boolean completed, String keyword, Pageable pageable);
}
