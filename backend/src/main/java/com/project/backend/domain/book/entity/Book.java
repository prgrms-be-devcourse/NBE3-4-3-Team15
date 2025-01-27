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

    private String title;

    private String author;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String image;

    @NonNull
    private String isbn;

    private int favoriteCount;

}