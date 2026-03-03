package com.author.book_finder.user.dto;

import java.time.LocalDate;

public class PublicUserResponseDTO {

    private String username;
    private String bio;
    private String firstName;
    private String lastName;
    private LocalDate joinDate;

    public PublicUserResponseDTO(String username,
                                 String bio,
                                 String firstName,
                                 String lastName,
                                 LocalDate joinDate) {
        this.username = username;
        this.bio = bio;
        this.firstName = firstName;
        this.lastName = lastName;
        this.joinDate = joinDate;
    }

    public String getUsername() { return username; }
    public String getBio() { return bio; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDate getJoinDate() { return joinDate; }
}
