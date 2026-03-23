package com.author.book_finder.readingprogress.service;

import com.author.book_finder.book.entity.Book;
import com.author.book_finder.book.repository.BookRepository;
import com.author.book_finder.infrastructure.aws.S3Service;
import com.author.book_finder.readingprogress.dto.CurrentlyReadingResponseDTO;
import com.author.book_finder.readingprogress.dto.StartReadingRequestDTO;
import com.author.book_finder.readingprogress.entity.ReadingProgress;
import com.author.book_finder.enums.ReadingStatus;
import com.author.book_finder.readingprogress.repository.ReadingProgressRepository;
import com.author.book_finder.user.entity.User;
import com.author.book_finder.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import com.author.book_finder.enums.PublicationStatus;

@Service
@Transactional
public class ReadingProgressService {

    private final ReadingProgressRepository readingProgressRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final S3Service s3Service;

    public ReadingProgressService(ReadingProgressRepository readingProgressRepository,
                                  UserRepository userRepository,
                                  BookRepository bookRepository,
                                  S3Service s3Service) {
        this.readingProgressRepository = readingProgressRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.s3Service = s3Service;
    }

    public void startReading(StartReadingRequestDTO requestDTO, Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Book book = bookRepository.findByBookIdAndPublicationStatus(
                        requestDTO.getBookId(),
                        PublicationStatus.PUBLISHED
                )
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        LocalDateTime now = LocalDateTime.now();

        ReadingProgress readingProgress = readingProgressRepository
                .findByUser_UserIdAndBook_BookId(user.getUserId(), book.getBookId())
                .orElse(null);

        if (readingProgress == null) {
            readingProgress = new ReadingProgress();
            readingProgress.setUser(user);
            readingProgress.setBook(book);
            readingProgress.setStatus(ReadingStatus.CURRENTLY_READING);
            readingProgress.setStartedAt(now);
            readingProgress.setLastReadAt(now);
        } else {
            readingProgress.setStatus(ReadingStatus.CURRENTLY_READING);
            readingProgress.setLastReadAt(now);
        }

        readingProgressRepository.save(readingProgress);
    }

    @Transactional(readOnly = true)
    public List<CurrentlyReadingResponseDTO> getCurrentlyReading(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<ReadingProgress> progressList =
                readingProgressRepository.findByUser_UserIdAndStatusOrderByLastReadAtDesc(
                        user.getUserId(),
                        ReadingStatus.CURRENTLY_READING
                );

        return progressList.stream()
                .map(this::mapToCurrentlyReadingDTO)
                .toList();
    }

    private CurrentlyReadingResponseDTO mapToCurrentlyReadingDTO(ReadingProgress readingProgress) {
        Book book = readingProgress.getBook();

        String coverUrl = null;
        if (book.getCoverImageKey() != null && !book.getCoverImageKey().isBlank()) {
            coverUrl = s3Service.getPublicUrl(book.getCoverImageKey());
        }

        return new CurrentlyReadingResponseDTO(
                book.getBookId(),
                book.getTitle(),
                book.getUser().getUsername(),
                coverUrl,
                readingProgress.getLastReadAt()
        );
    }
}