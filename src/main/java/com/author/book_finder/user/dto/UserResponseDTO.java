package com.author.book_finder.user.dto;

import java.time.LocalDate;
import java.util.Set;

public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String bio;
    private String firstName;
    private String lastName;
    private LocalDate joinDate;
    private boolean isBanned;
    private Set<String> roles;

    public UserResponseDTO(Long id,
                           String username,
                           String email,
                           String bio,
                           String firstName,
                           String lastName,
                           LocalDate joinDate,
                           boolean isBanned,
                           Set<String> roles) {

        this.id = id;
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.firstName = firstName;
        this.lastName = lastName;
        this.joinDate = joinDate;
        this.isBanned = isBanned;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getBio() {
        return bio;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public Set<String> getRoles() {
        return roles;
    }
}
