package com.author.book_finder.book.controller;

import com.author.book_finder.book.dto.BookCreateRequestDTO;
import com.author.book_finder.book.dto.BookDetailsDTO;
import com.author.book_finder.book.dto.BookResponseDTO;
import com.author.book_finder.book.dto.BookUpdateRequestDTO;
import com.author.book_finder.dto.*;
import com.author.book_finder.book.service.BookService;
import jakarta.validation.Valid;
import com.author.book_finder.service.ChapterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final ChapterService chapterService;

    public BookController(BookService bookService, ChapterService chapterService) {
        this.bookService = bookService;
        this.chapterService = chapterService;
    }

    // CREATE BOOK
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookResponseDTO> createBook(
           @Valid @RequestBody BookCreateRequestDTO dto
    ) {
        return ResponseEntity.ok(bookService.createBook(dto));
    }

    // GET ALL BOOKS PUBLIC
    @GetMapping
    public ResponseEntity<Page<BookResponseDTO>> getAllBooks(Pageable pageable) {
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }

    // GET BOOK DETAILS PUBLIC
    @GetMapping("/{id}")
    public ResponseEntity<BookDetailsDTO> getBookDetails(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookDetails(id));
    }

    // UPDATE BOOK
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BookResponseDTO> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(bookService.updateBook(id, dto));
    }

    // DELETE BOOK
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {

        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();

    }

    //GENERATE UPLOAD URL
    @PostMapping("/{bookId}/chapters/upload")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PresignedUploadResponseDTO> generateUploadUrl(
            @PathVariable Long bookId,
            @RequestParam String filename,
            @RequestParam String contentType) {

        PresignedUploadResponseDTO responseDTO =
                chapterService.generateUploadUrl(
                        bookId,
                        filename,
                        contentType
                );

        return ResponseEntity.ok(responseDTO);

    }

    // CONFIRM UPLOAD & SAVE CHAPTER METADATA
    @PostMapping("/{bookId}/chapters/confirm")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ChapterUploadResponseDTO> confirmUpload(
            @PathVariable Long bookId,
            @RequestBody ChapterConfirmUploadDTO requestDTO) {

        ChapterUploadResponseDTO responseDTO =
                chapterService.confirmUpload(
                        bookId,
                        requestDTO
                );

        return ResponseEntity.ok(responseDTO);
    }

    // LIST CHAPTERS FOR BOOK PUBLIC
    @GetMapping("/{bookId}/chapters")
    public ResponseEntity<Page<ChapterResponseDTO>> listChapters(
            @PathVariable Long bookId,
            Pageable pageable){

        return ResponseEntity.ok(chapterService.listChaptersForBook(bookId, pageable));
    }
}