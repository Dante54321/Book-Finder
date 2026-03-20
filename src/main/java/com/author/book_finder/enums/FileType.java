package com.author.book_finder.enums;

public enum FileType {

    // Chapter FileType
    MARKDOWN("text/markdown"),
    HTML("text/html"),

    // Series & Book Cover FileType
    JPEG("image/jpeg"),
    PNG("image/png");

    private final String mimeType;

    FileType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static FileType fromMimeType(String mimeType) {
        for (FileType type : FileType.values()) {
            if (type.getMimeType().equalsIgnoreCase(mimeType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported MIME type: " + mimeType);
    }
}
