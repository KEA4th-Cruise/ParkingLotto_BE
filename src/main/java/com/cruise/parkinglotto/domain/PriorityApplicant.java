package com.cruise.parkinglotto.domain;

import com.cruise.parkinglotto.domain.common.BaseEntity;
import com.cruise.parkinglotto.domain.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_priority_applicants")
public class PriorityApplicant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "priority_applicant_id")
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private ApprovalStatus approvalStatus;

    private Long parkingSpaceId;

    @Column(length = 8)
    private String carNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draw_id")
    private Draw draw;

    public void approvePriority(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public void rejectPriorityApply(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
        this.parkingSpaceId = -1L;
    }

    public void assignParkingSpace(Long parkingSpaceId) {
        this.parkingSpaceId = parkingSpaceId;
        this.approvalStatus = ApprovalStatus.ASSIGNED;
    }

    public void cancelPriorityAssign() {
        this.approvalStatus = ApprovalStatus.CANCELED;
        this.parkingSpaceId = -1L;
    }
}
