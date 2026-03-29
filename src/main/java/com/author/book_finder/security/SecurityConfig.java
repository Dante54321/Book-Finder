package com.author.book_finder.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                          UserDetailsServiceImpl userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers("/error").permitAll()
                                .requestMatchers("/api/auth/**").permitAll()

                                // BROWSE SERIES (PUBLIC)
                                .requestMatchers(HttpMethod.GET, "/api/series/list").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/series/*/cover").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/series/*/details").permitAll()

                                // BROWSE BOOK (PUBLIC)
                                .requestMatchers(HttpMethod.GET, "/api/books/list").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/books/top-rated").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/books/*/details").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/books/*/cover").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/books/search").permitAll()

                                // GENRES (PUBLIC)
                                .requestMatchers(HttpMethod.GET, "/api/genres").permitAll()

                                // BROWSE CHAPTER (PUBLIC) VIEW PREVIEW CHAPTER (PUBLIC)
                                .requestMatchers(HttpMethod.GET, "/api/chapters/*/preview").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/books/*/chapters/list").permitAll()

                                // USER PROFILE (PUBLIC)
                                .requestMatchers(HttpMethod.GET, "/api/users/profile/*").permitAll()

                                // BOOK REVIEWS (PUBLIC)
                                .requestMatchers(HttpMethod.GET, "/api/books/*/reviews").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/books/*/reviews/summary").permitAll()

                                .anyRequest().authenticated()
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("""
                                {
                                    "status": 401,
                                    "error": "Unauthorized",
                                    "message": "Authentication required"
                                }
                            """);
                        })
                )

                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:5500",
                "https://miamader.github.io"
        ));

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of("*"));

        config.setAllowCredentials(true);

        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}