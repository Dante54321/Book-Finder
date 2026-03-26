package com.author.book_finder.chapter.controller;

import com.author.book_finder.chapter.dto.*;
import com.author.book_finder.enums.FileType;
import com.author.book_finder.chapter.service.ChapterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ChapterController {

    private final ChapterService chapterService;

    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    // GET PREVIEW CHAPTER URL
    @GetMapping("/chapters/{id}/preview")
    public ResponseEntity<ChapterResponseDTO> getPreviewUrl(@PathVariable Long id) {
        return ResponseEntity.ok(chapterService.getPreviewUrl(id));
    }

    // GET URL FOR FULL CHAPTER
    @GetMapping("/chapters/{id}")
    public ResponseEntity<ChapterResponseDTO> getFullUrl(@PathVariable Long id) {
        return ResponseEntity.ok(chapterService.getFullUrl(id));
    }

    // LIST CHAPTERS FOR BOOK PUBLIC
    @GetMapping("/books/{bookId}/chapters/list")
    public ResponseEntity<Page<ChapterResponseDTO>> listChapters(
            @PathVariable Long bookId,
            Pageable pageable) {

        return ResponseEntity.ok(chapterService.listChaptersForBook(bookId, pageable));
    }

    // LIST CHAPTERS FOR BOOK OWNER/ADMIN (WRITER SIDEBAR)
    @GetMapping("/books/{bookId}/chapters/manage")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ChapterManageListDTO>> listChaptersForManagement(@PathVariable Long bookId) {
        return ResponseEntity.ok(chapterService.listChaptersForManagement(bookId));
    }

    // GET CHAPTER CONTENT FOR EDITOR
    @GetMapping("/chapters/{id}/editor")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ChapterEditorDTO> getChapterForEditor(@PathVariable Long id) {
        return ResponseEntity.ok(chapterService.getChapterForEditor(id));
    }

    // CREATE CHAPTER DIRECTLY FROM EDITOR
    @PostMapping("/books/{bookId}/chapters/editor-create")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ChapterEditorDTO> createChapterFromEditor(
            @PathVariable Long bookId,
            @RequestBody ChapterEditorCreateDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chapterService.createChapterFromEditor(bookId, request));
    }

    // SAVE CHAPTER DIRECTLY FROM EDITOR
    @PutMapping("/chapters/{id}/editor-save")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ChapterEditorDTO> saveChapterFromEditor(
            @PathVariable Long id,
            @RequestBody ChapterEditorSaveDTO request) {

        return ResponseEntity.ok(chapterService.saveChapterFromEditor(id, request));
    }

    // UPDATE CHAPTER METADATA (legacy/simple endpoint)
    @PatchMapping("/chapters/{id}/update")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ChapterResponseDTO> updateChapter(
            @PathVariable Long id,
            @RequestBody ChapterUpdateDTO request) {

        return ResponseEntity.ok(chapterService.updateChapter(id, request));
    }

    // DELETE CHAPTER
    @DeleteMapping("/chapters/{id}/delete")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> deleteChapter(@PathVariable Long id) {
        chapterService.deleteChapter(id);
        return ResponseEntity.noContent().build();
    }

    // GENERATE UPLOAD URL (legacy/manual upload flow)
    @PostMapping("/books/{bookId}/chapters/upload-url")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PresignedUploadResponseDTO> generateUploadUrl(
            @PathVariable Long bookId,
            @RequestParam String filename,
            @RequestParam FileType fileType) {

        PresignedUploadResponseDTO responseDTO =
                chapterService.generateUploadUrl(bookId, filename, fileType);

        return ResponseEntity.ok(responseDTO);
    }

    // CONFIRM UPLOAD & SAVE CHAPTER METADATA (legacy/manual upload flow)
    @PostMapping("/books/{bookId}/chapters/confirm")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ChapterUploadResponseDTO> confirmUpload(
            @PathVariable Long bookId,
            @RequestBody ChapterConfirmUploadDTO requestDTO) {

        ChapterUploadResponseDTO responseDTO =
                chapterService.confirmUpload(bookId, requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
}