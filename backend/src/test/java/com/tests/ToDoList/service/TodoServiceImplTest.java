package com.tests.ToDoList.service;

import com.tests.ToDoList.dto.TodoRequest;
import com.tests.ToDoList.dto.TodoResponse;
import com.tests.ToDoList.entity.Priority;
import com.tests.ToDoList.entity.Todo;
import com.tests.ToDoList.exception.TodoNotFoundException;
import com.tests.ToDoList.repository.TodoRepository;
import com.tests.ToDoList.service.impl.TodoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoServiceImpl todoService;

    private Todo sampleTodo;
    private TodoRequest sampleRequest;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        sampleTodo = Todo.builder()
                .id(1L)
                .title("Test Todo")
                .description("Test Description")
                .completed(false)
                .priority(Priority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequest = new TodoRequest("Test Todo", "Test Description", false, Priority.MEDIUM);
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getAllTodos_withNoFilters_returnsAll() {
        Page<Todo> page = new PageImpl<>(List.of(sampleTodo));
        when(todoRepository.findAll(pageable)).thenReturn(page);

        Page<TodoResponse> result = todoService.getAllTodos(null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().title()).isEqualTo("Test Todo");
        verify(todoRepository).findAll(pageable);
    }

    @Test
    void getAllTodos_withCompletedFilter_filtersCorrectly() {
        Page<Todo> page = new PageImpl<>(List.of(sampleTodo));
        when(todoRepository.findByCompleted(false, pageable)).thenReturn(page);

        Page<TodoResponse> result = todoService.getAllTodos(false, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(todoRepository).findByCompleted(false, pageable);
    }

    @Test
    void getAllTodos_withKeyword_searchesCorrectly() {
        Page<Todo> page = new PageImpl<>(List.of(sampleTodo));
        when(todoRepository.findByTitleContainingIgnoreCase("Test", pageable)).thenReturn(page);

        Page<TodoResponse> result = todoService.getAllTodos(null, "Test", pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(todoRepository).findByTitleContainingIgnoreCase("Test", pageable);
    }

    @Test
    void getAllTodos_withBothFilters_appliesBoth() {
        Page<Todo> page = new PageImpl<>(List.of(sampleTodo));
        when(todoRepository.findByCompletedAndTitleContainingIgnoreCase(false, "Test", pageable))
                .thenReturn(page);

        Page<TodoResponse> result = todoService.getAllTodos(false, "Test", pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(todoRepository).findByCompletedAndTitleContainingIgnoreCase(false, "Test", pageable);
    }

    @Test
    void getTodoById_whenExists_returnsTodo() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));

        TodoResponse result = todoService.getTodoById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Test Todo");
    }

    @Test
    void getTodoById_whenNotExists_throwsException() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.getTodoById(99L))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createTodo_savesAndReturns() {
        when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

        TodoResponse result = todoService.createTodo(sampleRequest);

        assertThat(result.title()).isEqualTo("Test Todo");
        assertThat(result.completed()).isFalse();
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    void updateTodo_whenExists_updatesAndReturns() {
        TodoRequest updateRequest = new TodoRequest("Updated Title", "Updated Desc", true, Priority.HIGH);
        Todo updatedTodo = Todo.builder()
                .id(1L)
                .title("Updated Title")
                .description("Updated Desc")
                .completed(true)
                .priority(Priority.HIGH)
                .createdAt(sampleTodo.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(updatedTodo);

        TodoResponse result = todoService.updateTodo(1L, updateRequest);

        assertThat(result.title()).isEqualTo("Updated Title");
        assertThat(result.completed()).isTrue();
        assertThat(result.priority()).isEqualTo(Priority.HIGH);
    }

    @Test
    void updateTodo_whenNotExists_throwsException() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.updateTodo(99L, sampleRequest))
                .isInstanceOf(TodoNotFoundException.class);
    }

    @Test
    void deleteTodo_whenExists_deletes() {
        when(todoRepository.existsById(1L)).thenReturn(true);

        todoService.deleteTodo(1L);

        verify(todoRepository).deleteById(1L);
    }

    @Test
    void deleteTodo_whenNotExists_throwsException() {
        when(todoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> todoService.deleteTodo(99L))
                .isInstanceOf(TodoNotFoundException.class);
    }

    @Test
    void toggleComplete_togglesFalseToTrue() {
        Todo toggledTodo = Todo.builder()
                .id(1L)
                .title("Test Todo")
                .completed(true)
                .priority(Priority.MEDIUM)
                .createdAt(sampleTodo.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(toggledTodo);

        TodoResponse result = todoService.toggleComplete(1L);

        assertThat(result.completed()).isTrue();
    }

    @Test
    void toggleComplete_whenNotExists_throwsException() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.toggleComplete(99L))
                .isInstanceOf(TodoNotFoundException.class);
    }
}
