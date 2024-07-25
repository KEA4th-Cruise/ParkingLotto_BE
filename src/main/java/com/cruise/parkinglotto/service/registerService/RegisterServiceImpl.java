package com.cruise.parkinglotto.service.registerService;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Object requestRegister(Member member) {
        int updatedCount = memberRepository.updateEnrollmentStatusToPending(member.getAccountId());
        if (updatedCount == 0) { // 등록 요청을 보냈는데 PENDING 으로 바뀌지 않은 경우
            throw new ExceptionHandler(ErrorStatus.REGISTER_REQUEST_FAILED);
        }
        return null;
    }
}
