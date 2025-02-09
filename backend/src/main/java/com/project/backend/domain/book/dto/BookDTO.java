package com.project.backend.domain.book.dto;

import lombok.*;

/**
 * -- Book엔티티의 DTO --
 *
 * @author -- 정재익 --
 * @since -- 1월 27일 --
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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

    private int favoriteCount;
}
