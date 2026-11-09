package com.bookstore.controller;

import com.bookstore.dto.AuthorRequestDto;
import com.bookstore.dto.AuthorResponseDto;
import com.bookstore.dto.PageResponse;
import com.bookstore.exception.ErrorResponse;
import com.bookstore.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authors")
@Tag(name = "Author Management", description = "Operations pertaining to authors in Bookstore API")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    @Operation(summary = "Create a new author", description = "Registers a new author with unique email into the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Author created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload / validation failure",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email already registered for another author",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AuthorResponseDto> createAuthor(@Valid @RequestBody AuthorRequestDto requestDto) {
        AuthorResponseDto created = authorService.createAuthor(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all authors with pagination and sorting",
            description = "Retrieves a paginated list of authors with configurable page, size, and sorting.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved author page")
    public ResponseEntity<PageResponse<AuthorResponseDto>> getAllAuthors(
            @Parameter(description = "Page index (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (1-100)", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field ('id', 'name', 'email')", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction ('asc' or 'desc')", example = "asc")
            @RequestParam(defaultValue = "asc") String direction
    ) {
        PageResponse<AuthorResponseDto> authors = authorService.getAllAuthors(page, size, sortBy, direction);
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get author by ID", description = "Returns single author details by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Author found"),
            @ApiResponse(responseCode = "404", description = "Author with given ID not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AuthorResponseDto> getAuthorById(
            @Parameter(description = "ID of author to retrieve", example = "1")
            @PathVariable Long id) {
        AuthorResponseDto author = authorService.getAuthorById(id);
        return ResponseEntity.ok(author);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing author", description = "Updates details of an existing author by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Author updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Author not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email already belongs to another author",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AuthorResponseDto> updateAuthor(
            @Parameter(description = "ID of author to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AuthorRequestDto requestDto) {
        AuthorResponseDto updated = authorService.updateAuthor(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an author", description = "Removes an author and cascades deletion to their associated books.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Author deleted successfully (No Content)"),
            @ApiResponse(responseCode = "404", description = "Author not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteAuthor(
            @Parameter(description = "ID of author to delete", example = "1")
            @PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}
