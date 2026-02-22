package com.author.book_finder.controller;

import com.author.book_finder.dto.ChapterResponseDTO;
import com.author.book_finder.dto.ChapterUploadRequestDTO;
import com.author.book_finder.dto.ChapterUploadResponseDTO;
import com.author.book_finder.service.ChapterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    private final ChapterService chapterService;

    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ChapterUploadResponseDTO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("bookId") Long bookId,
            @RequestParam("title") String title,
            @RequestParam("chapterNumber") int chapterNumber,
            @RequestParam("isPreview") boolean isPreview) throws IOException {

        ChapterUploadRequestDTO request = new ChapterUploadRequestDTO();
        request.setFile(file);
        request.setBookId(bookId);
        request.setTitle(title);
        request.setChapterNumber(chapterNumber);
        request.setIsPreview(isPreview);

        ChapterUploadResponseDTO response = chapterService.uploadChapter(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ChapterResponseDTO>> listChapters(@PathVariable Long bookId) {
        return ResponseEntity.ok(chapterService.listChaptersForBook(bookId));
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<ChapterResponseDTO> getPreviewUrl(@PathVariable Long id) {
        return ResponseEntity.ok(chapterService.getPreviewUrl(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChapterResponseDTO> getFullUrl(@PathVariable Long id) {
        return ResponseEntity.ok(chapterService.getFullUrl(id));
    }
}