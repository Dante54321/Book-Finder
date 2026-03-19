package com.author.book_finder.exception;

import com.author.book_finder.book.exception.*;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===================== BOOK =====================

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFound(BookNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BookAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleBookAccessDenied(BookAccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // ===================== JWT =====================

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "JWT token has expired.");
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSignature(SecurityException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid JWT signature.");
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJwt(MalformedJwtException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Malformed JWT token.");
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid JWT token.");
    }

    // ===================== AWS S3 =====================

    @ExceptionHandler(AwsServiceException.class)
    public ResponseEntity<ErrorResponse> handleS3ServiceException(AwsServiceException ex) {
        return buildResponse(HttpStatus.BAD_GATEWAY,
                "AWS S3 error: " + ex.awsErrorDetails().errorMessage());
    }

    @ExceptionHandler(SdkClientException.class)
    public ResponseEntity<ErrorResponse> handleS3ClientException(SdkClientException ex) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE,
                "Unable to connect to AWS S3.");
    }

    // ===================== SECURITY =====================

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleSecurityAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN,
                "You are not authorized to perform this action.");
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleLockedException(LockedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN,
                "Your account has been banned.");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED,
                "Invalid username or password.");
    }

    // ===================== VALIDATION =====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    // ===================== SPRING =====================

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException ex) {

        HttpStatus status = (HttpStatus) ex.getStatusCode();
        return buildResponse(status, ex.getReason());
    }

    // ===================== FALLBACK =====================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error occurred");
    }

    // ===================== BUILDER =====================

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        status.value(),
                        status.getReasonPhrase(),
                        message
                ),
                status
        );
    }
}