package com.project.backend.domain.challenge.entry.dto;

import com.project.backend.domain.challenge.entry.entity.Entry;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 환급 DTO
 *
 * @author 손진영
 * @since 2025년 3월 4일
 */
@Getter
@NoArgsConstructor
public class RefundsDto {
    private long entryId;
    private String challengeName;
    private long refundAmount;
    private boolean refunded;

    public RefundsDto(Entry entry) {
        this.entryId = entry.getId();
        this.challengeName = entry.getChallenge().getName();
        this.refundAmount = entry.getRefundAmount();
        this.refunded = entry.isRefunded();
    }
}
