package com.cruise.parkinglotto.repository;


import com.cruise.parkinglotto.domain.CertificateDocs;
import com.cruise.parkinglotto.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateDocsRepository extends JpaRepository<CertificateDocs, Long> {
    List<CertificateDocs> findByMemberId(Long memberId);
    void deleteByMember(Member member);
    void deleteAllByFileUrlIn(List<String> fileUrls);
}
