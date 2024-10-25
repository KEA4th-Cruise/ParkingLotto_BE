package com.cruise.parkinglotto.domain;

import com.cruise.parkinglotto.domain.common.BaseEntity;
import com.cruise.parkinglotto.domain.enums.AccountType;
import com.cruise.parkinglotto.domain.enums.EnrollmentStatus;
import com.cruise.parkinglotto.domain.enums.WorkType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "tb_members")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity implements UserDetails {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 18)
    private String nameKo;

    @Column(nullable = false, length = 20)
    private String accountId;

    @Column(nullable = false, length = 10)
    private String employeeNo;

    @Column(nullable = false, length = 15)
    private String deptPathName;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus enrollmentStatus;

    @Column(length = 8)
    private String carNum;

    private LocalDate deletedAt;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CertificateDocs> certificateDocsList;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountType accountType;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private WorkType workType;

    @Column(nullable = false, length = 50)
    private String address;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(accountType.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.accountId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private WeightDetails weightDetails;

    public void updateCarNum(String carNum) {
        this.carNum = carNum;
    }
}
