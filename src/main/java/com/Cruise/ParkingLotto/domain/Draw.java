package com.Cruise.ParkingLotto.domain;

import com.Cruise.ParkingLotto.domain.common.BaseEntity;
import com.Cruise.ParkingLotto.domain.enums.DrawStatus;
import com.Cruise.ParkingLotto.domain.enums.DrawType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Draw extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "draw_id")
    private Long id;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private DrawType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime drawStartAt;

    @Column(nullable = false)
    private LocalDateTime drawEndAt;

    @Column(nullable = false)
    private LocalDateTime usageStartAt;

    @Column(nullable = false)
    private LocalDateTime usageEndAt;

    private String seedNum;

    private String description;

    @Column(nullable = false)
    private String mapImageUrl;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private DrawStatus status;

    @Column(nullable = false)
    private Long totalSlots;

    @OneToMany(mappedBy = "draw", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Applicant> applicantList;

    @OneToOne(mappedBy = "draw", fetch = FetchType.LAZY)
    private DrawStatistics drawStatistics;

    @OneToMany(mappedBy = "draw", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeightSectionStatistics> weightSectionStatisticsList;

    @OneToMany(mappedBy = "draw", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParkingSpace> parkingSpaceList;
}
