package com.author.book_finder.service;

import com.author.book_finder.dto.ChapterConfirmUploadDTO;
import com.author.book_finder.dto.ChapterResponseDTO;
import com.author.book_finder.dto.ChapterUploadResponseDTO;
import com.author.book_finder.dto.PresignedUploadResponseDTO;
import com.author.book_finder.book.entity.Book;
import com.author.book_finder.entity.Chapter;
import com.author.book_finder.book.repository.BookRepository;
import com.author.book_finder.repository.ChapterRepository;
import com.author.book_finder.security.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
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

    // UPDATED GENERATE UPLOAD URL BOOK SPECIFIC
    public PresignedUploadResponseDTO generateUploadUrl(
            Long bookId,
            String filename,
            String contentType) {

        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        validateOwnership(book);

        // Check for correct extensions/file types
        if (filename == null || !(filename.toLowerCase().endsWith(".md") || filename.toLowerCase().endsWith(".html"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only .md or .html files are supported");
        }

        // Validate contentType if sent by client
        if (contentType == null || (!contentType.equalsIgnoreCase("text/markdown")
                && !contentType.equalsIgnoreCase("text/html"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid content type, only text/markdown or text/html are supported");
        }

        // Extension and content-type consistency
        if (filename.toLowerCase().endsWith(".md") && !contentType.equalsIgnoreCase("text/markdown")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Markdown files must use text/markdown");
        }

        if (filename.toLowerCase().endsWith(".html") && !contentType.equalsIgnoreCase("text/html")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "HTML files must use text/html");
        }

        // Generate a unique key for S3
        String objectKey = UUID.randomUUID() + "_" + filename;
        String uploadUrl = s3Service.generatePresignedUploadUrl(objectKey, contentType, 15);

        return new PresignedUploadResponseDTO(objectKey, uploadUrl);
    }

    public ChapterUploadResponseDTO confirmUpload(
            Long bookId,
            ChapterConfirmUploadDTO request) {

        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Book not found"));

        // Ownership Validation
        validateOwnership(book);

        // Validate Chapter Number
        if (request.getChapterNumber() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chapter number must be positive");
        }

        if (request.getContentType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Content type required");
        }

        // Prevent duplicate chapter numbers per book
        if (chapterRepo.existsByBookBookIdAndChapterNumber(bookId, request.getChapterNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chapter number already exists for this book");
        }

        // Create and save the chapter entity
        Chapter chapter = new Chapter();
        chapter.setS3Key(request.getObjectKey());
        chapter.setTitle(request.getTitle());
        chapter.setChapterNumber(request.getChapterNumber());
        chapter.setPreview(request.isPreview());
        chapter.setContentType(request.getContentType());
        chapter.setBook(book);

        chapterRepo.save(chapter);

        // Generate a presigned GET URL for clients to download/view this chapter
        String downloadUrl = s3Service.generatePresignedUrl(request.getObjectKey(), 60);

        return new ChapterUploadResponseDTO(chapter.getChapterId(), downloadUrl);
    }

    public Page<ChapterResponseDTO> listChaptersForBook(Long bookId, Pageable pageable) {

        Page<Chapter> chapterPage =
                chapterRepo.findByBookBookIdOrderByChapterNumberAsc(bookId, pageable);

        return chapterPage.map(ch ->
            mapToResponseDTO(ch,null,null)
        );
    }

    public ChapterResponseDTO getPreviewUrl(Long id) {
        Chapter ch = chapterRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapter not found"));
        if (!ch.isPreview()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chapter is not a preview");
        }
        String previewUrl = s3Service.generatePresignedUrl(ch.getS3Key(), 15);

        return mapToResponseDTO(ch, previewUrl, null);
    }

    public ChapterResponseDTO getFullUrl(Long id) {
        Chapter ch = chapterRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapter not found"));

        validateOwnership(ch.getBook());

        String fullUrl = s3Service.generatePresignedUrl(ch.getS3Key(), 60);

        return mapToResponseDTO(ch, null, fullUrl);
    }

    // OWNERSHIP VALIDATION (ADMIN BYPASS)
    private void validateOwnership(Book book) {

        Long currentUserId = securityUtil.getCurrentUserId();
        boolean isAdmin = securityUtil.isAdmin();

        if (!book.getUser().getUserId().equals(currentUserId) && !isAdmin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to perform this action");
        }

    }

    // MAPPER
    private ChapterResponseDTO mapToResponseDTO(
            Chapter chapter,
            String previewUrl,
            String fullUrl) {

        return new ChapterResponseDTO(
                chapter.getChapterId(),
                chapter.getChapterNumber(),
                chapter.getTitle(),
                chapter.isPreview(),
                chapter.getContentType(),
                previewUrl,
                fullUrl
        );

    }
}
