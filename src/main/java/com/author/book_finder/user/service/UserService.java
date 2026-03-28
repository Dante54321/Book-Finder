package com.author.book_finder.user.service;

import com.author.book_finder.book.dto.BookResponseDTO;
import com.author.book_finder.book.mapper.BookMapper;
import com.author.book_finder.book.repository.BookRepository;
import com.author.book_finder.enums.PublicationStatus;
import com.author.book_finder.user.dto.PublicUserResponseDTO;
import com.author.book_finder.user.dto.UserResponseDTO;
import com.author.book_finder.user.entity.User;
import com.author.book_finder.user.mapper.UserMapper;
import com.author.book_finder.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       BookRepository bookRepository,
                       BookMapper bookMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getCurrentUserProfile(String username) {
        User user = getByUsername(username);
        return UserMapper.toResponseDTO(user, getPublishedBooksForUser(user.getUserId()));
    }

    @Transactional(readOnly = true)
    public PublicUserResponseDTO getPublicProfile(String username) {
        User user = getByUsername(username);
        return UserMapper.toPublicResponseDTO(user, getPublishedBooksForUser(user.getUserId()));
    }

    public UserResponseDTO updateCurrentUserProfile(String username,
                                                    String bio,
                                                    String firstName,
                                                    String lastName) {

        User user = getByUsername(username);

        user.setBio(normalizeText(bio));
        user.setFirstName(normalizeText(firstName));
        user.setLastName(normalizeText(lastName));

        return UserMapper.toResponseDTO(user, getPublishedBooksForUser(user.getUserId()));
    }

    public void changePassword(Long userId,
                               String oldPassword,
                               String newPassword) {

        User user = getById(userId);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
    }

    public void banUser(Long userId) {

        User user = getById(userId);

        if (user.isBanned()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User is already banned");
        }

        user.setBanned(true);
    }

    public void unbanUser(Long userId) {

        User user = getById(userId);

        if (!user.isBanned()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User is not banned");
        }

        user.setBanned(false);
    }

    private List<BookResponseDTO> getPublishedBooksForUser(Long userId) {
        return bookRepository.findByUser_UserIdAndPublicationStatusOrderByPublishDateDesc(
                        userId,
                        PublicationStatus.PUBLISHED
                ).stream()
                .map(bookMapper::toResponseDTO)
                .toList();
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }
}