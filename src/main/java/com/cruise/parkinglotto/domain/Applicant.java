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

    @Column(nullable = false)
    private Double weightedTotalScore;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private WinningStatus winningStatus;

    private Long parkingSpaceId;

    @Column(nullable = false)
    private Integer reserveNum;    //  추첨대기 ( -1 ) / 당첨 ( 0 ) / 예비 ( 1 ~ )

    @Column(nullable = false)
    private Integer userSeedIndex;

    @Column(columnDefinition = "CHAR(1)", nullable = false)
    private String userSeed;

    private Double randomNumber;

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
    private Integer trafficCommuteTime;

    @Column(nullable = false)
    private Integer carCommuteTime;

    @Column(nullable = false)
    private Integer recentLossCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draw_id")
    private Draw draw;

    public void cancelWinningStatus() {
        this.winningStatus = WinningStatus.CANCELED;
        this.parkingSpaceId = null;
    }

}
