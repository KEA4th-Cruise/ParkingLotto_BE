package com.cruise.parkinglotto.domain;


import com.cruise.parkinglotto.domain.common.BaseEntity;
import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberRequestDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_weight_details")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeightDetails extends BaseEntity {
    @Id
    @Column(name = "weight_details_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String address;

    @Enumerated(value = EnumType.STRING)
    private WorkType workType;

    private Integer trafficCommuteTime;

    private Integer carCommuteTime;

    private Double distance;

    private Integer recentLossCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void updateWeightDetailsInApply(String address, WorkType workType, Integer trafficCommuteTime, Integer carCommuteTime, Double distance){
        this.address = address;
        this.workType = workType;
        this.trafficCommuteTime = trafficCommuteTime;
        this.carCommuteTime = carCommuteTime;
        this.distance = distance;
    }

    public void updateMyInfo(MemberRequestDTO.MyInfoRequestDTO myInfoRequestDTO) {
        this.address = myInfoRequestDTO.getAddress();
        this.workType = myInfoRequestDTO.getWorkType();
        this.member.updateCarNum(myInfoRequestDTO.getCarNum());
    }

}
