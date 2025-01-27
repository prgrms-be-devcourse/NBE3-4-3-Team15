package com.project.backend.domain.book.key;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class FavoriteId implements Serializable {

    private String memberId;

    private int bookId;

    /**
     * -- 복합키의 고유성을 판별하는 메소드 --
     * <p>
     * 여러명의 유저가 하나의 책을 추천할 수도 있고
     * 하나의 유저가 여러개의 책을 추천할 수도 있으므로
     * bookId, memberId 모두 두개다 유일성을 만족하지 못함
     * 다만 bookId와 memberId의 조합이 고유성을 가지고 있기 때문에 복합키로 설정
     *
     * @param -- Object o --
     * @return -- boolean --
     * @author -- 정재익 --
     * @since -- 1월 26일 --
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavoriteId that = (FavoriteId) o;
        return bookId == that.bookId && Objects.equals(memberId, that.memberId);
    }

    /**
     * -- 고유한 해쉬 코드를 생성하는 메소드 --
     * <p>
     * bookId와 memberId를 기반으로 고유한 해시 코드를 생성
     * JPA는 복합 키를 사용할때 해쉬 값을 사용하여 처리하기 때문
     *
     * @return -- int --
     * @author -- 정재익 --
     * @since -- 1월 26일 --
     */
    @Override
    public int hashCode() {
        return Objects.hash(bookId, memberId);
    }
}