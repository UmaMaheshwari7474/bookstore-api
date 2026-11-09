package com.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request body for creating or updating a Book")
public class BookRequestDto {

    @Schema(description = "Title of the book", example = "Clean Code")
    @NotBlank(message = "Book title is mandatory")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;

    @Schema(description = "International Standard Book Number (10 or 13 digits)", example = "9780132350884")
    @NotBlank(message = "ISBN is mandatory")
    @Pattern(regexp = "^(97[89])?\\d{9}[\\dXx]$", message = "ISBN must be a valid 10 or 13-digit format (digits and optional trailing X)")
    private String isbn;

    @Schema(description = "Description or synopsis of the book", example = "A handbook of agile software craftsmanship.")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @Schema(description = "Retail price of the book", example = "39.99")
    @NotNull(message = "Price is mandatory")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal price;

    @Schema(description = "Publication date (YYYY-MM-DD)", example = "2008-08-01")
    @PastOrPresent(message = "Published date cannot be in the future")
    private LocalDate publishedDate;

    @Schema(description = "Genre or category of the book", example = "Software Engineering")
    @Size(max = 50, message = "Genre cannot exceed 50 characters")
    private String genre;

    @Schema(description = "Available inventory quantity", example = "50")
    @NotNull(message = "Stock quantity is mandatory")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @Schema(description = "ID of the associated author", example = "1")
    @NotNull(message = "Author ID is mandatory")
    private Long authorId;

    public BookRequestDto() {
    }

    public BookRequestDto(String title, String isbn, String description, BigDecimal price,
                          LocalDate publishedDate, String genre, Integer stockQuantity, Long authorId) {
        this.title = title;
        this.isbn = isbn;
        this.description = description;
        this.price = price;
        this.publishedDate = publishedDate;
        this.genre = genre;
        this.stockQuantity = stockQuantity;
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
}
