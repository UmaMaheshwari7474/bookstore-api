package com.bookstore.service.impl;

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
import com.bookstore.repository.BookSpecification;
import com.bookstore.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "title", "isbn", "price", "publishedDate", "genre", "stockQuantity"
    );

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @Override
    public BookResponseDto createBook(BookRequestDto requestDto) {
        String isbn = requestDto.getIsbn().trim();
        if (bookRepository.existsByIsbn(isbn)) {
            throw new DuplicateResourceException("Book", "isbn", isbn);
        }

        Author author = authorRepository.findById(requestDto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", requestDto.getAuthorId()));

        Book book = new Book();
        book.setTitle(requestDto.getTitle().trim());
        book.setIsbn(isbn);
        book.setDescription(requestDto.getDescription() != null ? requestDto.getDescription().trim() : null);
        book.setPrice(requestDto.getPrice());
        book.setPublishedDate(requestDto.getPublishedDate());
        book.setGenre(requestDto.getGenre() != null ? requestDto.getGenre().trim() : null);
        book.setStockQuantity(requestDto.getStockQuantity());
        book.setAuthor(author);

        Book savedBook = bookRepository.save(book);
        return mapToDto(savedBook);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponseDto> getBooks(
            String title,
            String genre,
            Long authorId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        validatePaginationAndSorting(page, size, sortBy, direction);
        validatePriceFilter(minPrice, maxPrice);

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Specification<Book> specification = BookSpecification.filterBooks(title, genre, authorId, minPrice, maxPrice);

        Page<Book> booksPage = bookRepository.findAll(specification, pageable);
        Page<BookResponseDto> dtoPage = booksPage.map(this::mapToDto);

        return PageResponse.from(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        return mapToDto(book);
    }

    @Override
    public BookResponseDto updateBook(Long id, BookRequestDto requestDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        String isbn = requestDto.getIsbn().trim();
        if (bookRepository.existsByIsbnAndIdNot(isbn, id)) {
            throw new DuplicateResourceException("Book", "isbn", isbn);
        }

        Author author = authorRepository.findById(requestDto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", requestDto.getAuthorId()));

        book.setTitle(requestDto.getTitle().trim());
        book.setIsbn(isbn);
        book.setDescription(requestDto.getDescription() != null ? requestDto.getDescription().trim() : null);
        book.setPrice(requestDto.getPrice());
        book.setPublishedDate(requestDto.getPublishedDate());
        book.setGenre(requestDto.getGenre() != null ? requestDto.getGenre().trim() : null);
        book.setStockQuantity(requestDto.getStockQuantity());
        book.setAuthor(author);

        Book updatedBook = bookRepository.save(book);
        return mapToDto(updatedBook);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        bookRepository.delete(book);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponseDto> getBooksByAuthor(
            Long authorId,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        if (!authorRepository.existsById(authorId)) {
            throw new ResourceNotFoundException("Author", "id", authorId);
        }

        validatePaginationAndSorting(page, size, sortBy, direction);

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<Book> booksPage = bookRepository.findByAuthorId(authorId, pageable);
        Page<BookResponseDto> dtoPage = booksPage.map(this::mapToDto);

        return PageResponse.from(dtoPage);
    }

    private void validatePriceFilter(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidRequestException("minPrice cannot be negative");
        }
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidRequestException("maxPrice cannot be negative");
        }
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new InvalidRequestException("minPrice cannot be greater than maxPrice");
        }
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

    private BookResponseDto mapToDto(Book book) {
        Long authorId = book.getAuthor() != null ? book.getAuthor().getId() : null;
        String authorName = book.getAuthor() != null ? book.getAuthor().getName() : null;

        return new BookResponseDto(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getDescription(),
                book.getPrice(),
                book.getPublishedDate(),
                book.getGenre(),
                book.getStockQuantity(),
                authorId,
                authorName
        );
    }
}
