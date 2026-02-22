package com.author.book_finder.service;

import com.author.book_finder.dto.ChapterResponseDTO;
import com.author.book_finder.dto.ChapterUploadRequestDTO;
import com.author.book_finder.dto.ChapterUploadResponseDTO;
import com.author.book_finder.entity.Book;
import com.author.book_finder.entity.Chapter;
import com.author.book_finder.repository.BookRepository;
import com.author.book_finder.repository.ChapterRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChapterService {

    private final ChapterRepository chapterRepo;
    private final BookRepository bookRepo;
    private final S3Service s3Service;

    public ChapterService(ChapterRepository chapterRepo,
                          BookRepository bookRepo,
                          S3Service s3Service) {
        this.chapterRepo = chapterRepo;
        this.bookRepo = bookRepo;
        this.s3Service = s3Service;
    }

    public ChapterUploadResponseDTO uploadChapter(ChapterUploadRequestDTO request) throws IOException {

        String s3Key = s3Service.uploadFile(request.getFile());

        Book book = bookRepo.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Chapter chapter = new Chapter();
        chapter.setTitle(request.getTitle());
        chapter.setChapterNumber(request.getChapterNumber());
        chapter.setS3Key(s3Key);
        chapter.setContentType(request.getFile().getOriginalFilename().toLowerCase().endsWith(".md")
                ? "MARKDOWN" : "HTML");
        chapter.setIsPreview(request.getIsPreview());
        chapter.setBook(book);

        chapterRepo.save(chapter);

        // Generate a download URL for clients
        String downloadUrl = s3Service.generatePresignedUrl(s3Key, 60);

        return new ChapterUploadResponseDTO(chapter.getChapterId(), downloadUrl);
    }

    public List<ChapterResponseDTO> listChaptersForBook(Long bookId) {
        return chapterRepo.findByBookBookIdOrderByChapterNumberAsc(bookId)
                .stream()
                .map(ch -> {
                    String previewUrl = ch.isPreview()
                            ? s3Service.generatePresignedUrl(ch.getS3Key(), 15)
                            : null;

                    return new ChapterResponseDTO(
                            ch.getChapterId(),
                            ch.getChapterNumber(),
                            ch.getTitle(),
                            ch.isPreview(),
                            ch.getContentType(),
                            previewUrl,
                            null  // full URL not included in a list
                    );
                }).collect(Collectors.toList());
    }

    public ChapterResponseDTO getPreviewUrl(Long id) {
        Chapter ch = chapterRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        if (!ch.isPreview()) {
            throw new RuntimeException("Not a preview chapter");
        }
        String url = s3Service.generatePresignedUrl(ch.getS3Key(), 15);

        return new ChapterResponseDTO(
                ch.getChapterId(),
                ch.getChapterNumber(),
                ch.getTitle(),
                true,
                ch.getContentType(),
                url,
                null
        );
    }

    public ChapterResponseDTO getFullUrl(Long id) {
        Chapter ch = chapterRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        String fullUrl = s3Service.generatePresignedUrl(ch.getS3Key(), 60);

        return new ChapterResponseDTO(
                ch.getChapterId(),
                ch.getChapterNumber(),
                ch.getTitle(),
                ch.isPreview(),
                ch.getContentType(),
                null,
                fullUrl
        );
    }
}
