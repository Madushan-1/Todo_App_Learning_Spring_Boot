package com.example.todoapi.todo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    Page<Todo> findByCompleted(boolean completed, Pageable pageable);

    Page<Todo> findByTitleContainingIgnoreCase(String q, Pageable pageable);

    Page<Todo> findByCompletedAndTitleContainingIgnoreCase(boolean completed, String q, Pageable pageable);
}
