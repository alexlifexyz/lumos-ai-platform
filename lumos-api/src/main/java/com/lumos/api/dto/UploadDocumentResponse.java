package com.lumos.api.dto;

public record UploadDocumentResponse(
    String uuid,
    String filename,
    String status
) {}
