package com.project.backend.domain.book.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * -- Favorite엔티티의 DTO --
 *
 * @author -- 정재익 --
 * @since -- 1월 27일 --
 */
@Getter
@Setter
public class FavoriteDTO {

    @NonNull
    private Long memberId;

    @NonNull
    private String bookIsbn;
}