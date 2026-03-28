package com.author.book_finder.user.dto;

import com.author.book_finder.book.dto.BookResponseDTO;

import java.time.LocalDate;
import java.util.List;

public class PublicUserResponseDTO {

    private String username;
    private String bio;
    private String firstName;
    private String lastName;
    private LocalDate joinDate;
    private List<BookResponseDTO> books;

    public PublicUserResponseDTO(String username,
                                 String bio,
                                 String firstName,
                                 String lastName,
                                 LocalDate joinDate,
                                 List<BookResponseDTO> books) {
        this.username = username;
        this.bio = bio;
        this.firstName = firstName;
        this.lastName = lastName;
        this.joinDate = joinDate;
        this.books = books;
    }

    public String getUsername() { return username; }
    public String getBio() { return bio; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDate getJoinDate() { return joinDate; }
    public List<BookResponseDTO> getBooks() { return books; }
}