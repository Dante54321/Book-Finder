package com.author.book_finder.controller;

import com.author.book_finder.dto.ChapterResponseDTO;
import com.author.book_finder.service.ChapterService;
import org.springframework.http.ResponseEntity;
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
}