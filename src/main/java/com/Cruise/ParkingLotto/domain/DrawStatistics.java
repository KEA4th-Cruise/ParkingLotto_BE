package com.Cruise.ParkingLotto.domain;

import com.Cruise.ParkingLotto.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
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
    private Long totalApplicants;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draw_id")
    private Draw draw;
}
