package com.cruise.parkinglotto.domain;

import com.cruise.parkinglotto.domain.common.BaseEntity;
import com.cruise.parkinglotto.domain.enums.DrawStatus;
import com.cruise.parkinglotto.domain.enums.DrawType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_draws")
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

    @Column(nullable = false, length = 20)
    private String title;

    @Column(nullable = false)
    private LocalDateTime drawStartAt;

    @Column(nullable = false)
    private LocalDateTime drawEndAt;

    @Column(nullable = false)
    private LocalDateTime usageStartAt;

    @Column(nullable = false)
    private LocalDateTime usageEndAt;

    @Column(columnDefinition = "TEXT")
    private String seedNum;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 255)
    private String mapImageUrl;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private DrawStatus status;

    @Column(nullable = false)
    private Integer totalSlots;

    @Column(columnDefinition = "CHAR(4)", nullable = false)
    private String year;

    @Column(columnDefinition = "CHAR(1)", nullable = false)
    private String quarter;

    @ColumnDefault("false")
    private Boolean confirmed;

    @OneToMany(mappedBy = "draw", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Applicant> applicantList;

    @OneToOne(mappedBy = "draw", fetch = FetchType.LAZY)
    private DrawStatistics drawStatistics;

    @OneToMany(mappedBy = "draw", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeightSectionStatistics> weightSectionStatisticsList;

    @OneToMany(mappedBy = "draw", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParkingSpace> parkingSpaceList;

    public void updateConfirmed(Boolean confirmed, Integer totalSlots) {
        this.confirmed = confirmed;
        this.totalSlots = totalSlots;
    }
}
