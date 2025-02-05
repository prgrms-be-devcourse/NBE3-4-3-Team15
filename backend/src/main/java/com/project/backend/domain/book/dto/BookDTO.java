package com.project.backend.domain.book.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * -- Book엔티티의 DTO --
 *
 * @author -- 정재익 --
 * @since -- 1월 27일 --
 */
@Getter
@Setter
public class BookDTO {

    @NonNull
    private String title;

    @NonNull
    private String author;

    @NonNull
    private String description;

    @NonNull
    private String image;

    @NonNull
    private String isbn;

    @NonNull
    private int favoriteCount;
}
