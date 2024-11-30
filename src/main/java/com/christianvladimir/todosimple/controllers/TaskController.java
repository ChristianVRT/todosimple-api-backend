package com.christianvladimir.todosimple.controllers;

import com.christianvladimir.todosimple.models.Task;
import com.christianvladimir.todosimple.models.projection.TaskProjection;
import com.christianvladimir.todosimple.services.TaskService;
import com.christianvladimir.todosimple.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/task")
@Tag(name = "Task Controller", description = "Endpoint methods related to task management")
@Validated
public class  TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Operation(summary = "Get task by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable Long id) {
        Task task = this.taskService.findById(id);
        return ResponseEntity.ok().body(task);
    }

    @Operation(summary = "Get every task from a user")
    @GetMapping("/user")
    public ResponseEntity<List<TaskProjection>> findAllByUser() {
        List<TaskProjection> tasks = this.taskService.findAllByUser();
        return ResponseEntity.ok().body(tasks);
    }

    @Operation(summary = "Get all tasks")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<Task>> findAll() {
        List<Task> tasks = this.taskService.findAll();
        return ResponseEntity.ok().body(tasks);
    }

    @Operation(summary = "Create task")
    @PostMapping
    @Validated
    public ResponseEntity<Void> create(@Valid @RequestBody Task task) {
        this.taskService.create(task);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(task.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Update task")
    @PutMapping("/{id}")
    @Validated
    public ResponseEntity<Void> update(@Valid @RequestBody Task task, @PathVariable Long id) {
        task.setId(id);
        this.taskService.Update(task);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete task")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
