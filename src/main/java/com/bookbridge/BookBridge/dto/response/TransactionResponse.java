package com.bookbridge.BookBridge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private Integer id;
    private Integer bookId;
    private String bookTitle;
    private String sellerUsername;
    private String buyerUsername;
    private Double salePrice;
    private LocalDateTime salesDateTime;
    private String notes;
    private LocalDateTime createdAt;
}
