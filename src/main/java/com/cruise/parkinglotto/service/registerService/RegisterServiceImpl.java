package com.cruise.parkinglotto.service.registerService;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.enums.EnrollmentStatus;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.MemberRepository;
import com.cruise.parkinglotto.service.memberService.MemberService;
import com.cruise.parkinglotto.web.converter.RegisterConverter;
import com.cruise.parkinglotto.web.dto.registerDTO.RegisterResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @Override
    @Transactional
    public Object requestRegister(Member member) {
        int updatedCount = memberRepository.updateEnrollmentStatusToPending(member.getAccountId());
        if (updatedCount == 0) { // 등록 요청을 보냈는데 PENDING 으로 바뀌지 않은 경우
            throw new ExceptionHandler(ErrorStatus.REGISTER_REQUEST_FAILED);
        }
        return null;
    }

    @Override
    @Transactional
    public Object rejectRegister(Member member) {
        int updatedCount = memberRepository.updateEnrollmentStatusToPrepending(member.getAccountId());
        if (updatedCount == 0) { // 등록 거절을 했지만 prepending으로 바뀌지 않은 경우
            throw new ExceptionHandler(ErrorStatus.REGISTER_REFUSE_FAILED);
        }
        return null;
    }
  
    @Override
    @Transactional
    public Object approveRegister(Member member) {
        int updatedCount = memberRepository.updateEnrollmentStatusToEnrolled(member.getAccountId());
        if (updatedCount == 0) { // 관리자가 승인을 했는데 ENROLLED로 바뀌지 않은 경우
            throw new ExceptionHandler(ErrorStatus.REGISTER_APPROVE_FAILED);
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public RegisterResponseDTO.MemberInfoResponseDTO getMemberInfo(String accountId) {
        Member member = memberService.getMemberByAccountId(accountId);
        return RegisterConverter.toMemberInfoResponseDTO(member);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Member> getMembersByEnrollmentStatus(Integer page, EnrollmentStatus enrollmentStatus) {
        if (enrollmentStatus == EnrollmentStatus.PENDING || enrollmentStatus == EnrollmentStatus.ENROLLED) {
            return memberRepository.findByEnrollmentStatus(PageRequest.of(page, 6), enrollmentStatus);
        } else {
            throw new ExceptionHandler(ErrorStatus.REGISTER_MEMBERS_NOT_FOUND);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RegisterResponseDTO.MembersResponseDTO findMemberBySearchKeywordAndEnrollmentStatus(String searchKeyword, EnrollmentStatus enrollmentStatus) {
        if (enrollmentStatus == EnrollmentStatus.PREPENDING || enrollmentStatus == null) {
            throw new ExceptionHandler(ErrorStatus.REGISTER_MEMBERS_NOT_FOUND);
        }

        Optional<Member> memberByAccountId = memberRepository.findByAccountIdAndEnrollmentStatus(searchKeyword, enrollmentStatus);
        Optional<Member> memberByEmployeeNo = memberRepository.findByEmployeeNoAndEnrollmentStatus(searchKeyword, enrollmentStatus);

        if (memberByAccountId.isPresent()) { // 사원명으로 검색된 경우
            return RegisterConverter.toMembersResponseDTO(memberByAccountId.get());
        } else if (memberByEmployeeNo.isPresent()) { // 사번으로 검색된 경우
            return RegisterConverter.toMembersResponseDTO(memberByEmployeeNo.get());
        } else { // 아무것도 검색되지 않은 경우
            throw new ExceptionHandler(ErrorStatus.REGISTER_SEARCH_NOT_FOUND);
        }
    }

}
