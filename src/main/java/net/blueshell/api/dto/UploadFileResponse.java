package net.blueshell.api.dto;

public record UploadFileResponse(String fileName, String fileDownloadUri, String fileType, long size) {
}
