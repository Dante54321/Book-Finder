package com.author.book_finder.auth.dto;

public class SignupResponseDTO {

    private String message;

    public SignupResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

