package com.paypal.user_service.controller;

import com.paypal.user_service.entity.User;
import com.paypal.user_service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    ResponseEntity<User> createUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    ResponseEntity<User> findUserById(@PathVariable Long id) {
        return userService.getUserById(id).map(ResponseEntity :: ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }
}
