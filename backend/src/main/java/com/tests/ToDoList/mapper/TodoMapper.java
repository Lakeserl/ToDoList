package com.tests.ToDoList.mapper;

import com.tests.ToDoList.dto.TodoRequest;
import com.tests.ToDoList.dto.TodoResponse;
import com.tests.ToDoList.entity.Priority;
import com.tests.ToDoList.entity.Todo;

public class TodoMapper {

    private TodoMapper() {
    }

    public static Todo toEntity(TodoRequest request) {
        return Todo.builder()
                .title(request.title().trim())
                .description(request.description() != null ? request.description().trim() : null)
                .completed(request.completed() != null ? request.completed() : false)
                .priority(request.priority() != null ? request.priority() : Priority.MEDIUM)
                .build();
    }

    public static TodoResponse toResponse(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getCompleted(),
                todo.getPriority(),
                todo.getCreatedAt(),
                todo.getUpdatedAt()
        );
    }

    public static void updateEntity(Todo todo, TodoRequest request) {
        todo.setTitle(request.title().trim());
        todo.setDescription(request.description() != null ? request.description().trim() : null);
        if (request.completed() != null) {
            todo.setCompleted(request.completed());
        }
        if (request.priority() != null) {
            todo.setPriority(request.priority());
        }
    }
}
