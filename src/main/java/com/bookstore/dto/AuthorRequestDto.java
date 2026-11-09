package com.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for creating or updating an Author")
public class AuthorRequestDto {

    @Schema(description = "Full name of the author", example = "Robert C. Martin")
    @NotBlank(message = "Author name is mandatory")
    @Size(min = 2, max = 100, message = "Author name must be between 2 and 100 characters")
    private String name;

    @Schema(description = "Unique email address of the author", example = "unclebob@cleancode.com")
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email must be a valid email address")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    @Schema(description = "Short biography of the author", example = "American software engineer and co-author of Agile Manifesto.")
    @Size(max = 2000, message = "Biography cannot exceed 2000 characters")
    private String biography;

    public AuthorRequestDto() {
    }

    public AuthorRequestDto(String name, String email, String biography) {
        this.name = name;
        this.email = email;
        this.biography = biography;
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
}
