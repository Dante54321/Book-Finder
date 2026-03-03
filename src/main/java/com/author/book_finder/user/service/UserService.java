package com.author.book_finder.user.service;

import com.author.book_finder.user.entity.User;
import com.author.book_finder.user.repository.UserRepository;
import com.author.book_finder.user.mapper.UserMapper;

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

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

    public User updateCurrentUser(String username,
                              String bio,
                              String firstName,
                              String lastName) {

        User user = getByUsername(username);

        user.setBio(bio);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        return user;
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
}
