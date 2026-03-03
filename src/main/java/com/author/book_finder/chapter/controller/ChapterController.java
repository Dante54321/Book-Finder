package com.author.book_finder.chapter.controller;

import com.author.book_finder.chapter.dto.ChapterResponseDTO;
import com.author.book_finder.chapter.dto.ChapterUpdateDTO;
import com.author.book_finder.chapter.service.ChapterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    private final ChapterService chapterService;

    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<ChapterResponseDTO> getPreviewUrl(@PathVariable Long id) {
        return ResponseEntity.ok(chapterService.getPreviewUrl(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChapterResponseDTO> getFullUrl(@PathVariable Long id) {
        return ResponseEntity.ok(chapterService.getFullUrl(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ChapterResponseDTO> updateChapter(
            @PathVariable Long id,
            @RequestBody ChapterUpdateDTO request) {

        return ResponseEntity.ok(chapterService.updateChapter(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> deleteChapter(@PathVariable Long id) {
        chapterService.deleteChapter(id);
        return ResponseEntity.noContent().build();
    }
}