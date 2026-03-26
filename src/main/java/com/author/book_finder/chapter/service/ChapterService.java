package com.author.book_finder.chapter.service;

import com.author.book_finder.book.entity.Book;
import com.author.book_finder.book.repository.BookRepository;
import com.author.book_finder.chapter.dto.*;
import com.author.book_finder.chapter.entity.Chapter;
import com.author.book_finder.chapter.repository.ChapterRepository;
import com.author.book_finder.enums.FileType;
import com.author.book_finder.enums.PublicationStatus;
import com.author.book_finder.infrastructure.aws.S3Service;
import com.author.book_finder.security.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ChapterService {

    private final ChapterRepository chapterRepo;
    private final BookRepository bookRepo;
    private final S3Service s3Service;
    private final SecurityUtil securityUtil;

    public ChapterService(ChapterRepository chapterRepo,
                          BookRepository bookRepo,
                          S3Service s3Service,
                          SecurityUtil securityUtil) {
        this.chapterRepo = chapterRepo;
        this.bookRepo = bookRepo;
        this.s3Service = s3Service;
        this.securityUtil = securityUtil;
    }

    // -----------------------------
    // LEGACY MANUAL UPLOAD FLOW
    // -----------------------------
    public PresignedUploadResponseDTO generateUploadUrl(
            Long bookId,
            String filename,
            FileType fileType) {

        Book book = bookRepo.findById(bookId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        validateOwnership(book);

        if (filename == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filename is required");
        }

        String lowerFilename = filename.toLowerCase();

        if (lowerFilename.endsWith(".md")) {
            if (fileType != FileType.MARKDOWN) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Markdown files must use MARKDOWN content type");
            }
        } else if (lowerFilename.endsWith(".html")) {
            if (fileType != FileType.HTML) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "HTML files must use HTML content type");
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only .md or .html files are supported");
        }

        String objectKey =
                "books/" + bookId +
                        "/chapters/" +
                        UUID.randomUUID() + "_" + filename;

        String uploadUrl =
                s3Service.generatePresignedUploadUrl(objectKey, fileType, 15);

        return new PresignedUploadResponseDTO(objectKey, uploadUrl);
    }

    public ChapterUploadResponseDTO confirmUpload(
            Long bookId,
            ChapterConfirmUploadDTO request) {

        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Book not found"));

        validateOwnership(book);

        validatePositiveChapterNumber(request.getChapterNumber());
        validateEditorFileType(request.getFileType());

        if (chapterRepo.existsByBookBookIdAndChapterNumber(bookId, request.getChapterNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chapter number already exists for this book");
        }

        if (request.getObjectKey() == null || request.getObjectKey().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Object key is required"
            );
        }

        String expectedPrefix = "books/" + bookId + "/chapters/";

        if (!request.getObjectKey().startsWith(expectedPrefix)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid object key for this book"
            );
        }

        if (!s3Service.objectExists(request.getObjectKey())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Object does not exist");
        }

        Chapter chapter = new Chapter();
        chapter.setS3Key(request.getObjectKey());
        chapter.setTitle(resolveChapterTitle(request.getTitle(), request.getChapterNumber()));
        chapter.setChapterNumber(request.getChapterNumber());
        chapter.setFileType(request.getFileType());

        book.addChapter(chapter);

        chapterRepo.save(chapter);

        String downloadUrl = s3Service.generatePresignedUrl(request.getObjectKey(), 60);

        return new ChapterUploadResponseDTO(chapter.getChapterId(), downloadUrl);
    }

    // -----------------------------
    // WRITER / EDITOR FLOW
    // -----------------------------
    @Transactional(readOnly = true)
    public List<ChapterManageListDTO> listChaptersForManagement(Long bookId) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        validateOwnership(book);

        return chapterRepo.findByBookBookIdOrderByChapterNumberAsc(bookId).stream()
                .map(this::mapToManageListDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChapterEditorDTO getChapterForEditor(Long chapterId) {
        Chapter chapter = chapterRepo.findById(chapterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapter not found"));

        validateOwnership(chapter.getBook());

        String content = s3Service.getObjectAsString(chapter.getS3Key());

        return mapToEditorDTO(chapter, content);
    }

    public ChapterEditorDTO createChapterFromEditor(Long bookId, ChapterEditorCreateDTO request) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        validateOwnership(book);

        validatePositiveChapterNumber(request.getChapterNumber());
        validateEditorFileType(request.getFileType());

        if (chapterRepo.existsByBookBookIdAndChapterNumber(bookId, request.getChapterNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chapter number already exists for this book");
        }

        String normalizedContent = normalizeContent(request.getContent());

        Chapter chapter = new Chapter();
        chapter.setChapterNumber(request.getChapterNumber());
        chapter.setTitle(resolveChapterTitle(request.getTitle(), request.getChapterNumber()));
        chapter.setFileType(request.getFileType());

        // Temporary non-null key so the entity can be inserted and receive an ID
        chapter.setS3Key("books/" + bookId + "/chapters/pending_" + UUID.randomUUID());

        book.addChapter(chapter);

        Chapter savedChapter = chapterRepo.save(chapter);

        String finalKey = buildEditorObjectKey(bookId, savedChapter.getChapterId(), savedChapter.getFileType());
        s3Service.putTextObject(finalKey, normalizedContent, savedChapter.getFileType());

        savedChapter.setS3Key(finalKey);

        return mapToEditorDTO(savedChapter, normalizedContent);
    }

    public ChapterEditorDTO saveChapterFromEditor(Long chapterId, ChapterEditorSaveDTO request) {
        Chapter chapter = chapterRepo.findById(chapterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapter not found"));

        Book book = chapter.getBook();
        validateOwnership(book);

        validatePositiveChapterNumber(request.getChapterNumber());
        validateEditorFileType(request.getFileType());

        if (chapterRepo.existsByBookBookIdAndChapterNumberAndChapterIdNot(
                book.getBookId(),
                request.getChapterNumber(),
                chapterId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chapter number already exists for this book");
        }

        String oldKey = chapter.getS3Key();
        String normalizedContent = normalizeContent(request.getContent());

        chapter.setChapterNumber(request.getChapterNumber());
        chapter.setTitle(resolveChapterTitle(request.getTitle(), request.getChapterNumber()));
        chapter.setFileType(request.getFileType());

        String targetKey = buildEditorObjectKey(book.getBookId(), chapter.getChapterId(), chapter.getFileType());

        s3Service.putTextObject(targetKey, normalizedContent, chapter.getFileType());

        if (!targetKey.equals(oldKey) && oldKey != null && !oldKey.isBlank() && s3Service.objectExists(oldKey)) {
            s3Service.deleteObject(oldKey);
        }

        chapter.setS3Key(targetKey);

        return mapToEditorDTO(chapter, normalizedContent);
    }

    // -----------------------------
    // EXISTING PUBLIC / SIMPLE FLOW
    // -----------------------------
    public ChapterResponseDTO updateChapter(Long chapterId, ChapterUpdateDTO request) {

        Chapter chapter = chapterRepo.findById(chapterId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Chapter not found"));

        validateOwnership(chapter.getBook());

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            chapter.setTitle(request.getTitle());
        }

        return mapToResponseDTO(chapter, null, null);
    }

    public void deleteChapter(Long chapterId) {

        Chapter chapter = chapterRepo.findById(chapterId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Chapter not found"));

        validateOwnership(chapter.getBook());

        if (chapter.getS3Key() != null && !chapter.getS3Key().isBlank()) {
            s3Service.deleteObject(chapter.getS3Key());
        }

        chapterRepo.delete(chapter);
    }

    public Page<ChapterResponseDTO> listChaptersForBook(Long bookId, Pageable pageable) {

        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        ensurePublished(book);

        Page<Chapter> chapterPage =
                chapterRepo.findByBookBookIdOrderByChapterNumberAsc(bookId, pageable);

        return chapterPage.map(ch -> mapToResponseDTO(ch, null, null));
    }

    public ChapterResponseDTO getPreviewUrl(Long id) {
        Chapter ch = chapterRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapter not found"));

        ensurePublished(ch.getBook());

        if (!ch.isPreview()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Chapter not available as preview");
        }

        String previewUrl = s3Service.generatePresignedUrl(ch.getS3Key(), 15);

        return mapToResponseDTO(ch, previewUrl, null);
    }

    public ChapterResponseDTO getFullUrl(Long id) {
        Chapter ch = chapterRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapter not found"));

        ensurePublishedOrOwner(ch.getBook());

        String fullUrl = s3Service.generatePresignedUrl(ch.getS3Key(), 60);

        return mapToResponseDTO(ch, null, fullUrl);
    }

    // -----------------------------
    // HELPERS
    // -----------------------------
    private void validateOwnership(Book book) {
        Long currentUserId = securityUtil.getCurrentUserId();
        boolean isAdmin = securityUtil.isAdmin();

        if (!book.getUser().getUserId().equals(currentUserId) && !isAdmin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to perform this action");
        }
    }

    private void validatePositiveChapterNumber(int chapterNumber) {
        if (chapterNumber <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chapter number must be positive");
        }
    }

    private void validateEditorFileType(FileType fileType) {
        if (fileType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File type is required");
        }

        if (fileType != FileType.MARKDOWN && fileType != FileType.HTML) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only MARKDOWN or HTML are allowed for chapters");
        }
    }

    private String normalizeContent(String content) {
        return content == null ? "" : content;
    }

    private String resolveChapterTitle(String title, int chapterNumber) {
        if (title != null && !title.isBlank()) {
            return title.trim();
        }

        return "Chapter " + chapterNumber;
    }

    private String buildEditorObjectKey(Long bookId, Long chapterId, FileType fileType) {
        return "books/" + bookId + "/chapters/chapter_" + chapterId + getExtensionForEditorFileType(fileType);
    }

    private String getExtensionForEditorFileType(FileType fileType) {
        return fileType == FileType.HTML ? ".html" : ".md";
    }

    private ChapterManageListDTO mapToManageListDTO(Chapter chapter) {
        return new ChapterManageListDTO(
                chapter.getChapterId(),
                chapter.getChapterNumber(),
                chapter.getTitle(),
                chapter.isPreview(),
                chapter.getFileType()
        );
    }

    private ChapterEditorDTO mapToEditorDTO(Chapter chapter, String content) {
        return new ChapterEditorDTO(
                chapter.getChapterId(),
                chapter.getBook().getBookId(),
                chapter.getChapterNumber(),
                chapter.getTitle(),
                chapter.getFileType(),
                content
        );
    }

    private ChapterResponseDTO mapToResponseDTO(
            Chapter chapter,
            String previewUrl,
            String fullUrl) {

        return new ChapterResponseDTO(
                chapter.getChapterId(),
                chapter.getChapterNumber(),
                chapter.getTitle(),
                chapter.isPreview(),
                chapter.getFileType(),
                previewUrl,
                fullUrl
        );
    }

    private void ensurePublished(Book book) {
        if (book.getPublicationStatus() != PublicationStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }
    }

    private void ensurePublishedOrOwner(Book book) {
        if (book.getPublicationStatus() == PublicationStatus.PUBLISHED) {
            return;
        }

        Long currentUserId = securityUtil.getCurrentUserId();
        boolean isAdmin = securityUtil.isAdmin();

        if (!book.getUser().getUserId().equals(currentUserId) && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }
    }
}
