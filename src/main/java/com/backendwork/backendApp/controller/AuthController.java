package com.backendwork.backendApp.controller;


import com.backendwork.backendApp.dto.LoginRequest;
import com.backendwork.backendApp.dto.RegisterRequest;
import com.backendwork.backendApp.entity.User;
import com.backendwork.backendApp.services.AgentService;
import com.backendwork.backendApp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AgentService agentService;

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {

        User user = userService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

    @PostMapping("/login")
    public  ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = userService.login(
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(token);
    }

    @PostMapping("/agent/login")
    public ResponseEntity<String> agentLogin(
            @RequestBody LoginRequest request
    ) {

        String token = agentService.login(
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(token);
    }
}
