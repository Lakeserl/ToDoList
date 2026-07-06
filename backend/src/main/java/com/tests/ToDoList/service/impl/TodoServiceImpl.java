package com.tests.ToDoList.service.impl;

import com.tests.ToDoList.dto.TodoRequest;
import com.tests.ToDoList.dto.TodoResponse;
import com.tests.ToDoList.entity.Todo;
import com.tests.ToDoList.exception.TodoNotFoundException;
import com.tests.ToDoList.mapper.TodoMapper;
import com.tests.ToDoList.repository.TodoRepository;
import com.tests.ToDoList.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    @Override
    public Page<TodoResponse> getAllTodos(Boolean completed, String keyword, Pageable pageable) {
        Page<Todo> todos;

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasCompleted = completed != null;

        if (hasCompleted && hasKeyword) {
            todos = todoRepository.findByCompletedAndTitleContainingIgnoreCase(
                    completed, keyword.trim(), pageable);
        } else if (hasCompleted) {
            todos = todoRepository.findByCompleted(completed, pageable);
        } else if (hasKeyword) {
            todos = todoRepository.findByTitleContainingIgnoreCase(keyword.trim(), pageable);
        } else {
            todos = todoRepository.findAll(pageable);
        }

        return todos.map(TodoMapper::toResponse);
    }

    @Override
    public TodoResponse getTodoById(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
        return TodoMapper.toResponse(todo);
    }

    @Override
    @Transactional
    public TodoResponse createTodo(TodoRequest request) {
        Todo todo = TodoMapper.toEntity(request);
        Todo saved = todoRepository.save(todo);
        return TodoMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public TodoResponse updateTodo(Long id, TodoRequest request) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
        TodoMapper.updateEntity(todo, request);
        Todo updated = todoRepository.save(todo);
        return TodoMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteTodo(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        todoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public TodoResponse toggleComplete(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
        todo.setCompleted(!todo.getCompleted());
        Todo updated = todoRepository.save(todo);
        return TodoMapper.toResponse(updated);
    }
}
