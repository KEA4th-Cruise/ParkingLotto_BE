package com.cruise.parkinglotto.repository.querydsl;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.QMember;
import com.cruise.parkinglotto.domain.enums.EnrollmentStatus;
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
public class MemberCustomRepositoryImpl implements MemberCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Member> findByEnrollmentStatusAndKeyword(PageRequest pageRequest, String keyword, EnrollmentStatus enrollmentStatus) {
        QMember member = QMember.member;

        BooleanExpression predicate = member.enrollmentStatus.eq(enrollmentStatus)
                        .and(member.accountId.contains(keyword)
                        .or(member.employeeNo.contains(keyword))
                        .or(member.nameKo.contains(keyword))
                        .or(member.deptPathName.contains(keyword)));

        List<Member> members = jpaQueryFactory.selectFrom(member)
                .where(predicate)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        int count = jpaQueryFactory.selectFrom(member)
                .where(predicate)
                .fetch().size();

        return new PageImpl<>(members, pageRequest, count);
    }

}
