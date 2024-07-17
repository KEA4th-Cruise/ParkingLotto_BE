package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
}
