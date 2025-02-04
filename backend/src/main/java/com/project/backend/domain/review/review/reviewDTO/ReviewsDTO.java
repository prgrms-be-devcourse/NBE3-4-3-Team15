package com.project.backend.domain.review.review.reviewDTO;

import com.project.backend.domain.member.dto.MemberDto;
import com.project.backend.domain.review.comment.dto.ReviewCommentDto;
import com.project.backend.domain.review.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


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
    List<ReviewCommentDto>  reviewCommentDtos;
    Set<MemberDto> memberDtos;


    public ReviewsDTO(Review review){
        this.id=review.getId();
        this.bookId = review.getBookId();
        this.memberId= review.getMemberId();
        this.content = review.getContent();
        this.rating = review.getRating();
        this.reviewCommentDtos = review.getComments().stream()
                .map(ReviewCommentDto::new)
                .collect(Collectors.toList());
        this.memberDtos = review.getRecommendMember().stream()
                .map(MemberDto::new)
                .collect(Collectors.toSet());
    }
}
