package com.author.book_finder.service;

import com.author.book_finder.dto.BookCreateRequestDTO;
import com.author.book_finder.dto.BookDetailsDTO;
import com.author.book_finder.dto.BookResponseDTO;
import com.author.book_finder.entity.*;
import com.author.book_finder.entity.Book;
import com.author.book_finder.repository.*;
import com.author.book_finder.security.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final SeriesRepository seriesRepository;
    private final GenreRepository genreRepository;
    private final HashtagRepository hashtagRepository;
    private final SecurityUtil securityUtil;

    public BookService(BookRepository bookRepository,
                       UserRepository userRepository,
                       SeriesRepository seriesRepository,
                       GenreRepository genreRepository,
                       HashtagRepository hashtagRepository,
                       SecurityUtil securityUtil) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.seriesRepository = seriesRepository;
        this.genreRepository = genreRepository;
        this.hashtagRepository = hashtagRepository;
        this.securityUtil = securityUtil;
    }


    // CREATE BOOK
    public BookResponseDTO createBook(BookCreateRequestDTO dto) {

        Long userId = securityUtil.getCurrentUserId();

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setSummary(dto.getSummary());
        book.setPublishDate(dto.getPublishDate());
        book.setUser(author);

        attachSeries(book, dto.getSeriesId(),null);
        attachGenres(book, dto.getGenreIds());
        attachHashtags(book, dto.getHashtagIds());

        Book saved = bookRepository.save(book);

        return mapToResponseDTO(saved);
    }

    // GET ALL (PAGINATED)
    public Page<BookResponseDTO> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(this::mapToResponseDTO);
    }


    // GET DETAILS
    public BookDetailsDTO getBookDetails(Long bookId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        return mapToDetailsDTO(book);
    }


    // UPDATE BOOK
    public BookResponseDTO updateBook(Long bookId, BookCreateRequestDTO dto) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        validateOwnership(book);

        book.setTitle(dto.getTitle());
        book.setSummary(dto.getSummary());
        book.setPublishDate(dto.getPublishDate());

        attachSeries(book, dto.getSeriesId(), bookId);
        attachGenres(book, dto.getGenreIds());

        book.getBookHashtags().clear();
        attachHashtags(book, dto.getHashtagIds());

        Book updated = bookRepository.save(book);
        return mapToResponseDTO(updated);
    }


    // DELETE BOOK
    public void deleteBook(Long bookId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        validateOwnership(book);

        bookRepository.delete(book);
    }

    // HELPER METHODS
    private void attachSeries(Book book,
                              Long seriesId,
                              Long currentBookId) {

        // Standalone
        if (seriesId == null) {
            book.setSeries(null);
            book.setVolumeNumber(null);
            return;
        }

        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Series not found")
                );

        // UPDATING SERIES w/ NO VOLUME CHANGE
        if (currentBookId != null &&
                book.getSeries() != null &&
                book.getSeries().getSeriesId().equals(seriesId)) {

            book.setSeries(series);
            return;
        }
        // AUTO ASSIGN volume
        Integer maxVolume = bookRepository.findMaxVolumeBySeriesId(seriesId);
        int nextVolume = maxVolume + 1;

        book.setSeries(series);
        book.setVolumeNumber(nextVolume);
    }

    private void attachGenres(Book book, Set<Long> genreIds) {

        if (genreIds == null || genreIds.isEmpty()) {
            book.getGenres().clear();
            return;
        }

        Set<Genre> genres = new HashSet<>(genreRepository.findAllById(genreIds));
        book.setGenres(genres);
    }

    private void attachHashtags(Book book, Set<Long> hashtagIds) {

        if (hashtagIds == null || hashtagIds.isEmpty()) {
            return;
        }

        Set<Hashtag> hashtags =
                new HashSet<>(hashtagRepository.findAllById(hashtagIds));

        for (Hashtag hashtag : hashtags) {
            book.addHashtag(hashtag);
        }
    }

    // VALIDATE OWNERSHIP (ADMIN BYPASS)
    private void validateOwnership(Book book) {

        Long currentUserId = securityUtil.getCurrentUserId();
        boolean isAdmin = securityUtil.isAdmin();

        if (!book.getUser().getUserId().equals(currentUserId) && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to perform this action");
        }
    }

    // MAPPERS
    private BookResponseDTO mapToResponseDTO(Book book) {

        return new BookResponseDTO(
                book.getBookId(),
                book.getVolumeNumber(),
                book.getTitle(),
                book.getPublishDate(),
                book.getUser().getUsername(),
                book.getSeries() != null
                        ? book.getSeries().getSeriesName()
                        : null
        );
    }

    private BookDetailsDTO mapToDetailsDTO(Book book) {
        Set<String> genreNames = book.getGenres() == null
                ? Set.of()
                : book.getGenres()
                .stream()
                .map(Genre::getGenreName)
                .collect(Collectors.toSet());

        Set<String> hashtagNames = book.getBookHashtags() == null
                ? Set.of()
                : book.getBookHashtags()
                .stream()
                .map(bh -> bh.getHashtag().getHashtag())
                .collect(Collectors.toSet());

        return new BookDetailsDTO(
                book.getBookId(),
                book.getVolumeNumber(),
                book.getTitle(),
                book.getSummary(),
                book.getPublishDate(),
                book.getUser().getUsername(),
                book.getSeries() != null
                        ? book.getSeries().getSeriesName()
                        : null,
                genreNames,
                hashtagNames
        );
    }

}