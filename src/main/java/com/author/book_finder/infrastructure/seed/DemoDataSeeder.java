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
import java.util.*;
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

        log.info("Starting large demo data seed...");

        Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("ROLE_USER not found."));

        List<AuthorSeed> authorSeeds = buildAuthorSeeds();

        List<User> authors = new ArrayList<>();
        List<Series> allSeries = new ArrayList<>();
        List<Book> allBooks = new ArrayList<>();

        int globalBookIndex = 0;

        // ----- AUTHORS + SERIES + BOOKS -----
        for (AuthorSeed seed : authorSeeds) {
            User author = createUser(
                    buildUsername(seed.firstName(), seed.lastName()),
                    buildEmail(seed.firstName(), seed.lastName()),
                    seed.firstName(),
                    seed.lastName(),
                    seed.bio(),
                    userRole
            );

            authors.add(author);
            userRepository.save(author);

            Series series = null;
            if (seed.seriesName() != null && !seed.seriesName().isBlank()) {
                series = createSeries(
                        author,
                        seed.seriesName(),
                        seed.seriesDescription(),
                        LocalDate.of(2024, 1, 15).plusDays(globalBookIndex * 3L)
                );
                seriesRepository.save(series);
                allSeries.add(series);
            }

            List<String> titles = seed.titles();
            for (int i = 0; i < titles.size(); i++) {
                boolean inSeries = series != null && i < 3;
                Integer volumeNumber = inSeries ? i + 1 : null;

                Book book = createBook(
                        author,
                        inSeries ? series : null,
                        volumeNumber,
                        titles.get(i),
                        buildSummary(titles.get(i), seed.primaryGenre(), inSeries, seed.seriesName()),
                        LocalDate.of(2024, 2, 1).plusDays(globalBookIndex * 9L),
                        seed.genres(),
                        buildTags(titles.get(i), seed.primaryGenre(), seed.seriesName())
                );

                allBooks.add(book);
                globalBookIndex++;
            }
        }

        bookRepository.saveAll(allBooks);

        // ----- READERS -----
        List<User> readers = List.of(
                createUser("Mia Reader", "miareader@bookfinder.demo", "Mia", "Reader",
                        "Always looking for the next fantasy or romance obsession.", userRole),
                createUser("Noah Reviews", "noahreviews@bookfinder.demo", "Noah", "Reviews",
                        "Leaves detailed reviews on almost every book he reads.", userRole),
                createUser("Camila Pages", "camilapages@bookfinder.demo", "Camila", "Pages",
                        "Likes sci-fi, thrillers, and late-night reading marathons.", userRole),
                createUser("Ethan Hart", "ethanhart@bookfinder.demo", "Ethan", "Hart",
                        "Collects favorite quotes and follows top-rated books.", userRole),
                createUser("Sofia Turner", "sofiaturner@bookfinder.demo", "Sofia", "Turner",
                        "Reads contemporary fiction, drama, and romance.", userRole),
                createUser("Lucas Dale", "lucasdale@bookfinder.demo", "Lucas", "Dale",
                        "Enjoys crime, mystery, and suspense series.", userRole),
                createUser("Ariana Writes", "arianawrites@bookfinder.demo", "Ariana", "Writes",
                        "Supports indie authors and reads across multiple genres.", userRole),
                createUser("Gabriel North", "gabrielnorth@bookfinder.demo", "Gabriel", "North",
                        "Mostly reads dark fantasy, action, and dystopian stories.", userRole)
        );

        userRepository.saveAll(readers);

        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(authors);
        allUsers.addAll(readers);

        // ----- REVIEWS -----
        Random random = new Random(42);

        for (Book book : allBooks) {
            List<User> reviewers = new ArrayList<>(allUsers);
            reviewers.removeIf(user -> user.getUserId().equals(book.getUser().getUserId()));
            Collections.shuffle(reviewers, random);

            int reviewCount = random.nextInt(6); // 0 to 5 reviews

            for (int i = 0; i < Math.min(reviewCount, reviewers.size()); i++) {
                User reviewer = reviewers.get(i);
                int rating = weightedRating(random);

                saveReview(
                        reviewer,
                        book,
                        rating,
                        buildReviewComment(book.getTitle(), rating, i)
                );
            }
        }

        log.info("Large demo data seeded successfully.");
        log.info("Authors: {}", authors.size());
        log.info("Readers: {}", readers.size());
        log.info("Series: {}", allSeries.size());
        log.info("Books: {}", allBooks.size());
    }

    private List<AuthorSeed> buildAuthorSeeds() {
        return List.of(
                new AuthorSeed(
                        "Luna", "Mercer",
                        "Fantasy author who writes about ruined kingdoms, ancient magic, and impossible oaths.",
                        "Chronicles of Ember",
                        "A dark fantasy saga about crowns, war, and the cost of power.",
                        "Fantasy",
                        List.of("Fantasy", "Dark Fantasy", "Adventure"),
                        List.of(
                                "Ashes of the First King",
                                "Crown of Cinders",
                                "The Ember Throne",
                                "A Forest of Glass",
                                "The Last Oathkeeper"
                        )
                ),
                new AuthorSeed(
                        "Orion", "Vale",
                        "Sci-fi writer focused on surveillance cities, rogue signals, and collapsing systems.",
                        "Neon City Files",
                        "A cyberpunk thriller series set inside a city built on lies.",
                        "Science Fiction",
                        List.of("Science Fiction", "Cyberpunk", "Thriller"),
                        List.of(
                                "Signal in the Static",
                                "Midnight Circuit",
                                "Ghost Protocol Nine",
                                "Glassline District",
                                "The Last Firewall"
                        )
                ),
                new AuthorSeed(
                        "Ivy", "Marlow",
                        "Writes emotional contemporary stories with romance, family tension, and sharp dialogue.",
                        "",
                        "",
                        "Contemporary Fiction",
                        List.of("Contemporary Fiction", "Drama", "Romance"),
                        List.of(
                                "The Last Lighthouse",
                                "Velvet & Vows",
                                "Summer After Snow",
                                "Rooms We Left Behind",
                                "The Weight of Almost"
                        )
                ),
                new AuthorSeed(
                        "Elias", "Thorne",
                        "Mystery and suspense author with a taste for missing persons cases and hidden records.",
                        "Black Harbor Cases",
                        "A mystery series full of cold cases, false leads, and buried truths.",
                        "Mystery",
                        List.of("Mystery", "Crime", "Suspense"),
                        List.of(
                                "The Harbor File",
                                "Whispers Under Mason Street",
                                "Dead Letter Room",
                                "The Eighth Witness",
                                "A Trace in Winter"
                        )
                ),
                new AuthorSeed(
                        "Nora", "Bennett",
                        "Young adult and dystopian author who likes rebellion, secrets, and found-family dynamics.",
                        "After the Divide",
                        "A dystopian series about survival after society fractures into isolated sectors.",
                        "Dystopian",
                        List.of("Dystopian", "Young Adult", "Action"),
                        List.of(
                                "After the Divide",
                                "City of Broken Signals",
                                "The Rebel Archive",
                                "Dustline Run",
                                "When the Grid Fell"
                        )
                ),
                new AuthorSeed(
                        "Jasper", "Cross",
                        "Thriller writer with fast pacing, unreliable narrators, and dangerous secrets.",
                        "",
                        "",
                        "Thriller",
                        List.of("Thriller", "Suspense", "Crime"),
                        List.of(
                                "Seven Minutes to Midnight",
                                "The Locked Floor",
                                "A Name in Red Ink",
                                "Behind the Safe Door",
                                "The Silence Ledger"
                        )
                ),
                new AuthorSeed(
                        "Selene", "Hart",
                        "Romance author who writes second chances, elegant chaos, and emotionally messy characters.",
                        "Rosewood Hearts",
                        "A romance series built around weddings, betrayals, and second chances.",
                        "Romance",
                        List.of("Romance", "Contemporary Fiction", "Drama"),
                        List.of(
                                "Rosewood Promises",
                                "Gold Silk Evenings",
                                "A Kiss at Bellview Hall",
                                "The Language of Maybe",
                                "Paper Rings and Rain"
                        )
                ),
                new AuthorSeed(
                        "Adrian", "Frost",
                        "Horror and supernatural writer obsessed with empty towns, cursed places, and wrong turns.",
                        "",
                        "",
                        "Horror",
                        List.of("Horror", "Supernatural", "Thriller"),
                        List.of(
                                "The Hollow Parish",
                                "When the Walls Breathed",
                                "Blackwater House",
                                "No One Leaves Briar Hill",
                                "Midnight in Hollow Creek"
                        )
                ),
                new AuthorSeed(
                        "Clara", "Winslow",
                        "Historical fiction author drawn to letters, war-time secrets, and quiet emotional stakes.",
                        "",
                        "",
                        "Historical Fiction",
                        List.of("Historical Fiction", "Drama", "Literary Fiction"),
                        List.of(
                                "The Cartographer's Daughter",
                                "Letters from Alder Bay",
                                "When April Burned",
                                "The Orchard Between Us",
                                "A Winter in Marseille"
                        )
                ),
                new AuthorSeed(
                        "Theo", "Rowan",
                        "Adventure and urban fantasy writer mixing modern cities with hidden magical systems.",
                        "Veilbound",
                        "An urban fantasy series where old magic survives under modern skylines.",
                        "Urban Fantasy",
                        List.of("Urban Fantasy", "Adventure", "Action"),
                        List.of(
                                "Veilbound",
                                "City of Sleeping Sigils",
                                "The Iron Lantern",
                                "Stormglass Alley",
                                "The Atlas of Hidden Doors"
                        )
                )
        );
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
                            Integer volumeNumber,
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
        book.setVolumeNumber(volumeNumber);

        author.addBook(book);

        if (series != null) {
            series.addBook(book);
        }

        book.replaceGenres(resolveGenres(genreNames));
        book.replaceHashtags(resolveHashtags(hashtags));

        return book;
    }

    private void saveReview(User reviewer, Book book, int rating, String comment) {
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
                .map(tag -> tag.replaceAll("[^a-z0-9]+", ""))
                .filter(tag -> !tag.isBlank())
                .map(tag -> hashtagRepository.findByHashtag(tag)
                        .orElseGet(() -> hashtagRepository.save(new Hashtag(tag))))
                .collect(Collectors.toSet());
    }

    private String buildUsername(String firstName, String lastName) {
        return (firstName.trim() + " " + lastName.trim())
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String buildEmail(String firstName, String lastName) {
        String safe = (firstName + lastName)
                .replaceAll("[^A-Za-z0-9]", "")
                .toLowerCase();
        return safe + "@bookfinder.demo";
    }

    private String buildSummary(String title, String primaryGenre, boolean inSeries, String seriesName) {
        if (inSeries && seriesName != null && !seriesName.isBlank()) {
            return title + " is part of " + seriesName + ", a " + primaryGenre.toLowerCase() +
                    " story filled with rising tension, layered characters, and a conflict that keeps expanding with every chapter.";
        }

        return title + " is a " + primaryGenre.toLowerCase() +
                " novel that follows a character pushed into difficult choices, hidden truths, and a situation that keeps getting harder to escape.";
    }

    private List<String> buildTags(String title, String primaryGenre, String seriesName) {
        List<String> tags = new ArrayList<>();

        tags.add(primaryGenre.toLowerCase().replaceAll("[^a-z0-9]+", ""));
        if (seriesName != null && !seriesName.isBlank()) {
            tags.add(seriesName.toLowerCase().replaceAll("[^a-z0-9]+", ""));
        }

        String[] words = title.split("\\s+");
        for (String word : words) {
            String cleaned = word.toLowerCase().replaceAll("[^a-z0-9]+", "");
            if (cleaned.length() >= 4) {
                tags.add(cleaned);
            }
            if (tags.size() >= 4) {
                break;
            }
        }

        return tags.stream().distinct().toList();
    }

    private int weightedRating(Random random) {
        int roll = random.nextInt(100);

        if (roll < 10) return 2;
        if (roll < 30) return 3;
        if (roll < 65) return 4;
        return 5;
    }

    private String buildReviewComment(String title, int rating, int variant) {
        List<String> positive = List.of(
                "Really enjoyed the pacing and atmosphere.",
                "This one was easy to get into and hard to put down.",
                "Strong concept and very readable from the start.",
                "The characters and tone worked very well for me.",
                "A very solid book with a strong hook."
        );

        List<String> mixed = List.of(
                "Interesting idea, though I wanted a bit more depth in some parts.",
                "Good overall, but some sections felt slower than others.",
                "I liked the premise and would still recommend it.",
                "Not perfect, but definitely engaging and worth reading."
        );

        if (rating >= 4) {
            return title + ": " + positive.get(variant % positive.size());
        }

        return title + ": " + mixed.get(variant % mixed.size());
    }

    private record AuthorSeed(
            String firstName,
            String lastName,
            String bio,
            String seriesName,
            String seriesDescription,
            String primaryGenre,
            List<String> genres,
            List<String> titles
    ) {}
}