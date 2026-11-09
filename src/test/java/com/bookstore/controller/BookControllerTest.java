package com.bookstore.controller;

import com.bookstore.dto.BookRequestDto;
import com.bookstore.dto.BookResponseDto;
import com.bookstore.dto.PageResponse;
import com.bookstore.exception.GlobalExceptionHandler;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import(GlobalExceptionHandler.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @Test
    @DisplayName("POST /api/books - 201 Created when valid")
    void createBook_Valid_Returns201() throws Exception {
        BookRequestDto request = new BookRequestDto(
                "Refactoring", "9780201485677", "Improving design",
                new BigDecimal("49.99"), LocalDate.of(1999, 7, 8),
                "Software Engineering", 25, 1L
        );

        BookResponseDto response = new BookResponseDto(
                10L, "Refactoring", "9780201485677", "Improving design",
                new BigDecimal("49.99"), LocalDate.of(1999, 7, 8),
                "Software Engineering", 25, 1L, "Martin Fowler"
        );

        when(bookService.createBook(any(BookRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.title").value("Refactoring"))
                .andExpect(jsonPath("$.isbn").value("9780201485677"))
                .andExpect(jsonPath("$.authorName").value("Martin Fowler"));
    }

    @Test
    @DisplayName("POST /api/books - 400 Bad Request when price is zero or negative")
    void createBook_NegativePrice_Returns400() throws Exception {
        BookRequestDto invalid = new BookRequestDto(
                "Refactoring", "9780201485677", "Desc",
                BigDecimal.ZERO, LocalDate.now(), "Genre", -5, 1L
        );

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.price").exists())
                .andExpect(jsonPath("$.validationErrors.stockQuantity").exists());
    }

    @Test
    @DisplayName("GET /api/books/{id} - 200 OK when found")
    void getBookById_Found_Returns200() throws Exception {
        BookResponseDto response = new BookResponseDto(
                10L, "Refactoring", "9780201485677", "Desc",
                new BigDecimal("49.99"), LocalDate.now(),
                "Software Engineering", 25, 1L, "Martin Fowler"
        );

        when(bookService.getBookById(10L)).thenReturn(response);

        mockMvc.perform(get("/api/books/{id}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.title").value("Refactoring"));
    }

    @Test
    @DisplayName("GET /api/books/{id} - 404 Not Found when not exists")
    void getBookById_NotFound_Returns404() throws Exception {
        when(bookService.getBookById(999L))
                .thenThrow(new ResourceNotFoundException("Book", "id", 999L));

        mockMvc.perform(get("/api/books/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /api/books - 200 OK with filtering and pagination")
    void getBooks_Returns200() throws Exception {
        BookResponseDto response = new BookResponseDto(
                10L, "Refactoring", "9780201485677", "Desc",
                new BigDecimal("49.99"), LocalDate.now(),
                "Software Engineering", 25, 1L, "Martin Fowler"
        );
        PageResponse<BookResponseDto> pageResponse = new PageResponse<>(
                List.of(response), 0, 10, 1L, 1, true, true
        );

        when(bookService.getBooks(any(), any(), any(), any(), any(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/books?genre=Software Engineering&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Refactoring"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("DELETE /api/books/{id} - 204 No Content")
    void deleteBook_Returns204() throws Exception {
        doNothing().when(bookService).deleteBook(10L);

        mockMvc.perform(delete("/api/books/{id}", 10L))
                .andExpect(status().isNoContent());
    }
}
