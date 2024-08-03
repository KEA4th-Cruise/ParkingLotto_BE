package com.cruise.parkinglotto.domain;

import com.cruise.parkinglotto.domain.common.BaseEntity;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import com.cruise.parkinglotto.domain.enums.WorkType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_applicants")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Applicant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "applicant_id")
    private Long id;

    private Double weightedTotalScore;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private WinningStatus winningStatus;

    private Long parkingSpaceId;

    @Column(nullable = false)
    private Integer reserveNum;    //  추첨대기 ( -1 ) / 당첨 ( 0 ) / 예비 ( 1 ~ )

    private Integer userSeedIndex;

    @Column(columnDefinition = "CHAR(1)")
    private String userSeed;

    private Double randomNumber;

    private Long firstChoice;

    private Long secondChoice;

    private Double distance;

    @Enumerated(value = EnumType.STRING)
    private WorkType workType;

    private Integer trafficCommuteTime;

    private Integer carCommuteTime;

    private Integer recentLossCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draw_id")
    private Draw draw;

    public void cancelWinningStatus() {
        this.winningStatus = WinningStatus.CANCELED;
    }

    public void approveParkingSpaceToPriority(Long parkingSpaceId, WinningStatus winningStatus, Integer reserveNum) {
        this.parkingSpaceId = parkingSpaceId;
        this.winningStatus = winningStatus;
        this.reserveNum = reserveNum;
    }

    public void assignRandomNumber(double randomNumber) {
        this.randomNumber = randomNumber;
    }

    public void updateTotalWeightedScore(double weightedTotalScore) {
        this.weightedTotalScore = weightedTotalScore;
    }

    public void updateUserSeedIndex(Integer userSeedIndex) {
        this.userSeedIndex = userSeedIndex;
    }

    public void updateReserveNum(Integer reserveNum) {
        this.reserveNum = reserveNum;
    }

    public void updateReserve(Long parkingSpaceId, Integer reserveNum, WinningStatus winningStatus) {
        this.parkingSpaceId = parkingSpaceId;
        this.reserveNum = reserveNum;
        this.winningStatus = winningStatus;
    }

    public void updateWinningStatus(WinningStatus winningStatus) {
        this.winningStatus = winningStatus;
    }
}
