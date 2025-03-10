package com.project.backend.domain.challenge.entry.dto;

import com.project.backend.domain.challenge.entry.entity.Entry;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EntryDto {
    private long id;
    private long challengeId;
    private long memberId;
    private long deposit;
    private long totalDeposit;
    private boolean isActive;
    private long refundAmount;
    private long rewardAmount;
    private boolean refunded;
    private double rate;

    public EntryDto(Entry entry) {
        this.id = entry.getId();
        this.challengeId = entry.getChallenge().getId();
        this.memberId = entry.getMember().getId();
        this.deposit = entry.getDeposit();
        this.totalDeposit = entry.getChallenge().getTotalDeposit();
        this.isActive = entry.isActive();
        this.refundAmount = entry.getRefundAmount();
        this.rewardAmount = entry.getRewardAmount();
        this.refunded = entry.isRefunded();
        this.rate = entry.getRate();
    }
}
