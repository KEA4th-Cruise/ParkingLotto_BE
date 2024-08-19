package com.cruise.parkinglotto.repository.querydsl;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface MemberCustomRepository {

    Page<Member> findByEnrollmentStatusAndKeyword(PageRequest pageRequest, String keyword, EnrollmentStatus enrollmentStatus);
}
