package com.author.book_finder.user.controller;

import com.author.book_finder.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> banUser(@PathVariable Long id) {
        userService.banUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unbanUser(@PathVariable Long id) {
        userService.unbanUser(id);
        return ResponseEntity.noContent().build();
    }
}
