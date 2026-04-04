package com.author.book_finder.infrastructure.seed;

import com.author.book_finder.book.entity.Book;
import com.author.book_finder.book.repository.BookRepository;
import com.author.book_finder.enums.PublicationStatus;
import com.author.book_finder.genre.entity.Genre;
import com.author.book_finder.genre.repository.GenreRepository;
import com.author.book_finder.hashtag.entity.Hashtag;
import com.author.book_finder.hashtag.repository.HashtagRepository;
import com.author.book_finder.review.entity.Review;
import com.author.book_finder.review.repository.ReviewRepository;
import com.author.book_finder.series.entity.Series;
import com.author.book_finder.series.repository.SeriesRepository;
import com.author.book_finder.user.entity.Role;
import com.author.book_finder.user.entity.User;
import com.author.book_finder.user.enums.RoleName;
import com.author.book_finder.user.repository.RoleRepository;
import com.author.book_finder.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Transactional
@ConditionalOnProperty(name = "app.demo.seed.enabled", havingValue = "true")
public class DemoDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GenreRepository genreRepository;
    private final HashtagRepository hashtagRepository;
    private final SeriesRepository seriesRepository;
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataSeeder(UserRepository userRepository,
                          RoleRepository roleRepository,
                          GenreRepository genreRepository,
                          HashtagRepository hashtagRepository,
                          SeriesRepository seriesRepository,
                          BookRepository bookRepository,
                          ReviewRepository reviewRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.genreRepository = genreRepository;
        this.hashtagRepository = hashtagRepository;
        this.seriesRepository = seriesRepository;
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0 || bookRepository.count() > 0 || reviewRepository.count() > 0) {
            log.info("DemoDataSeeder skipped: demo data already exists.");
            return;
        }

        log.info("Starting demo data seed...");

        Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("ROLE_USER not found. Flyway role seed may not have run."));

        // ---- USERS ----
        User author1 = createUser(
                "luna.mercer",
                "luna@bookfinder.demo",
                "Luna",
                "Mercer",
                "Fantasy author who writes about fallen kingdoms and cursed crowns.",
                userRole
        );

        User author2 = createUser(
                "orion.vale",
                "orion@bookfinder.demo",
                "Orion",
                "Vale",
                "Sci-fi writer focused on neon cities, rogue signals, and broken futures.",
                userRole
        );

        User author3 = createUser(
                "ivy.marlow",
                "ivy@bookfinder.demo",
                "Ivy",
                "Marlow",
                "Writes emotional contemporary stories with romance and family tension.",
                userRole
        );

        User reader1 = createUser(
                "mia.reader",
                "mia@bookfinder.demo",
                "Mia",
                "Reader",
                "A passionate reader who loves fantasy and romance.",
                userRole
        );

        User reader2 = createUser(
                "noah.reviews",
                "noah@bookfinder.demo",
                "Noah",
                "Reviews",
                "Always leaving reviews and discovering new books.",
                userRole
        );

        User reader3 = createUser(
                "camila.pages",
                "camila@bookfinder.demo",
                "Camila",
                "Pages",
                "Loves thrillers, sci-fi, and binge-reading series.",
                userRole
        );

        userRepository.saveAll(List.of(author1, author2, author3, reader1, reader2, reader3));

        // ---- SERIES ----
        Series emberSeries = createSeries(
                author1,
                "Chronicles of Ember",
                "A dark fantasy series about a ruined crown, old magic, and a kingdom on the edge of collapse.",
                LocalDate.of(2024, 2, 10)
        );

        Series neonSeries = createSeries(
                author2,
                "Neon City Files",
                "A science fiction series set in a surveillance-heavy metropolis where every signal hides a secret.",
                LocalDate.of(2024, 4, 5)
        );

        seriesRepository.saveAll(List.of(emberSeries, neonSeries));

        // ---- BOOKS ----
        Book book1 = createBook(
                author1,
                emberSeries,
                "Ashes of the First King",
                "When a forgotten heir discovers a ruined throne beneath the capital, old powers awaken and drag the kingdom toward war.",
                LocalDate.of(2024, 3, 1),
                List.of("Fantasy", "Dark Fantasy", "Adventure"),
                List.of("kingdom", "magic", "crown", "war")
        );

        Book book2 = createBook(
                author1,
                emberSeries,
                "Crown of Cinders",
                "The throne has been claimed, but the deeper enemy sleeps beneath the city and feeds on every promise of power.",
                LocalDate.of(2024, 8, 12),
                List.of("Fantasy", "Dark Fantasy", "Action"),
                List.of("sequel", "darkmagic", "betrayal", "throne")
        );

        Book book3 = createBook(
                author2,
                neonSeries,
                "Signal in the Static",
                "A data courier intercepts a forbidden transmission that exposes a conspiracy linking the city grid to vanished citizens.",
                LocalDate.of(2024, 5, 9),
                List.of("Science Fiction", "Cyberpunk", "Thriller"),
                List.of("cyberpunk", "signal", "conspiracy", "future")
        );

        Book book4 = createBook(
                author2,
                neonSeries,
                "Midnight Circuit",
                "As the blackout spreads across the district, the people hunting the truth discover that the city was designed to lie.",
                LocalDate.of(2024, 11, 3),
                List.of("Science Fiction", "Cyberpunk", "Suspense"),
                List.of("ai", "city", "surveillance", "blackout")
        );

        Book book5 = createBook(
                author3,
                null,
                "The Last Lighthouse",
                "A quiet coastal drama where two estranged siblings return home and confront the memories they tried to outrun.",
                LocalDate.of(2024, 6, 21),
                List.of("Drama", "Contemporary Fiction", "Literary Fiction"),
                List.of("family", "coast", "grief", "homecoming")
        );

        Book book6 = createBook(
                author3,
                null,
                "Velvet & Vows",
                "A contemporary romance about ambition, second chances, and the wedding season that keeps throwing two rivals together.",
                LocalDate.of(2025, 1, 14),
                List.of("Romance", "Contemporary Fiction", "Young Adult"),
                List.of("romance", "wedding", "rivals", "secondchance")
        );

        bookRepository.saveAll(List.of(book1, book2, book3, book4, book5, book6));

        // ---- REVIEWS ----
        saveReview(reader1, book1, 5, "Great worldbuilding and a really strong opening.");
        saveReview(reader2, book1, 4, "Very solid fantasy setup. I would definitely read the sequel.");
        saveReview(reader3, book1, 5, "My favorite demo book so far. Feels epic right away.");

        saveReview(reader1, book2, 4, "Good sequel with higher stakes.");
        saveReview(reader2, book2, 5, "Loved the darker tone and the pacing.");

        saveReview(reader1, book3, 5, "Fast, stylish, and easy to imagine visually.");
        saveReview(reader3, book3, 4, "The mystery and tech atmosphere were really good.");

        saveReview(reader2, book4, 5, "This one feels like a strong cyberpunk thriller.");
        saveReview(reader3, book4, 4, "Very cool premise and tension.");

        saveReview(reader2, book5, 4, "More emotional and quiet, but very well written.");
        saveReview(reader1, book6, 5, "Fun romance with strong chemistry.");
        saveReview(reader3, book6, 4, "Easy read and very charming.");

        log.info("Demo data seeded successfully.");
    }

    private User createUser(String username,
                            String email,
                            String firstName,
                            String lastName,
                            String bio,
                            Role userRole) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("Password123!"));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setBio(bio);
        user.setBanned(false);
        user.replaceRoles(Set.of(userRole));
        return user;
    }

    private Series createSeries(User author,
                                String name,
                                String description,
                                LocalDate publishDate) {
        Series series = new Series();
        series.setSeriesName(name);
        series.setDescription(description);
        series.setPublishDate(publishDate);
        author.addSeries(series);
        return series;
    }

    private Book createBook(User author,
                            Series series,
                            String title,
                            String summary,
                            LocalDate publishDate,
                            List<String> genreNames,
                            List<String> hashtags) {
        Book book = new Book();
        book.setTitle(title);
        book.setSummary(summary);
        book.setPublishDate(publishDate);
        book.setPublicationStatus(PublicationStatus.PUBLISHED);

        author.addBook(book);

        if (series != null) {
            series.addBook(book);
        }

        book.replaceGenres(resolveGenres(genreNames));
        book.replaceHashtags(resolveHashtags(hashtags));

        return book;
    }

    private void saveReview(User reviewer, Book book, int rating, String comment) {
        if (reviewer.getUserId().equals(book.getUser().getUserId())) {
            throw new IllegalStateException("Demo review cannot be created by the author of the book.");
        }

        Review review = new Review();
        review.setRating(rating);
        review.setComment(comment);

        reviewer.addReview(review);
        book.addReview(review);

        reviewRepository.save(review);
    }

    private Set<Genre> resolveGenres(List<String> genreNames) {
        return genreNames.stream()
                .map(name -> genreRepository.findByGenreName(name)
                        .orElseThrow(() -> new IllegalStateException("Genre not found: " + name)))
                .collect(Collectors.toSet());
    }

    private Set<Hashtag> resolveHashtags(List<String> tagNames) {
        return tagNames.stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(tag -> !tag.isBlank())
                .map(tag -> hashtagRepository.findByHashtag(tag)
                        .orElseGet(() -> hashtagRepository.save(new Hashtag(tag))))
                .collect(Collectors.toSet());
    }
}