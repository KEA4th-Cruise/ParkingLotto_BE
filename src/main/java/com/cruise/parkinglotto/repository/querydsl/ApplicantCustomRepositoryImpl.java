package com.cruise.parkinglotto.repository.querydsl;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.QApplicant;
import com.cruise.parkinglotto.domain.QMember;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ApplicantCustomRepositoryImpl implements ApplicantCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Applicant> findApplicantByDrawIdAndKeyword(PageRequest pageRequest, String keyword, Long drawId) {
        QApplicant applicant = QApplicant.applicant;
        QMember member = QMember.member;

        BooleanExpression predicate = member.employeeNo.contains(keyword)
                .or(member.accountId.contains(keyword))
                .and(applicant.draw.id.eq(drawId));

        List<Applicant> applicants = jpaQueryFactory.selectFrom(applicant)
                .join(applicant.member, member)
                .where(predicate)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        int count = jpaQueryFactory.selectFrom(applicant)
                .join(applicant.member, member)
                .where(predicate)
                .fetch().size();

        return new PageImpl<>(applicants, pageRequest, count);
    }

    @Override
    public Page<Applicant> findWinnerByDrawIdAndKeyword(PageRequest pageRequest, String keyword, Long drawId) {
        QApplicant applicant = QApplicant.applicant;
        QMember member = QMember.member;

        BooleanExpression predicate = applicant.winningStatus.eq(WinningStatus.WINNER)
                .and(member.employeeNo.contains(keyword).or(member.accountId.contains(keyword)))
                .and(applicant.draw.id.eq(drawId));

        List<Applicant> applicants = jpaQueryFactory.selectFrom(applicant)
                .join(applicant.member, member)
                .where(predicate)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        int count = jpaQueryFactory.selectFrom(applicant)
                .join(applicant.member, member)
                .where(predicate)
                .fetch().size();

        return new PageImpl<>(applicants, pageRequest, count);
    }
}
