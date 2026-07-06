package com.tests.ToDoList.controller;

import tools.jackson.databind.ObjectMapper;
import com.tests.ToDoList.dto.TodoRequest;
import com.tests.ToDoList.dto.TodoResponse;
import com.tests.ToDoList.entity.Priority;
import com.tests.ToDoList.exception.GlobalExceptionHandler;
import com.tests.ToDoList.exception.TodoNotFoundException;
import com.tests.ToDoList.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    private TodoResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = new TodoResponse(
                1L, "Test Todo", "Test Description",
                false, Priority.MEDIUM,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void getAllTodos_returnsPagedResults() throws Exception {
        when(todoService.getAllTodos(any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sampleResponse)));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Todo"))
                .andExpect(jsonPath("$.content[0].completed").value(false));
    }

    @Test
    void getTodoById_whenExists_returnsTodo() throws Exception {
        when(todoService.getTodoById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Todo"));
    }

    @Test
    void getTodoById_whenNotExists_returns404() throws Exception {
        when(todoService.getTodoById(99L)).thenThrow(new TodoNotFoundException(99L));

        mockMvc.perform(get("/api/todos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Todo not found with id: 99"));
    }

    @Test
    void createTodo_withValidRequest_returns201() throws Exception {
        TodoRequest request = new TodoRequest("New Todo", "Description", false, Priority.HIGH);
        when(todoService.createTodo(any(TodoRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Todo"));
    }

    @Test
    void createTodo_withBlankTitle_returns400() throws Exception {
        TodoRequest request = new TodoRequest("", "Description", false, Priority.MEDIUM);

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").exists());
    }

    @Test
    void createTodo_withNullTitle_returns400() throws Exception {
        TodoRequest request = new TodoRequest(null, "Description", false, Priority.MEDIUM);

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").exists());
    }

    @Test
    void updateTodo_withValidRequest_returns200() throws Exception {
        TodoRequest request = new TodoRequest("Updated", "Updated Desc", true, Priority.HIGH);
        TodoResponse updatedResponse = new TodoResponse(
                1L, "Updated", "Updated Desc",
                true, Priority.HIGH,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(todoService.updateTodo(eq(1L), any(TodoRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    void deleteTodo_returns204() throws Exception {
        doNothing().when(todoService).deleteTodo(1L);

        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTodo_whenNotExists_returns404() throws Exception {
        doThrow(new TodoNotFoundException(99L)).when(todoService).deleteTodo(99L);

        mockMvc.perform(delete("/api/todos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void toggleComplete_returns200() throws Exception {
        TodoResponse toggledResponse = new TodoResponse(
                1L, "Test Todo", "Test Description",
                true, Priority.MEDIUM,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(todoService.toggleComplete(1L)).thenReturn(toggledResponse);

        mockMvc.perform(patch("/api/todos/1/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }
}
