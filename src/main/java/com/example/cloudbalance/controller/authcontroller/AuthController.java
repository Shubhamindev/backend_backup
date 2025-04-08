package com.example.cloudbalance.controller.authcontroller;

import com.example.cloudbalance.dto.authdto.AuthUserRequestDTO;
import com.example.cloudbalance.dto.authdto.AuthUserResponseDTO;
import com.example.cloudbalance.service.authservice.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody AuthUserRequestDTO userRequest) {
        return authService.registerUser(userRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthUserResponseDTO> loginUser(@RequestBody AuthUserRequestDTO userRequest) {
        return authService.loginUser(userRequest);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthUserResponseDTO> refreshToken(@RequestBody String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid token");
        }
        String token = authHeader.substring(7);
        return authService.logoutUser(token);
    }

    @PostMapping("/register-admin")
    public ResponseEntity<String> registerAdmin(@RequestBody AuthUserRequestDTO userRequest) {
        return authService.registerAdmin(userRequest);
    }
}