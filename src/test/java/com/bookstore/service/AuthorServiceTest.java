package com.bookstore.service;

import com.bookstore.dto.AuthorRequestDto;
import com.bookstore.dto.AuthorResponseDto;
import com.bookstore.dto.PageResponse;
import com.bookstore.entity.Author;
import com.bookstore.exception.DuplicateResourceException;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.repository.AuthorRepository;
import com.bookstore.service.impl.AuthorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private Author author;
    private AuthorRequestDto requestDto;

    @BeforeEach
    void setUp() {
        author = new Author(1L, "Robert C. Martin", "unclebob@cleancode.com", "Clean code advocate");
        author.setBooks(new ArrayList<>());
        requestDto = new AuthorRequestDto("Robert C. Martin", "unclebob@cleancode.com", "Clean code advocate");
    }

    @Test
    @DisplayName("createAuthor - success")
    void createAuthor_Success() {
        when(authorRepository.existsByEmail("unclebob@cleancode.com")).thenReturn(false);
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        AuthorResponseDto result = authorService.createAuthor(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Robert C. Martin");
        assertThat(result.getEmail()).isEqualTo("unclebob@cleancode.com");
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    @DisplayName("createAuthor - throws DuplicateResourceException when email exists")
    void createAuthor_DuplicateEmail_ThrowsException() {
        when(authorRepository.existsByEmail("unclebob@cleancode.com")).thenReturn(true);

        assertThatThrownBy(() -> authorService.createAuthor(requestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("unclebob@cleancode.com");

        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    @DisplayName("getAuthorById - success")
    void getAuthorById_Success() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        AuthorResponseDto result = authorService.getAuthorById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Robert C. Martin");
    }

    @Test
    @DisplayName("getAuthorById - throws ResourceNotFoundException when author does not exist")
    void getAuthorById_NotFound_ThrowsException() {
        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.getAuthorById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("getAllAuthors - success with pagination")
    void getAllAuthors_Success() {
        Page<Author> page = new PageImpl<>(List.of(author));
        when(authorRepository.findAll(any(Pageable.class))).thenReturn(page);

        PageResponse<AuthorResponseDto> response = authorService.getAllAuthors(0, 10, "id", "asc");

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1L);
    }

    @Test
    @DisplayName("updateAuthor - success")
    void updateAuthor_Success() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.existsByEmailAndIdNot("unclebob@cleancode.com", 1L)).thenReturn(false);
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        AuthorResponseDto updated = authorService.updateAuthor(1L, requestDto);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(1L);
        verify(authorRepository).save(author);
    }

    @Test
    @DisplayName("deleteAuthor - success")
    void deleteAuthor_Success() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        authorService.deleteAuthor(1L);

        verify(authorRepository).delete(author);
    }
}
