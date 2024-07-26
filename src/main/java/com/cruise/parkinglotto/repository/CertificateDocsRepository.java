package com.cruise.parkinglotto.repository;


import com.cruise.parkinglotto.domain.CertificateDocs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateDocsRepository extends JpaRepository<CertificateDocs, Long> {
    List<CertificateDocs> findByMemberId(Long memberId);
}
