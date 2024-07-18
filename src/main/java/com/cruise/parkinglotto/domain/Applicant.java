package com.cruise.parkinglotto.domain;

import com.cruise.parkinglotto.domain.common.BaseEntity;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import com.cruise.parkinglotto.domain.enums.WorkType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Applicant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "applicant_id")
    private Long id;

    @Column(nullable = false)
    private Double weightedTotalScore;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private WinningStatus winningStatus;

    private Long parkingSpaceId;

    @Column(nullable = false)
    private Long reserveNum;    //  추첨대기 ( -1 ) / 당첨 ( 0 ) / 예비 ( 1 ~ )

    @Column(nullable = false)
    private Long userSeedIndex;

    @Column(nullable = false)
    private String userSeed;

    private String randomNumber;

    @Column(nullable = false)
    private Long firstChoice;

    @Column(nullable = false)
    private Long secondChoice;

    @Column(nullable = false)
    private Double distance;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private WorkType workType;

    @Column(nullable = false)
    private Long trafficCommuteTime;

    @Column(nullable = false)
    private Long carCommuteTime;

    @Column(nullable = false)
    private Long recentLossCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draw_id")
    private Draw draw;

    public void cancelWinningStatus() {
        this.winningStatus = WinningStatus.CANCELED;
    }

}
