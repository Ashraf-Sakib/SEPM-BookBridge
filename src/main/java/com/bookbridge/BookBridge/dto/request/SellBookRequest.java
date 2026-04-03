package com.bookbridge.BookBridge.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellBookRequest {

    @NotNull(message = "Book ID is required")
    private Integer bookId;

    @NotNull(message = "Buyer ID is required")
    private Integer buyerId;

    @NotNull(message = "Sale price is required")
    @Min(value = 0, message = "Sale price must be greater than or equal to 0")
    private Double salePrice;

    private String notes;
}
