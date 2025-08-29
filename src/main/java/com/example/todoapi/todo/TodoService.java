package com.example.todoapi.todo;

import com.example.todoapi.todo.dto.TodoRequest;
import com.example.todoapi.todo.dto.TodoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@Transactional
public class TodoService {
    private final TodoRepository repo;

    public TodoService(TodoRepository repo) {
        this.repo = repo;
    }

    public TodoResponse create(TodoRequest req) {
        Todo todo = new Todo();
        todo.setTitle(req.getTitle());
        todo.setDescription(req.getDescription());
        todo.setDueDate(LocalDate.now().plusDays(7));
        todo.setPriority(req.getPriority());
        todo.setCompleted(req.isCompleted());
        todo = repo.save(todo);

        return toDto(todo);
    }

    @Transactional(readOnly = true)
    public Page<TodoResponse> list(Integer page, Integer size, Boolean completed, String q) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return switch (mode(completed, q)) {
            case 1 -> repo.findByCompleted(completed, pageable).map(this::toDto);
            case 2 -> repo.findByTitleContainingIgnoreCase(q, pageable).map(this::toDto);
            case 3 -> repo.findByCompletedAndTitleContainingIgnoreCase(completed, q, pageable).map(this::toDto);
            default -> repo.findAll(pageable).map(this::toDto);
        };
    }

    @Transactional(readOnly = true)
    public TodoResponse get(Long id) {
        return toDto(find(id));
    }

    public TodoResponse update(Long id, TodoRequest req) {
        Todo todo = find(id);
        if (req.getTitle() != null) todo.setTitle(req.getTitle());
        if (req.getDescription() != null) todo.setDescription(req.getDescription());
        if (req.getDueDate() != null) todo.setDueDate(req.getDueDate());
        if (req.getPriority() != null) todo.setPriority(req.getPriority());
        if (req.isCompleted() != null) todo.setCompleted(req.isCompleted());

        return toDto(repo.save(todo));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw notFound(id);
        repo.deleteById(id);
    }

    private Todo find(Long id) {
        return repo.findById(id).orElseThrow(() -> notFound(id));
    }

    private ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo " + id + " not found");
    }

    private int mode(Boolean c, String q) {
        int m = 0;
        if (c != null) m |= 1;
        if (q != null && !q.isBlank()) m |= 2;
        return m;
    }

    private TodoResponse toDto(Todo todo) {
        TodoResponse response = new TodoResponse();
        response.setId(todo.getId());
        response.setTitle(todo.getTitle());
        response.setDescription(todo.getDescription());
        response.setDueDate(todo.getDueDate());
        response.setCompleted(todo.isCompleted());
        response.setCreatedAt(todo.getCreatedAt());
        response.setUpdatedAt(todo.getUpdatedAt());

        return response;
    }
}
