package com.author.book_finder.book.exception;

public class BookAccessDeniedException extends RuntimeException {

    public BookAccessDeniedException() {
        super("You are not allowed to perform this action.");
    }
}
