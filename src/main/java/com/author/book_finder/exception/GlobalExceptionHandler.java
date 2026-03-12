package com.author.book_finder.exception;

import com.author.book_finder.book.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.LockedException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Book Domain Exceptions
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFound(BookNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BookAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(BookAccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // Spring ResponseStatusException
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException ex) {

        HttpStatus status = (HttpStatus) ex.getStatusCode();

        return buildResponse(status, ex.getReason());
    }

    // Security Exceptions
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "You are not authorized to perform this action.");
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleLockedException(LockedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN,"Your account has been banned.");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex) {

        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password."
        );
    }

    // Validation Errors Exception Handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex
    ) {

        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    // Fallback, if adding new exception handlers please place above this method!
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ex.printStackTrace();
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
    }


    // Helper Builder
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