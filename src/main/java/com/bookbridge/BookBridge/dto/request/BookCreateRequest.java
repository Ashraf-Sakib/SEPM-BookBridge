package com.bookbridge.BookBridge.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BookCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 255, message = "Author must be at most 255 characters")
    private String author;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be non-negative")
    private Double price;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount must be at least 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Discount must be at most 100")
    private Double discountPercentage;

    private Boolean available = true;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    @Size(max = 255, message = "Category slug must be at most 255 characters")
    private String categorySlug;

    private MultipartFile image;

}