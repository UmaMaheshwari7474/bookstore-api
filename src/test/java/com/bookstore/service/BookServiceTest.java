package com.bookstore.service;

import com.bookstore.dto.BookRequestDto;
import com.bookstore.dto.BookResponseDto;
import com.bookstore.dto.PageResponse;
import com.bookstore.entity.Author;
import com.bookstore.entity.Book;
import com.bookstore.exception.DuplicateResourceException;
import com.bookstore.exception.InvalidRequestException;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.repository.AuthorRepository;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.impl.BookServiceImpl;
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
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Author author;
    private Book book;
    private BookRequestDto requestDto;

    @BeforeEach
    void setUp() {
        author = new Author(1L, "Robert C. Martin", "unclebob@cleancode.com", "Clean coder");
        book = new Book(
                10L,
                "Clean Code",
                "9780132350884",
                "Handbook of agile craftsmanship",
                new BigDecimal("39.99"),
                LocalDate.of(2008, 8, 1),
                "Software Engineering",
                50,
                author
        );

        requestDto = new BookRequestDto(
                "Clean Code",
                "9780132350884",
                "Handbook of agile craftsmanship",
                new BigDecimal("39.99"),
                LocalDate.of(2008, 8, 1),
                "Software Engineering",
                50,
                1L
        );
    }

    @Test
    @DisplayName("createBook - success")
    void createBook_Success() {
        when(bookRepository.existsByIsbn("9780132350884")).thenReturn(false);
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookResponseDto result = bookService.createBook(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getTitle()).isEqualTo("Clean Code");
        assertThat(result.getAuthorId()).isEqualTo(1L);
        assertThat(result.getAuthorName()).isEqualTo("Robert C. Martin");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("createBook - throws DuplicateResourceException when ISBN exists")
    void createBook_DuplicateIsbn_ThrowsException() {
        when(bookRepository.existsByIsbn("9780132350884")).thenReturn(true);

        assertThatThrownBy(() -> bookService.createBook(requestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("9780132350884");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("createBook - throws ResourceNotFoundException when author does not exist")
    void createBook_AuthorNotFound_ThrowsException() {
        when(bookRepository.existsByIsbn("9780132350884")).thenReturn(false);
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.createBook(requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Author");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("getBookById - success")
    void getBookById_Success() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        BookResponseDto result = bookService.getBookById(10L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getTitle()).isEqualTo("Clean Code");
    }

    @Test
    @DisplayName("getBookById - throws ResourceNotFoundException when book not found")
    void getBookById_NotFound_ThrowsException() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("getBooks - invalid price filter throws InvalidRequestException")
    void getBooks_MinPriceGreaterThanMaxPrice_ThrowsException() {
        assertThatThrownBy(() -> bookService.getBooks(
                null, null, null,
                new BigDecimal("100.00"), new BigDecimal("50.00"),
                0, 10, "id", "asc"))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("minPrice cannot be greater than maxPrice");
    }

    @Test
    @DisplayName("getBooks - success with filtering and pagination")
    void getBooks_Success() {
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PageResponse<BookResponseDto> response = bookService.getBooks(
                "Clean", "Software Engineering", 1L,
                BigDecimal.ZERO, new BigDecimal("100.00"),
                0, 10, "id", "asc"
        );

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getTitle()).isEqualTo("Clean Code");
    }

    @Test
    @DisplayName("deleteBook - success")
    void deleteBook_Success() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        bookService.deleteBook(10L);

        verify(bookRepository).delete(book);
    }
}
