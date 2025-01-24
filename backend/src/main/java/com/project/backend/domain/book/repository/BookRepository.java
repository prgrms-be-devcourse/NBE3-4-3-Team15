package com.project.backend.domain.book.repository;

import com.project.backend.domain.book.entitiy.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {
}
