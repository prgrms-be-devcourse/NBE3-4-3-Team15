package com.project.backend.domain.book.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NonNull
    private String title;

    @NonNull
    private String author;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NonNull
    private String image;

    @NonNull
    private String isbn;
}