package com.example.todoapi.todo;

import com.example.todoapi.todo.dto.TodoRequest;
import com.example.todoapi.todo.dto.TodoResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @PostMapping
    public TodoResponse create(@Valid @RequestBody TodoRequest req) {
        return service.create(req);
    }

    @GetMapping
    public Page<TodoResponse> list(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) String q) {
        return service.list(page, size, completed, q);
    }

    @GetMapping("/{id}")
    public TodoResponse get(@PathVariable Long id, @RequestBody TodoRequest req) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public TodoResponse update(@PathVariable Long id, @RequestBody TodoRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
