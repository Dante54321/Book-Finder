package edu.fiu.book_finder;

import edu.fiu.book_finder.user.User;
import edu.fiu.book_finder.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DevSeed {

    private void seed(UserRepository repo, String username, String email, String passwordHash, String bio) {
        // Inserta solo si NO existe ya por username o email
        if (!repo.existsByUsername(username) && !repo.existsByEmail(email)) {
            repo.save(new User(username, email, passwordHash, bio));
        }
    }

    @Bean
    CommandLineRunner seedUsers(UserRepository repo) {
        return args -> {

            seed(repo, "david", "david@test.com", "hash123", "First Book Finder user");
            seed(repo, "mia", "mia@test.com", "hash456", "Second Book Finder user");
            seed(repo, "mariah", "mariah@test.com", "hash789", "Third Book Finder user");
            seed(repo, "christopher", "toph@test.com", "hash123", "Fourth Book Finder user");
            seed(repo, "zachary", "zach@test.com", "hash456", "Fifth Book Finder user");

            System.out.println("=== Users in DB ===");
            repo.findAll().forEach(u ->
                    System.out.println(
                            u.getUserId() + " | " +
                                    u.getUsername() + " | " +
                                    u.getEmail() + " | " +
                                    u.getJoinDate() + " | " +
                                    u.isBanned() + " | " +
                                    u.getBio() + " | " +
                                    u.getPasswordHash()
                    )
            );
        };
    }
}

