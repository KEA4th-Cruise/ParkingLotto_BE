package com.cruise.parkinglotto.domain;

import com.cruise.parkinglotto.domain.common.BaseEntity;
import com.cruise.parkinglotto.domain.enums.AccountType;
import com.cruise.parkinglotto.domain.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tb_members")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 18)
    private String nameKo;

    @Column(nullable = false, length = 20)
    private String accountId;

    @Column(nullable = false,  length = 10)
    private String employeeNo;

    @Column(nullable = false, length = 15)
    private String deptPathName;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountType accountType;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private EnrollmentStatus enrollmentStatus;

    @Column(length = 8)
    private String carNum;

    private LocalDate deleteAt;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CertificateDocs> certificateDocsList;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private WeightDetails weightDetails;
}
