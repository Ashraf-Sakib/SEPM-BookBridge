package com.bookbridge.BookBridge.service;

import com.bookbridge.BookBridge.dto.response.BookResponse;
import com.bookbridge.BookBridge.dto.response.PageResponse;
import com.bookbridge.BookBridge.entity.Book;
import com.bookbridge.BookBridge.entity.User;
import com.bookbridge.BookBridge.exception.ResourceNotFoundException;
import com.bookbridge.BookBridge.repository.BookRepository;
import com.bookbridge.BookBridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookResponse createBook(Book book, Integer userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        book.setAddedBy(user);
        book.setImageUrl(normalizeImageUrl(book.getImageUrl()));
        Book savedBook = bookRepository.save(book);
        return convertToResponse(savedBook);
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(this::convertToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
    }

    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getAllBooks(int page, int size) {
        Page<Book> books = bookRepository.findAll(PageRequest.of(page, size));
        return convertPageToResponse(books);
    }

    @Transactional(readOnly = true)
    public PageResponse<BookResponse> searchBooksByTitle(String title, int page, int size) {
        Page<Book> books = bookRepository.findByTitleContainingIgnoreCase(title, PageRequest.of(page, size));
        return convertPageToResponse(books);
    }

    @Transactional(readOnly = true)
    public PageResponse<BookResponse> searchBooksByAuthor(String author, int page, int size) {
        Page<Book> books = bookRepository.findByAuthorContainingIgnoreCase(author, PageRequest.of(page, size));
        return convertPageToResponse(books);
    }

    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getBooksByFilters(String title, String author, String categorySlug, int page, int size) {
        String normalizedTitle = normalizeFilterValue(title);
        String normalizedAuthor = normalizeFilterValue(author);
        String normalizedCategory = normalizeFilterValue(categorySlug);

        List<Book> filtered = bookRepository.findAll().stream()
                .filter(book -> containsIgnoreCase(book.getTitle(), normalizedTitle))
                .filter(book -> containsIgnoreCase(book.getAuthor(), normalizedAuthor))
                .filter(book -> matchesCategorySlug(book, normalizedCategory))
                .collect(Collectors.toList());

        int total = filtered.size();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, size);
        int from = Math.min(safePage * safeSize, total);
        int to = Math.min(from + safeSize, total);

        PageResponse<BookResponse> response = new PageResponse<>();
        response.setContent(filtered.subList(from, to).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
        response.setPageNumber(safePage);
        response.setPageSize(safeSize);
        response.setTotalElements(total);
        response.setTotalPages(total == 0 ? 0 : (int) Math.ceil((double) total / safeSize));
        response.setFirst(safePage == 0);
        response.setLast(to >= total);
        return response;
    }

    private boolean containsIgnoreCase(String source, String filter) {
        if (filter == null) {
            return true;
        }
        if (source == null) {
            return false;
        }
        return source.toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT));
    }

    private boolean matchesCategorySlug(Book book, String filterSlug) {
        if (filterSlug == null) {
            return true;
        }
        if (book.getCategory() == null || book.getCategory().getSlug() == null) {
            return false;
        }
        return book.getCategory().getSlug().equalsIgnoreCase(filterSlug);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getBooksAddedByUser(Integer userId) {
        return bookRepository.findByAddedByIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Book getBookEntityById(Integer bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
    }

    @Transactional
    public BookResponse updateBook(Integer bookId, Book bookDetails) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        if (bookDetails.getTitle() != null) book.setTitle(bookDetails.getTitle());
        if (bookDetails.getDescription() != null) book.setDescription(bookDetails.getDescription());
        if (bookDetails.getAuthor() != null) book.setAuthor(bookDetails.getAuthor());
        if (bookDetails.getIsbn() != null) book.setIsbn(bookDetails.getIsbn());
        if (bookDetails.getPublisher() != null) book.setPublisher(bookDetails.getPublisher());
        if (bookDetails.getPublishYear() != null) book.setPublishYear(bookDetails.getPublishYear());
        if (bookDetails.getImageUrl() != null) book.setImageUrl(normalizeImageUrl(bookDetails.getImageUrl()));
        if (bookDetails.getRating() != null) book.setRating(bookDetails.getRating());
        if (bookDetails.getTotalPages() != null) book.setTotalPages(bookDetails.getTotalPages());
        if (bookDetails.getPrice() != null) book.setPrice(bookDetails.getPrice());
        if (bookDetails.getDiscountPercentage() != null) book.setDiscountPercentage(bookDetails.getDiscountPercentage());
        if (bookDetails.getAvailable() != null) book.setAvailable(bookDetails.getAvailable());
        if (bookDetails.getCondition() != null) book.setCondition(bookDetails.getCondition());
        if (bookDetails.getCategory() != null) book.setCategory(bookDetails.getCategory());

        Book updatedBook = bookRepository.save(book);
        return convertToResponse(updatedBook);
    }

    @Transactional
    public BookResponse updateBookForOwner(Integer bookId, Integer userId, Book bookDetails) {
        Book book = getBookEntityById(bookId);
        if (book.getAddedBy() == null || !book.getAddedBy().getId().equals(userId)) {
            throw new ResourceNotFoundException("Book not found");
        }

        return updateBook(bookId, bookDetails);
    }

    @Transactional
    public void deleteBook(Integer bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Book not found");
        }
        bookRepository.deleteById(bookId);
    }

    private BookResponse convertToResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setDescription(book.getDescription());
        response.setAuthor(book.getAuthor());
        response.setIsbn(book.getIsbn());
        response.setPublisher(book.getPublisher());
        response.setPublishYear(book.getPublishYear());
        response.setImageUrl(normalizeImageUrl(book.getImageUrl()));
        response.setRating(book.getRating());
        response.setTotalPages(book.getTotalPages());
        response.setPrice(book.getPrice());
        response.setDiscountPercentage(book.getDiscountPercentage());
        response.setDiscountedPrice(calculateDiscountedPrice(book.getPrice(), book.getDiscountPercentage()));
        response.setAvailable(book.getAvailable());
        response.setCondition(book.getCondition());
        response.setCategoryName(book.getCategory() != null ? book.getCategory().getName() : null);
        response.setCategorySlug(book.getCategory() != null ? book.getCategory().getSlug() : null);
        response.setAddedBy(book.getAddedBy() != null ? book.getAddedBy().getUsername() : null);
        response.setCreatedAt(book.getCreatedAt());
        return response;
    }

    private Double calculateDiscountedPrice(Double price, Double discountPercentage) {
        if (price == null || discountPercentage == null) {
            return null;
        }

        double discountedPrice = price - (price * discountPercentage / 100.0);
        return Math.max(0.0, discountedPrice);
    }

    private String normalizeImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return imageUrl;
        }
        if (imageUrl.startsWith("/") || imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return imageUrl;
        }
        return "/" + imageUrl;
    }

    private String normalizeFilterValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private PageResponse<BookResponse> convertPageToResponse(Page<Book> page) {
        PageResponse<BookResponse> response = new PageResponse<>();
        response.setContent(page.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        return response;
    }
}