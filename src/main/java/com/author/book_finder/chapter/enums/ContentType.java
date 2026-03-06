package com.author.book_finder.chapter.enums;

public enum ContentType {
    MARKDOWN("text/markdown"),
    HTML("text/html");

    private final String mimeType;

    ContentType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }
}
