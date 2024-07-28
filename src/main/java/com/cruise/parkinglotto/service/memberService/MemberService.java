package com.cruise.parkinglotto.service.memberService;

import com.cruise.parkinglotto.web.dto.memberDTO.MemberRequestDTO;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

    // 로그인 로직
    MemberResponseDTO.LoginResponseDTO login(MemberRequestDTO.LoginRequestDTO loginRequestDTO);

    // 로그아웃 로직
    void logout(String accountId);

    Long getMemberIdByAccountId(String accountId);

}
