package com.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object representing an Author")
public class AuthorResponseDto {

    @Schema(description = "Unique database ID of the author", example = "1")
    private Long id;

    @Schema(description = "Full name of the author", example = "Robert C. Martin")
    private String name;

    @Schema(description = "Email address of the author", example = "unclebob@cleancode.com")
    private String email;

    @Schema(description = "Short biography of the author", example = "Author of Clean Code and Clean Architecture.")
    private String biography;

    @Schema(description = "Total number of books written by this author in the catalog", example = "5")
    private int bookCount;

    public AuthorResponseDto() {
    }

    public AuthorResponseDto(Long id, String name, String email, String biography, int bookCount) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.biography = biography;
        this.bookCount = bookCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }
}
