package com.author.book_finder.controller;

import com.author.book_finder.entity.Chapter;
import com.author.book_finder.repository.BookRepository;
import com.author.book_finder.repository.ChapterRepository;
import com.author.book_finder.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    private final S3Service s3Service;
    private final ChapterRepository chapterRepo;
    private final BookRepository bookRepo;

    public ChapterController(S3Service s3Service,
                             ChapterRepository chapterRepo,
                             BookRepository bookRepo) {
        this.s3Service = s3Service;
        this.chapterRepo = chapterRepo;
        this.bookRepo = bookRepo;
    }

    /**
     * Upload a new chapter file for a book
     * (protected — requires JWT)
     */
    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("bookId") Long bookId,
            @RequestParam("title") String title,
            @RequestParam("chapterNumber") int chapterNumber,
            @RequestParam("isPreview") boolean isPreview) throws IOException {

        // Upload file to S3 and get the object key
        String s3Key = s3Service.uploadFile(file);

        // Create and save the chapter record
        Chapter chapter = new Chapter();
        chapter.setTitle(title);
        chapter.setChapterNumber(chapterNumber);
        chapter.setS3Key(s3Key);
        chapter.setContentType(file.getOriginalFilename().toLowerCase().endsWith(".md") ? "MARKDOWN" : "HTML");
        chapter.setIsPreview(isPreview);
        chapter.setBook(bookRepo.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found")));

        chapterRepo.save(chapter);

        return ResponseEntity.ok(Map.of("message", "Upload successful", "chapterId", chapter.getChapterId()));
    }

    // List all chapters for a given book (public)
    @GetMapping("/book/{bookId}")
    public List<Chapter> listChapters(@PathVariable Long bookId) {
        return chapterRepo.findByBookBookIdOrderByChapterNumberAsc(bookId);
    }


    // Get a preview pre-signed URL for a chapter (public)
    @GetMapping("/{id}/preview")
    public ResponseEntity<?> getPreviewUrl(@PathVariable Long id) {
        Chapter chapter = chapterRepo.findById(id).orElseThrow(() -> new RuntimeException("Chapter not found"));

        if (!chapter.isPreview()) {
            return ResponseEntity.status(403).body(Map.of("error", "Not a preview chapter"));
        }

        String url = s3Service.generatePresignedUrl(chapter.getS3Key(), 15);
        return ResponseEntity.ok(Map.of("url", url));
    }

    // Get a full pre-signed URL for a chapter (protected by JWT)
    @GetMapping("/{id}")
    public ResponseEntity<?> getFullUrl(@PathVariable Long id) {
        Chapter chapter = chapterRepo.findById(id).orElseThrow(() -> new RuntimeException("Chapter not found"));

        String url = s3Service.generatePresignedUrl(chapter.getS3Key(), 15);
        return ResponseEntity.ok(Map.of("url", url));
    }
}

