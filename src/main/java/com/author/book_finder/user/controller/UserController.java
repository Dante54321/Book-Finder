package com.author.book_finder.user.controller;

import com.author.book_finder.user.dto.PublicUserResponseDTO;
import com.author.book_finder.user.dto.UpdateProfileRequestDTO;
import com.author.book_finder.user.dto.UserResponseDTO;
import com.author.book_finder.user.entity.User;
import com.author.book_finder.user.mapper.UserMapper;
import com.author.book_finder.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    //
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();

        User user = userService.getByUsername(username);

        return ResponseEntity.ok(UserMapper.toResponseDTO(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateProfile(
            Authentication authentication,
            @RequestBody UpdateProfileRequestDTO request) {

        String username = authentication.getName();

        User updatedUser = userService.updateCurrentUser(
                username,
                request.getBio(),
                request.getFirstName(),
                request.getLastName()
        );

        return ResponseEntity.ok(UserMapper.toResponseDTO(updatedUser));
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<PublicUserResponseDTO> getPublicProfile(
            @PathVariable String username) {

        User user = userService.getByUsername(username);

        return ResponseEntity.ok(UserMapper.toPublicResponseDTO(user));
    }
}
