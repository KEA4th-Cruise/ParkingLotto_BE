package com.Cruise.ParkingLotto.domain;

import com.Cruise.ParkingLotto.domain.common.BaseEntity;
import com.Cruise.ParkingLotto.domain.enums.AccountType;
import com.Cruise.ParkingLotto.domain.enums.EnrollmentStatus;
import com.Cruise.ParkingLotto.domain.enums.WorkType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nameKo;

    @Column(nullable = false)
    private String accountId;

    @Column(nullable = false)
    private String employeeNo;

    @Column(nullable = false)
    private String deptPathName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountType accountType;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private EnrollmentStatus enrollmentStatus;

    private String carNum;

    private String address;

    @Enumerated(value = EnumType.STRING)
    private WorkType workType;

    private LocalDate deleteDate;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CertificateDocs> certificateDocsList;
}
