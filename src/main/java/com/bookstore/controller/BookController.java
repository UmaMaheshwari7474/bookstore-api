package com.bookstore.controller;

import com.bookstore.dto.BookRequestDto;
import com.bookstore.dto.BookResponseDto;
import com.bookstore.dto.PageResponse;
import com.bookstore.exception.ErrorResponse;
import com.bookstore.service.BookService;
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

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Book Management", description = "Operations pertaining to books in Bookstore API")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @Operation(summary = "Create a new book", description = "Adds a new book linked to an existing author.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failure",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Author ID not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "ISBN already exists in catalog",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BookResponseDto> createBook(@Valid @RequestBody BookRequestDto requestDto) {
        BookResponseDto created = bookService.createBook(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get books with filtering, sorting, and pagination",
            description = "Search and filter books by title, genre, authorId, price range with pagination and sorting.")
    @ApiResponse(responseCode = "200", description = "Page of books matching search criteria")
    public ResponseEntity<PageResponse<BookResponseDto>> getBooks(
            @Parameter(description = "Filter by title substring (case-insensitive)", example = "Clean")
            @RequestParam(required = false) String title,
            @Parameter(description = "Filter by genre", example = "Software Engineering")
            @RequestParam(required = false) String genre,
            @Parameter(description = "Filter by author ID", example = "1")
            @RequestParam(required = false) Long authorId,
            @Parameter(description = "Minimum price threshold", example = "10.00")
            @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price threshold", example = "50.00")
            @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Page index (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (1-100)", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field ('id', 'title', 'isbn', 'price', 'publishedDate', 'genre', 'stockQuantity')", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction ('asc' or 'desc')", example = "asc")
            @RequestParam(defaultValue = "asc") String direction
    ) {
        PageResponse<BookResponseDto> books = bookService.getBooks(
                title, genre, authorId, minPrice, maxPrice, page, size, sortBy, direction
        );
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Retrieves a book's details by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BookResponseDto> getBookById(
            @Parameter(description = "ID of book to retrieve", example = "1")
            @PathVariable Long id) {
        BookResponseDto book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing book", description = "Updates details, price, or author of an existing book.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Book or Author not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "ISBN already belongs to another book",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BookResponseDto> updateBook(
            @Parameter(description = "ID of book to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody BookRequestDto requestDto) {
        BookResponseDto updated = bookService.updateBook(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book", description = "Deletes a book from the bookstore catalog.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book deleted successfully (No Content)"),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "ID of book to delete", example = "1")
            @PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get all books by author", description = "Retrieves all books authored by the specified author ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of books by author"),
            @ApiResponse(responseCode = "404", description = "Author not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<BookResponseDto>> getBooksByAuthor(
            @Parameter(description = "Author ID", example = "1")
            @PathVariable Long authorId,
            @Parameter(description = "Page index (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (1-100)", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field ('id', 'title', 'price', etc.)", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction ('asc' or 'desc')", example = "asc")
            @RequestParam(defaultValue = "asc") String direction
    ) {
        PageResponse<BookResponseDto> books = bookService.getBooksByAuthor(authorId, page, size, sortBy, direction);
        return ResponseEntity.ok(books);
    }
}
