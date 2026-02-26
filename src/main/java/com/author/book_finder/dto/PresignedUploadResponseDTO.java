package com.author.book_finder.dto;

public class PresignedUploadResponseDTO {
    private String objectKey;
    private String uploadUrl;

    public PresignedUploadResponseDTO(String objectKey, String uploadUrl) {
        this.objectKey = objectKey;
        this.uploadUrl = uploadUrl;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }
}
