package com.vaibhavs_backend.proper_backend.controller;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaibhavs_backend.proper_backend.dto.LoginRequest;
import com.vaibhavs_backend.proper_backend.dto.RefreshTokenRequest;
import com.vaibhavs_backend.proper_backend.dto.RegisterRequest;
import com.vaibhavs_backend.proper_backend.entity.User;
import com.vaibhavs_backend.proper_backend.repository.UserRepository;
import com.vaibhavs_backend.proper_backend.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.vaibhavs_backend.proper_backend.dto.UserResponse;




@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        String token = authService.register(request);
        return ResponseEntity.ok(Map.of("token", token));
    }    
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        Map<String, String> token = authService.login(request);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/Users")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserResponse>> getAlluser() {
        List<User> users = userRepository.findAll();
        List<UserResponse> response = users.stream()
            .map(user -> new UserResponse(user.getId(), user.getEmail(), user.getRole()))
            .toList();
        return ResponseEntity.ok(response);
    }
    @PostMapping("/refresh")
    public ResponseEntity<Map<String,String>> refresh(@RequestBody RefreshTokenRequest request) {
        Map<String,String> token = authService.refresh(request);
        return ResponseEntity.ok(token);
    }
    
}