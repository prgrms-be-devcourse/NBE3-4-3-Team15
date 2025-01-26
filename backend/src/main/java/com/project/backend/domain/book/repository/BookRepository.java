package com.project.backend.domain.book.repository;

import com.project.backend.domain.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    boolean existsByIsbn(String isbn);
}
