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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @Transactional(readOnly = true)
    public RegisterResponseDTO.MemberInfoResponseDTO getMemberInfo(String accountId) {
        Member member = memberService.getMemberByAccountId(accountId);
        return RegisterConverter.toMemberInfoResponseDTO(member);
    }

    @Override
    public List<RegisterResponseDTO.MembersResponseDTO> getMembersByEnrollmentStatus(EnrollmentStatus enrollmentStatus) {
        if (enrollmentStatus == EnrollmentStatus.PENDING || enrollmentStatus == EnrollmentStatus.ENROLLED) {
            List<Member> members = memberRepository.findByEnrollmentStatus(enrollmentStatus);
            return RegisterConverter.toMembersResponseDTOList(members);
        } else {
            throw new ExceptionHandler(ErrorStatus.REGISTER_MEMBERS_NOT_FOUND);
        }
    }

}
