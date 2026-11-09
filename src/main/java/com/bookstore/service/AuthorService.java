package com.bookstore.service;

import com.bookstore.dto.AuthorRequestDto;
import com.bookstore.dto.AuthorResponseDto;
import com.bookstore.dto.PageResponse;

public interface AuthorService {

    AuthorResponseDto createAuthor(AuthorRequestDto requestDto);

    PageResponse<AuthorResponseDto> getAllAuthors(int page, int size, String sortBy, String direction);

    AuthorResponseDto getAuthorById(Long id);

    AuthorResponseDto updateAuthor(Long id, AuthorRequestDto requestDto);

    void deleteAuthor(Long id);
}
