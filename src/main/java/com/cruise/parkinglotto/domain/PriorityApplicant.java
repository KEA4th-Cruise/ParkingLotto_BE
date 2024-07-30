package com.cruise.parkinglotto.domain;

import com.cruise.parkinglotto.domain.common.BaseEntity;
import com.cruise.parkinglotto.domain.enums.ApprovalStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_priority_applicants")
public class PriorityApplicant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "priority_applicant_id")
    private Long id;

    private ApprovalStatus approvalStatus;

    private Long parkingSpaceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draw_id")
    private Draw draw;
}
