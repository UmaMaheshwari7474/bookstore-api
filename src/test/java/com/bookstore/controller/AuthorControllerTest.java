package com.bookstore.controller;

import com.bookstore.dto.AuthorRequestDto;
import com.bookstore.dto.AuthorResponseDto;
import com.bookstore.dto.PageResponse;
import com.bookstore.exception.GlobalExceptionHandler;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.service.AuthorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
@Import(GlobalExceptionHandler.class)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthorService authorService;

    @Test
    @DisplayName("POST /api/authors - 201 Created when request is valid")
    void createAuthor_Valid_Returns201() throws Exception {
        AuthorRequestDto request = new AuthorRequestDto("Martin Fowler", "martin@fowler.com", "Author of Refactoring");
        AuthorResponseDto response = new AuthorResponseDto(1L, "Martin Fowler", "martin@fowler.com", "Author of Refactoring", 0);

        when(authorService.createAuthor(any(AuthorRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Martin Fowler"))
                .andExpect(jsonPath("$.email").value("martin@fowler.com"));
    }

    @Test
    @DisplayName("POST /api/authors - 400 Bad Request when validation fails")
    void createAuthor_InvalidEmail_Returns400() throws Exception {
        AuthorRequestDto invalidRequest = new AuthorRequestDto("", "invalid-email-format", "Bio");

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.email").exists());
    }

    @Test
    @DisplayName("GET /api/authors/{id} - 200 OK when author exists")
    void getAuthorById_Exists_Returns200() throws Exception {
        AuthorResponseDto response = new AuthorResponseDto(1L, "Martin Fowler", "martin@fowler.com", "Bio", 2);

        when(authorService.getAuthorById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/authors/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Martin Fowler"))
                .andExpect(jsonPath("$.bookCount").value(2));
    }

    @Test
    @DisplayName("GET /api/authors/{id} - 404 Not Found when author does not exist")
    void getAuthorById_NotFound_Returns404() throws Exception {
        when(authorService.getAuthorById(999L))
                .thenThrow(new ResourceNotFoundException("Author", "id", 999L));

        mockMvc.perform(get("/api/authors/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Author not found with id: '999'"));
    }

    @Test
    @DisplayName("GET /api/authors - 200 OK with paginated list")
    void getAllAuthors_Returns200() throws Exception {
        AuthorResponseDto dto = new AuthorResponseDto(1L, "Martin Fowler", "martin@fowler.com", "Bio", 1);
        PageResponse<AuthorResponseDto> pageResponse = new PageResponse<>(
                List.of(dto), 0, 10, 1L, 1, true, true
        );

        when(authorService.getAllAuthors(0, 10, "id", "asc")).thenReturn(pageResponse);

        mockMvc.perform(get("/api/authors?page=0&size=10&sortBy=id&direction=asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Martin Fowler"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("DELETE /api/authors/{id} - 204 No Content")
    void deleteAuthor_Returns204() throws Exception {
        doNothing().when(authorService).deleteAuthor(1L);

        mockMvc.perform(delete("/api/authors/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
