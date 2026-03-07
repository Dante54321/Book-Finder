package com.author.book_finder.auth.service;

import com.author.book_finder.auth.dto.JwtResponseDTO;
import com.author.book_finder.auth.dto.LoginRequestDTO;
import com.author.book_finder.auth.dto.SignupRequestDTO;
import com.author.book_finder.auth.dto.SignupResponseDTO;
import com.author.book_finder.user.entity.Role;
import com.author.book_finder.user.enums.RoleName;
import com.author.book_finder.user.entity.User;
import com.author.book_finder.user.repository.RoleRepository;
import com.author.book_finder.user.repository.UserRepository;
import com.author.book_finder.security.JwtUtils;
import com.author.book_finder.security.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public SignupResponseDTO registerUser(SignupRequestDTO signupRequestDTO) {
        // Check for existing username
        if (userRepository.findByUsername(signupRequestDTO.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Username is already taken!");
        }

        // Check for existing email
        if (userRepository.findByEmail(signupRequestDTO.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Email is already taken!");
        }

        // Create new user
        User user = new User();
        user.setUsername(signupRequestDTO.getUsername());
        user.setEmail(signupRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequestDTO.getPassword()));

        // Assign default ROLE_USER
        Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("ROLE_USER not found"));


        user.replaceRoles(Set.of(userRole));
        userRepository.save(user);

        return new SignupResponseDTO("User registered successfully!");
    }

    public JwtResponseDTO authenticateUser(LoginRequestDTO loginRequestDTO) {

         User u = userRepository.findByUsername(loginRequestDTO.getUsername()).orElse(null);
    if (u != null) {
        System.out.println("Found user: " + u.getUsername());
        System.out.println("Stored hash: " + u.getPassword());
        System.out.println("Password matches: " + passwordEncoder.matches(loginRequestDTO.getPassword(), u.getPassword()));
        System.out.println("Is banned: " + u.isBanned());
    } else {
        System.out.println("User NOT found in DB");
    }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getUsername(),
                        loginRequestDTO.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toSet());

        return new JwtResponseDTO(
                jwt,
                userDetails.getUserId(),
                userDetails.getUsername(),
                roles
        );
    }
}

