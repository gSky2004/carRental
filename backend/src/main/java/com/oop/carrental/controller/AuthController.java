package com.oop.carrental.controller;

import com.oop.carrental.dto.LoginRequest;
import com.oop.carrental.dto.LoginResponse;
import com.oop.carrental.dto.RegisterRequest;
import com.oop.carrental.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest request) {

        LoginResponse response = authService.register(request);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authService.logout(authHeader.substring(7));
        }

        return ResponseEntity.ok().build();

    }

    @GetMapping("/validate")
    public ResponseEntity<LoginResponse> validate(@RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            LoginResponse adminInfo = authService.getAdminByToken(token);

            if (adminInfo != null) {
                return ResponseEntity.ok(adminInfo);
            }
        }

        return ResponseEntity.status(401).build();

    }

}
