package com.author.book_finder.controller;

import com.author.book_finder.dto.*;
import com.author.book_finder.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> registerUser(
            @RequestBody SignupRequestDTO signupRequestDTO) {

        SignupResponseDTO response = authService.registerUser(signupRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtResponseDTO> authenticateUser(
            @RequestBody LoginRequestDTO loginRequestDTO) {

        JwtResponseDTO response = authService.authenticateUser(loginRequestDTO);
        return ResponseEntity.ok(response);
    }
}