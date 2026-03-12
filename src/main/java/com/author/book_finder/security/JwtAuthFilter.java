package com.author.book_finder.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Get the Authorization header and check if starts with "Bearer "
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            try {
                username = jwtUtils.getUserNameFromJwtToken(token);
            } catch (Exception e) {
                // token invalid or expired — you can log or handle it here
            }
        }

        // If username was found and no authentication exists yet
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // load user details from DB
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // validate token again (optional but good practice)
            if (jwtUtils.validateJwtToken(token)) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                // set the authentication in context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // continue filter chain
        filterChain.doFilter(request, response);
    }
}