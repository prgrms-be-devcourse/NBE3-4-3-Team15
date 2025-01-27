package com.project.backend.domain.book.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class FavoriteDTO {

    @NonNull
    private String memberId;

    @NonNull
    private int bookId;
}
