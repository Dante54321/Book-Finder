package com.author.book_finder.series.service;

import com.author.book_finder.book.entity.Book;
import com.author.book_finder.book.service.BookService;
import com.author.book_finder.infrastructure.aws.S3Service;
import com.author.book_finder.series.dto.*;
import com.author.book_finder.series.entity.Series;
import com.author.book_finder.user.entity.User;
import com.author.book_finder.series.repository.SeriesRepository;
import com.author.book_finder.user.repository.UserRepository;
import com.author.book_finder.security.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.author.book_finder.enums.FileType;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@Transactional
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final S3Service s3Service;
    private final BookService bookService;

    public SeriesService(SeriesRepository seriesRepository, UserRepository userRepository, SecurityUtil securityUtil, S3Service s3Service, BookService bookService) {
        this.seriesRepository = seriesRepository;
        this.userRepository = userRepository;
        this.securityUtil = securityUtil;
        this.s3Service = s3Service;
        this.bookService = bookService;
    }

    // CREATE SERIES
    public SeriesResponseDTO createSeries(SeriesCreateRequestDTO seriesCreateRequestDTO) {

        Long userId = securityUtil.getCurrentUserId();

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Series series = new Series();
        series.setSeriesName(seriesCreateRequestDTO.getSeriesName());
        series.setDescription(seriesCreateRequestDTO.getDescription());
        series.setPublishDate(seriesCreateRequestDTO.getPublishDate());

        if (seriesCreateRequestDTO.getCoverImageKey() != null && !seriesCreateRequestDTO.getCoverImageKey().isBlank()) {
            series.setCoverImageKey(seriesCreateRequestDTO.getCoverImageKey());
        }

        author.addSeries(series);

        Series savedSeries = seriesRepository.save(series);

        return mapToResponseDTO(savedSeries);
    }

    // GENERATE PRESIGNED UPLOAD URL
    public PresignedUploadResponseDTO generateCoverUploadUrl(Long seriesId, String filename, FileType fileType) {

        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Series not found"));

        validateOwnership(series);

        if (fileType != FileType.JPEG && fileType != FileType.PNG) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only JPEG or PNG allowed");
        }

        String key = "series/" + seriesId + "/cover/" + UUID.randomUUID() + "_" + filename;

        return new PresignedUploadResponseDTO(
                key,
                s3Service.generatePresignedUploadUrl(key, fileType, 15)
        );
    }

    // GET PRESIGNED URL FOR EXISTING SERIES COVER
    public String getCoverUrl(Long seriesId) {

        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Series not found"));

        if (series.getCoverImageKey() == null || series.getCoverImageKey().isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cover not found");
        }

        return s3Service.generatePresignedUrl(series.getCoverImageKey(), 60);
    }

    // GET ALL SERIES PUBLIC
    public Page<SeriesResponseDTO> getAllSeries(Pageable pageable) {
        return seriesRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    // GET MY SERIES
    public Page<SeriesResponseDTO> getMySeries(Pageable pageable) {

        Long userId = securityUtil.getCurrentUserId();

        return seriesRepository.findByUser_UserId(userId, pageable).map(this::mapToResponseDTO);
    }

    // GET SERIES DETAILS PUBLIC
    public SeriesDetailsDTO getSeriesDetails(Long seriesId) {

        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Series not found"));

        return mapToDetailsDTO(series);
    }

    // UPDATE SERIES
    public SeriesResponseDTO updateSeries(Long seriesId, SeriesCreateRequestDTO seriesCreateRequestDTO) {
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Series not found"));

        validateOwnership(series);

        series.setSeriesName(seriesCreateRequestDTO.getSeriesName());
        series.setDescription(seriesCreateRequestDTO.getDescription());
        series.setPublishDate(seriesCreateRequestDTO.getPublishDate());

        if (seriesCreateRequestDTO.getCoverImageKey() != null && !seriesCreateRequestDTO.getCoverImageKey().isBlank()) {

            if (series.getCoverImageKey() != null && !series.getCoverImageKey().isBlank()) {
                s3Service.deleteObject(series.getCoverImageKey());
            }

            series.setCoverImageKey(seriesCreateRequestDTO.getCoverImageKey());
        }

        Series updatedSeries = seriesRepository.save(series);

        return mapToResponseDTO(updatedSeries);
    }

    // DELETE SERIES
    public void deleteSeries(Long seriesId) {

        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Series not found"));

        validateOwnership(series);

        if (series.getCoverImageKey() != null && !series.getCoverImageKey().isBlank()) {
            s3Service.deleteObject(series.getCoverImageKey());
        }

        for (Book book : new ArrayList<>(series.getBooks())) {
            bookService.deleteBook(book.getBookId());
        }

        series.getUser().removeSeries(series);
    }

    // OWNERSHIP VALIDATION (ADMIN BYPASS)
    private void validateOwnership(Series series) {

        Long currentUserId = securityUtil.getCurrentUserId();
        boolean isAdmin = securityUtil.isAdmin();

        if (!series.getUser().getUserId().equals(currentUserId) && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to perform this action");
        }
    }

    // MAPPER
    private SeriesResponseDTO mapToResponseDTO(Series series) {

        int totalBooks = series.getBooks() != null ? series.getBooks().size() : 0;

        String coverUrl = null;
        if (series.getCoverImageKey() != null && !series.getCoverImageKey().isBlank()) {
            coverUrl = s3Service.getPublicUrl(series.getCoverImageKey());
        }

        return new SeriesResponseDTO(
                series.getSeriesId(),
                series.getSeriesName(),
                series.getDescription(),
                series.getPublishDate(),
                series.getUser().getUsername(),
                totalBooks,
                coverUrl
        );
    }

    private SeriesDetailsDTO mapToDetailsDTO(Series series) {

        List<BooksInSeriesDTO> booksInSeries =
                series.getBooks() == null ? List.of() :
                        series.getBooks().stream()
                                .map(book -> new BooksInSeriesDTO(
                                        book.getBookId(),
                                        book.getVolumeNumber(),
                                        book.getTitle()
                                ))
                                .toList();

        String coverUrl = null;
        if (series.getCoverImageKey() != null && !series.getCoverImageKey().isBlank()) {
            coverUrl = s3Service.getPublicUrl(series.getCoverImageKey());
        }

        return new SeriesDetailsDTO(
                series.getSeriesId(),
                series.getSeriesName(),
                series.getDescription(),
                series.getPublishDate(),
                series.getUser().getUsername(),
                booksInSeries.size(),
                booksInSeries,
                coverUrl
        );
    }
}
