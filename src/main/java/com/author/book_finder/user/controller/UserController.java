package com.author.book_finder.user.controller;

import com.author.book_finder.user.dto.PublicUserResponseDTO;
import com.author.book_finder.user.dto.UpdateProfileRequestDTO;
import com.author.book_finder.user.dto.UserResponseDTO;
import com.author.book_finder.user.entity.User;
import com.author.book_finder.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET USER BY ID
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    // LIST ALL USERS
    @GetMapping("/users/list")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // GET CURRENT USER
    @GetMapping("/users/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUserProfile(authentication.getName()));
    }

    // UPDATE PROFILE
    @PutMapping("/users/me")
    public ResponseEntity<UserResponseDTO> updateProfile(
            Authentication authentication,
            @RequestBody UpdateProfileRequestDTO request) {

        return ResponseEntity.ok(
                userService.updateCurrentUserProfile(
                        authentication.getName(),
                        request.getBio(),
                        request.getFirstName(),
                        request.getLastName()
                )
        );
    }

    // VIEW PUBLIC PROFILE
    @GetMapping("/users/profile/{username}")
    public ResponseEntity<PublicUserResponseDTO> getPublicProfile(
            @PathVariable String username) {

        return ResponseEntity.ok(userService.getPublicProfile(username));
    }
}