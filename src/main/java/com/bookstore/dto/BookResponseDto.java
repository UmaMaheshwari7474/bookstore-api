package com.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Response object representing a Book")
public class BookResponseDto {

    @Schema(description = "Unique database ID of the book", example = "1")
    private Long id;

    @Schema(description = "Title of the book", example = "Clean Code")
    private String title;

    @Schema(description = "International Standard Book Number", example = "9780132350884")
    private String isbn;

    @Schema(description = "Description or synopsis of the book", example = "A handbook of agile software craftsmanship.")
    private String description;

    @Schema(description = "Retail price of the book", example = "39.99")
    private BigDecimal price;

    @Schema(description = "Publication date (YYYY-MM-DD)", example = "2008-08-01")
    private LocalDate publishedDate;

    @Schema(description = "Genre or category of the book", example = "Software Engineering")
    private String genre;

    @Schema(description = "Available inventory quantity", example = "50")
    private Integer stockQuantity;

    @Schema(description = "ID of the associated author", example = "1")
    private Long authorId;

    @Schema(description = "Full name of the associated author", example = "Robert C. Martin")
    private String authorName;

    public BookResponseDto() {
    }

    public BookResponseDto(Long id, String title, String isbn, String description, BigDecimal price,
                           LocalDate publishedDate, String genre, Integer stockQuantity,
                           Long authorId, String authorName) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.description = description;
        this.price = price;
        this.publishedDate = publishedDate;
        this.genre = genre;
        this.stockQuantity = stockQuantity;
        this.authorId = authorId;
        this.authorName = authorName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
