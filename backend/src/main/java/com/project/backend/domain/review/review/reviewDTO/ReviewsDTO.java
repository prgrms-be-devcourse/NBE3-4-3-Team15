package com.project.backend.domain.review.review.reviewDTO;

import com.project.backend.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 리뷰DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewsDTO {
    Integer id;
    String bookId;
    String memberId;
    String content;
    Integer rating;
//    Integer recommendCount;
}
