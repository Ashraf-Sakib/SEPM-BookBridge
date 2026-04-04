package com.bookbridge.BookBridge.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

	private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
			"image/jpeg",
			"image/png",
			"image/webp",
			"image/gif"
	);

	private final Path uploadRoot;

	public FileStorageService(@Value("${bookbridge.upload-dir:uploads}") String uploadDir) {
		this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
	}

	public boolean isSupportedImage(MultipartFile file) {
		String contentType = file.getContentType();
		return contentType != null && ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase());
	}

	public String store(MultipartFile file) {
		try {
			Files.createDirectories(uploadRoot);

			String extension = resolveExtension(file.getOriginalFilename());
			String fileName = UUID.randomUUID() + extension;
			Path destination = uploadRoot.resolve(fileName).normalize();

			if (!destination.startsWith(uploadRoot)) {
				throw new IllegalStateException("Invalid file path");
			}

			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
			}

			return "/uploads/" + fileName;
		} catch (IOException ex) {
			throw new IllegalStateException("Could not store uploaded file", ex);
		}
	}

	private String resolveExtension(String originalFileName) {
		if (originalFileName == null) {
			return ".jpg";
		}
		int dotIndex = originalFileName.lastIndexOf('.');
		if (dotIndex < 0 || dotIndex == originalFileName.length() - 1) {
			return ".jpg";
		}
		String extension = originalFileName.substring(dotIndex).toLowerCase();
		return switch (extension) {
			case ".jpg", ".jpeg", ".png", ".webp", ".gif" -> extension;
			default -> ".jpg";
		};
	}
}
