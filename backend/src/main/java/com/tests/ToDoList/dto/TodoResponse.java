package com.tests.ToDoList.dto;

import com.tests.ToDoList.entity.Priority;

import java.time.LocalDateTime;

public record TodoResponse(
        Long id,
        String title,
        String description,
        Boolean completed,
        Priority priority,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
