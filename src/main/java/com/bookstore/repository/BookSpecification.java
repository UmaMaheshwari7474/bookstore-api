package com.bookstore.repository;

import com.bookstore.entity.Book;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BookSpecification {

    private BookSpecification() {
        // Utility class private constructor
    }

    public static Specification<Book> filterBooks(String title, String genre, Long authorId,
                                                  BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + title.trim().toLowerCase() + "%"
                ));
            }

            if (genre != null && !genre.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("genre")),
                        genre.trim().toLowerCase()
                ));
            }

            if (authorId != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("author").get("id"),
                        authorId
                ));
            }

            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("price"),
                        minPrice
                ));
            }

            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("price"),
                        maxPrice
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
