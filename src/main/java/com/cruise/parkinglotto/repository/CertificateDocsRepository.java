package com.cruise.parkinglotto.repository;


import com.cruise.parkinglotto.domain.CertificateDocs;
import com.cruise.parkinglotto.domain.Member;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CertificateDocsRepository extends JpaRepository<CertificateDocs, Long> {

    void deleteAllByMemberIdAndDrawId(Long memberId, Long drawId);

    @Query("select c from CertificateDocs c where c.member.id = :memberId and c.drawId = :drawId")
    Optional<List<CertificateDocs>> findCertificateDocsByMemberIdAndDrawId(@Param("memberId") Long memberId, @Param("drawId") Long drawId);

    List<CertificateDocs> findByMemberAndDrawId(Member member, Long drawId);
}
