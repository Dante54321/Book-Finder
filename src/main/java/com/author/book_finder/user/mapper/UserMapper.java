package com.author.book_finder.user.mapper;

import com.author.book_finder.book.dto.BookResponseDTO;
import com.author.book_finder.user.dto.PublicUserResponseDTO;
import com.author.book_finder.user.dto.UserResponseDTO;
import com.author.book_finder.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    private UserMapper() {}

    public static UserResponseDTO toResponseDTO(User user, List<BookResponseDTO> books) {
        return new UserResponseDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getBio(),
                user.getFirstName(),
                user.getLastName(),
                user.getJoinDate(),
                user.isBanned(),
                user.getRoles()
                        .stream()
                        .map(role -> role.getRoleName().name())
                        .collect(Collectors.toSet()),
                books
        );
    }

    public static PublicUserResponseDTO toPublicResponseDTO(User user, List<BookResponseDTO> books) {
        return new PublicUserResponseDTO(
                user.getUsername(),
                user.getBio(),
                user.getFirstName(),
                user.getLastName(),
                user.getJoinDate(),
                books
        );
    }
}