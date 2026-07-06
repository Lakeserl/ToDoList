package com.tests.ToDoList.service;

import com.tests.ToDoList.dto.TodoRequest;
import com.tests.ToDoList.dto.TodoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoService {

    Page<TodoResponse> getAllTodos(Boolean completed, String keyword, Pageable pageable);

    TodoResponse getTodoById(Long id);

    TodoResponse createTodo(TodoRequest request);

    TodoResponse updateTodo(Long id, TodoRequest request);

    void deleteTodo(Long id);

    TodoResponse toggleComplete(Long id);
}
