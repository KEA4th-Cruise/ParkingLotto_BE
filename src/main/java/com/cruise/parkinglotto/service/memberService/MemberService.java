package com.cruise.parkinglotto.service.memberService;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberRequestDTO;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberResponseDTO;
import org.springframework.transaction.annotation.Transactional;


public interface MemberService {

    // 사용자의 accountId로 멤버 객체를 가져오는 메서드
    Member getMemberByAccountId(String accountId);

    // 로그인
    MemberResponseDTO.LoginResponseDTO login(MemberRequestDTO.LoginRequestDTO loginRequestDTO);

    // 로그아웃
    MemberResponseDTO.LogoutResponseDTO logout(MemberRequestDTO.LogoutRequestDTO logoutRequestDTO);


    // 내가 입력한 정보 불러오기
    MemberResponseDTO.MyInfoResponseDTO getMyInfo(Long memberId);
}
