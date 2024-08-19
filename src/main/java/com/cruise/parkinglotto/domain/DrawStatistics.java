package com.cruise.parkinglotto.domain;

import com.cruise.parkinglotto.domain.common.BaseEntity;
import com.cruise.parkinglotto.domain.enums.WorkType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_draw_statistics")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DrawStatistics extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "draw_statistics_id")
    private Long id;

    @Column(nullable = false)
    private Double competitionRate;

    @Column(nullable = false)
    private Double applicantsWeightAvg;

    @Column(nullable = false)
    private Integer totalApplicants;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draw_id")
    private Draw draw;

    @Column(nullable = false)
    private Double trafficCommuteTimeAvg;

    @Column(nullable = false)
    private Double carCommuteTimeAvg;

    @Column(nullable = false)
    private Double distanceAvg;

    @Column(nullable = false)
    private Double recentLossCountAvg;

    @Column(nullable = false)
    private Double winnersWeightAvg;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private WorkType dominantWorkType;
}
