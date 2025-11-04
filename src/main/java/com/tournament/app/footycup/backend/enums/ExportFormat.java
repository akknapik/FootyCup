package com.tournament.app.footycup.backend.enums;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

public enum ExportFormat {
    PDF("pdf", MediaType.APPLICATION_PDF, "pdf"),
    CSV("csv", MediaType.parseMediaType("text/csv"), "csv");

    private final String paramValue;
    private final MediaType mediaType;
    private final String fileExtension;

    ExportFormat(String paramValue, MediaType mediaType, String fileExtension) {
        this.paramValue = paramValue;
        this.mediaType = mediaType;
        this.fileExtension = fileExtension;
    }

    public MediaType mediaType() {
        return mediaType;
    }

    public String fileExtension() {
        return fileExtension;
    }

    public static ExportFormat fromParam(String value) {
        if (!StringUtils.hasText(value)) {
            return PDF;
        }
        for (ExportFormat format : values()) {
            if (format.paramValue.equalsIgnoreCase(value) || format.name().equalsIgnoreCase(value)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unsupported export format: " + value);
    }
}