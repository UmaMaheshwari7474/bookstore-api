package com.bookstore.service.impl;

import com.bookstore.dto.AuthorRequestDto;
import com.bookstore.dto.AuthorResponseDto;
import com.bookstore.dto.PageResponse;
import com.bookstore.entity.Author;
import com.bookstore.exception.DuplicateResourceException;
import com.bookstore.exception.InvalidRequestException;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.repository.AuthorRepository;
import com.bookstore.service.AuthorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class AuthorServiceImpl implements AuthorService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name", "email");

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public AuthorResponseDto createAuthor(AuthorRequestDto requestDto) {
        if (authorRepository.existsByEmail(requestDto.getEmail().trim())) {
            throw new DuplicateResourceException("Author", "email", requestDto.getEmail());
        }

        Author author = new Author();
        author.setName(requestDto.getName().trim());
        author.setEmail(requestDto.getEmail().trim());
        author.setBiography(requestDto.getBiography() != null ? requestDto.getBiography().trim() : null);

        Author savedAuthor = authorRepository.save(author);
        return mapToDto(savedAuthor);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AuthorResponseDto> getAllAuthors(int page, int size, String sortBy, String direction) {
        validatePaginationAndSorting(page, size, sortBy, direction);

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<Author> authorsPage = authorRepository.findAll(pageable);
        Page<AuthorResponseDto> dtoPage = authorsPage.map(this::mapToDto);

        return PageResponse.from(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorResponseDto getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", id));
        return mapToDto(author);
    }

    @Override
    public AuthorResponseDto updateAuthor(Long id, AuthorRequestDto requestDto) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", id));

        String email = requestDto.getEmail().trim();
        if (authorRepository.existsByEmailAndIdNot(email, id)) {
            throw new DuplicateResourceException("Author", "email", email);
        }

        author.setName(requestDto.getName().trim());
        author.setEmail(email);
        author.setBiography(requestDto.getBiography() != null ? requestDto.getBiography().trim() : null);

        Author updatedAuthor = authorRepository.save(author);
        return mapToDto(updatedAuthor);
    }

    @Override
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", id));
        authorRepository.delete(author);
    }

    private void validatePaginationAndSorting(int page, int size, String sortBy, String direction) {
        if (page < 0) {
            throw new InvalidRequestException("Page index cannot be negative");
        }
        if (size <= 0 || size > 100) {
            throw new InvalidRequestException("Page size must be between 1 and 100");
        }
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new InvalidRequestException(String.format("Invalid sort field '%s'. Allowed fields: %s",
                    sortBy, ALLOWED_SORT_FIELDS));
        }
        if (!"asc".equalsIgnoreCase(direction) && !"desc".equalsIgnoreCase(direction)) {
            throw new InvalidRequestException("Sort direction must be either 'asc' or 'desc'");
        }
    }

    private AuthorResponseDto mapToDto(Author author) {
        int bookCount = author.getBooks() != null ? author.getBooks().size() : 0;
        return new AuthorResponseDto(
                author.getId(),
                author.getName(),
                author.getEmail(),
                author.getBiography(),
                bookCount
        );
    }
}
