package com.christianvladimir.todosimple.controllers;


import com.christianvladimir.todosimple.models.User;
import com.christianvladimir.todosimple.models.dto.UserCreateDTO;
import com.christianvladimir.todosimple.models.dto.UserUpdateDTO;
import com.christianvladimir.todosimple.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/user")
@Tag(name = "User Controller", description = "Endpoint methods related to user management")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Get every task from a user")
    @GetMapping("/name")
    public ResponseEntity<User> findUser(){
        User user = this.userService.findUser();
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> findAll(){
        List<User> users = this.userService.findAll();
        return ResponseEntity.ok().body(users);
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody UserCreateDTO obj){
        User user = this.userService.fromDTO(obj);
        User newUser = this.userService.create(user);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(newUser.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@Valid @RequestBody UserUpdateDTO obj, @PathVariable Long id){
        obj.setId(id);
        User user = this.userService.fromDTO(obj);
        this.userService.update(user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        this.userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}