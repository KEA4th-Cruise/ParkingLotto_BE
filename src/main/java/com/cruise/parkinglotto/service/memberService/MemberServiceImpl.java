package com.cruise.parkinglotto.service.memberService;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.jwt.JwtToken;
import com.cruise.parkinglotto.global.jwt.JwtUtils;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.MemberRepository;
import com.cruise.parkinglotto.service.redisService.RedisService;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberRequestDTO;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberResponseDTO;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtils jwtUtils;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인 로직
     * 로그인 할 때 주차 추첨 서비스에 등록이 되어있는지 확인해야 한다.
     * 등록이 되어있으면 -> 토큰 발급
     * 등록이 안되어있으면 -> 등록하라고 알려줘야 한다.
     * 로그인 성공 시 JWT 토큰이 생성된다.
     * 한번 발급한 토큰은 토큰 블랙리스트에 저장한다.
     * 로그인 실패 시 에러 발생
     */
    @Override
    public MemberResponseDTO.LoginResponseDTO login(MemberRequestDTO.LoginRequestDTO loginRequestDTO) {

        Member member = getMemberByAccountId(loginRequestDTO.getAccountId());

        // 비밀번호 일치 검증
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), member.getPassword())) {
            throw new ExceptionHandler(ErrorStatus.MEMBER_PASSWORD_NOT_MATCHED);
        }

        // 블랙리스트에서 해당 토큰 값이 있는지 검증

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDTO.getAccountId(), loginRequestDTO.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 토큰 생성
        JwtToken jwtToken = jwtUtils.generateToken(authentication);

        // 7일간 refresh token을 redis에 저장
        redisService.setValues(loginRequestDTO.getAccountId(), jwtToken.getRefreshToken(), Duration.ofDays(7));

        // 등록이 된 사용자인지 아닌지 여부 넘겨줌
        return MemberResponseDTO.LoginResponseDTO.builder()
                .jwtToken(jwtToken)
                .enrollmentStatus(member.getEnrollmentStatus())
                .build();
        }

    /**
     * 로그아웃 로직
     * 로그아웃 성공 시 redis에서 refresh token 삭제
     */
    @Override
    public MemberResponseDTO.LogoutResponseDTO logout(MemberRequestDTO.LogoutRequestDTO logoutRequestDTO) {

        // redis에서 refresh token 삭제
        redisService.deleteValues(logoutRequestDTO.getAccountId());

        // 블랙리스트에 저장
        redisService.setBlackList(logoutRequestDTO.getAccessToken(), logoutRequestDTO.getAccessToken());
        redisService.setBlackList(logoutRequestDTO.getRefreshToken(), logoutRequestDTO.getRefreshToken());

        return MemberResponseDTO.LogoutResponseDTO.builder()
                .logoutAt(LocalDateTime.now())
                .build();
    }

    @Override
    public Member getMemberByAccountId(String accountId) {
        return memberRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

}

