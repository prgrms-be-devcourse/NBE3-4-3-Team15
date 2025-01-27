package com.project.backend.domain.book.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class BookDto {

    @NonNull
    private int id;

    @NonNull
    private String title;

    @NonNull
    private String author;

    @NonNull
    private String description;

    @NonNull
    private String image;

    @NonNull
    private int favoriteCount;
}
