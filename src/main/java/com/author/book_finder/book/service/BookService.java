package com.author.book_finder.book.service;

import com.author.book_finder.book.dto.BookUpdateRequestDTO;
import com.author.book_finder.book.exception.BookAccessDeniedException;
import com.author.book_finder.book.exception.BookNotFoundException;
import com.author.book_finder.book.mapper.BookMapper;
import com.author.book_finder.book.repository.BookRepository;
import com.author.book_finder.book.dto.BookCreateRequestDTO;
import com.author.book_finder.book.dto.BookDetailsDTO;
import com.author.book_finder.book.dto.BookResponseDTO;
import com.author.book_finder.entity.*;
import com.author.book_finder.book.entity.Book;
import com.author.book_finder.repository.*;
import com.author.book_finder.security.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final SeriesRepository seriesRepository;
    private final GenreRepository genreRepository;
    private final HashtagRepository hashtagRepository;
    private final SecurityUtil securityUtil;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository,
                       UserRepository userRepository,
                       SeriesRepository seriesRepository,
                       GenreRepository genreRepository,
                       HashtagRepository hashtagRepository,
                       SecurityUtil securityUtil,
                       BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.seriesRepository = seriesRepository;
        this.genreRepository = genreRepository;
        this.hashtagRepository = hashtagRepository;
        this.securityUtil = securityUtil;
        this.bookMapper = bookMapper;
    }


    // CREATE BOOK
    public BookResponseDTO createBook(BookCreateRequestDTO dto) {

        Long userId = securityUtil.getCurrentUserId();

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setSummary(dto.getSummary());
        book.setPublishDate(dto.getPublishDate());
        book.setUser(author);

        attachSeries(book, dto.getSeriesId(), null);
        attachGenres(book, dto.getGenreIds());
        attachHashtags(book, dto.getHashtags());   // ← changed

        Book saved = bookRepository.save(book);

        return bookMapper.toResponseDTO(saved);
    }

    // GET ALL (PAGINATED)
    public Page<BookResponseDTO> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toResponseDTO);
    }


    // GET DETAILS
    public BookDetailsDTO getBookDetails(Long bookId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new BookNotFoundException(bookId));

        return bookMapper.toDetailsDTO(book);
    }


    // UPDATE BOOK
    public BookResponseDTO updateBook(Long id, BookUpdateRequestDTO dto) {

        Long userId = securityUtil.getCurrentUserId();

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        if (!book.getUser().getUserId().equals(userId)) {
            throw new BookAccessDeniedException();
        }

        // Partial updates
        if (dto.getTitle() != null) {
            book.setTitle(dto.getTitle());
        }

        if (dto.getSummary() != null) {
            book.setSummary(dto.getSummary());
        }

        if (dto.getPublishDate() != null) {
            book.setPublishDate(dto.getPublishDate());
        }

        if (dto.getSeriesId() != null) {
            attachSeries(book, dto.getSeriesId(), null);
        }

        if (dto.getGenreIds() != null) {
            if (dto.getGenreIds().isEmpty()) {
                throw new IllegalArgumentException("Book must have at least one genre");
            }
            attachGenres(book, dto.getGenreIds());
        }

        if (dto.getHashtags() != null) {
            attachHashtags(book, dto.getHashtags());
        }

        Book updated = bookRepository.save(book);

        return bookMapper.toResponseDTO(updated);
    }


    // DELETE BOOK
    public void deleteBook(Long bookId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "At least one genre is required");
        }

        Set<Genre> genres = genreIds.stream()
                .map(id -> genreRepository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Genre not found with id: " + id)))
                .collect(Collectors.toSet());

        book.setGenres(genres);
    }

    private void attachHashtags(Book book, Set<String> hashtags) {

        if (hashtags == null) {
            return;
        }

        Set<String> normalized = hashtags.stream()
                .filter(Objects::nonNull)
                .map(tag -> tag.trim().toLowerCase())
                .filter(tag -> !tag.isBlank())
                .collect(Collectors.toSet());

        Set<Hashtag> resolved = new HashSet<>();

        for (String name : normalized) {
            Hashtag hashtag = hashtagRepository
                    .findByHashtag(name)
                    .orElseGet(() -> hashtagRepository.save(new Hashtag(name)));

            resolved.add(hashtag);
        }

        book.setHashtags(resolved);
    }

    // VALIDATE OWNERSHIP (ADMIN BYPASS)
    private void validateOwnership(Book book) {

        Long currentUserId = securityUtil.getCurrentUserId();
        boolean isAdmin = securityUtil.isAdmin();

        if (!book.getUser().getUserId().equals(currentUserId) && !isAdmin) {
            throw new BookAccessDeniedException();
        }
    }

}