package com.bookstore.service;

import com.bookstore.dto.BookRequestDto;
import com.bookstore.dto.BookResponseDto;
import com.bookstore.dto.PageResponse;

import java.math.BigDecimal;

public interface BookService {

    BookResponseDto createBook(BookRequestDto requestDto);

    PageResponse<BookResponseDto> getBooks(
            String title,
            String genre,
            Long authorId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sortBy,
            String direction
    );

    BookResponseDto getBookById(Long id);

    BookResponseDto updateBook(Long id, BookRequestDto requestDto);

    void deleteBook(Long id);

    PageResponse<BookResponseDto> getBooksByAuthor(
            Long authorId,
            int page,
            int size,
            String sortBy,
            String direction
    );
}
