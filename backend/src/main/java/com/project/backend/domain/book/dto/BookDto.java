package com.project.backend.domain.book.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDto {

    private int id;

    private String title;

    private String author;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String image;
}
