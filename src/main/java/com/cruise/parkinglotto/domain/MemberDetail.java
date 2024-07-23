package com.cruise.parkinglotto.domain;


import com.cruise.parkinglotto.domain.common.BaseEntity;
import com.cruise.parkinglotto.domain.enums.WorkType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_member_details")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetail extends BaseEntity {
    @Id
    @Column(name = "member_details_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String address;

    @Enumerated(value = EnumType.STRING)
    private WorkType workType;

    @Column(nullable = false)
    private Integer trafficCommuteTime;

    @Column(nullable = false)
    private Integer carCommuteTime;

    @Column(nullable = false)
    private Double distance;

    @Column(nullable = false)
    private Integer recentLossCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
