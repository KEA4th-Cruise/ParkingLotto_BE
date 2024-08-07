package com.cruise.parkinglotto.domain;

import com.cruise.parkinglotto.domain.common.BaseEntity;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "tb_parking_spaces")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParkingSpace extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parking_space_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 50)
    private String address;

    @Column(nullable = false)
    private Integer slots;

    @Column(nullable = false)
    private Integer remainSlots;

    @Column(nullable = false, length = 255)
    private String floorPlanImageUrl;

    private Integer applicantCount;

    @ColumnDefault("false")
    private Boolean confirmed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draw_id")
    private Draw draw;

    public void updateConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public void updateApplicantCount() {
        this.applicantCount = this.applicantCount + 1;
    }

    public void decreaseApplicantCount() {
        this.applicantCount = this.applicantCount - 1;
    }

    public void decrementSlots() {
        if (this.remainSlots > 0) {
            this.remainSlots--;
        } else {
            throw new ExceptionHandler(ErrorStatus.NO_REMAIN_SLOTS);
        }
    }

    public void confirmParkingSpace() {
        this.confirmed = true;
    }
}
