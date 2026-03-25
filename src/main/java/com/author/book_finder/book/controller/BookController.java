package com.author.book_finder.book.controller;

import com.author.book_finder.book.dto.*;
import com.author.book_finder.book.service.BookService;
import com.author.book_finder.enums.FileType;
import com.author.book_finder.search.dto.SearchRequestDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;

import java.util.List;


@RestController
@RequestMapping("/api")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // CREATE BOOK
    @PostMapping("/books/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookResponseDTO> createBook(
           @Valid @RequestBody BookCreateRequestDTO dto
    ) {
        return ResponseEntity.ok(bookService.createBook(dto));
    }

    // GENERATE BOOK COVER UPLOAD URL
    @PostMapping("/books/{bookId}/cover/upload-url")
    public ResponseEntity<PresignedUploadResponseDTO> generateCoverUploadUrl(
            @PathVariable Long bookId,
            @RequestParam String filename,
            @RequestParam FileType fileType) {

        PresignedUploadResponseDTO responseDTO = bookService.generateCoverUploadUrl(bookId, filename, fileType);
        return ResponseEntity.ok(responseDTO);
    }

    // GENERATE BOOK COVER DOWNLOAD URL
    @GetMapping("/books/{bookId}/cover")
    public ResponseEntity<String> getCover(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookService.getCoverUrl(bookId));
    }

    // GET ALL BOOKS PUBLIC
    @GetMapping("/books/list")
    public ResponseEntity<Page<BookResponseDTO>> getAllBooks(Pageable pageable) {
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }

    // GET BOOK DETAILS PUBLIC
    @GetMapping("/books/{id}/details")
    public ResponseEntity<BookDetailsDTO> getBookDetails(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookDetails(id));
    }

    // UPDATE BOOK
    @PutMapping("/books/{id}/update")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BookResponseDTO> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(bookService.updateBook(id, dto));
    }

    // DELETE BOOK
    @DeleteMapping("/books/{id}/delete")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {

        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();

    }

    // SEARCH FEATURE
    @PostMapping("/books/search")
    public Page<BookResponseDTO> searchBooks(
            @RequestBody SearchRequestDTO request) {

        return bookService.searchBooks(request);
    }

    @PutMapping("/books/{id}/publish")
    public BookResponseDTO publishBook(@PathVariable Long id, Authentication authentication) {
        return bookService.publishBook(id, authentication);
    }

    @PutMapping("/books/{id}/unpublish")
    public BookResponseDTO unpublishBook(@PathVariable Long id, Authentication authentication) {
        return bookService.unpublishBook(id, authentication);
    }

    @GetMapping("/books/me/published")
    public List<BookResponseDTO> getMyPublishedBooks(Authentication authentication) {
        return bookService.getMyPublishedBooks(authentication);
    }

    @GetMapping("/books/me/standalone")
    public List<BookResponseDTO> getMyStandaloneBooks(Authentication authentication) {
        return bookService.getMyStandaloneBooks(authentication);
    }

    @PutMapping("/books/{id}/assign-series/{seriesId}")
    public BookResponseDTO assignBookToSeries(
            @PathVariable Long id,
            @PathVariable Long seriesId,
            Authentication authentication) {
        return bookService.assignBookToSeries(id, seriesId, authentication);
    }

    @PutMapping("/books/{id}/remove-series")
    public BookResponseDTO removeBookFromSeries(
            @PathVariable Long id,
            Authentication authentication) {
        return bookService.removeBookFromSeries(id, authentication);
    }
}