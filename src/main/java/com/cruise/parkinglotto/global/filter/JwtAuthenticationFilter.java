package com.cruise.parkinglotto.global.filter;

import com.cruise.parkinglotto.global.jwt.JwtTokenValidationResult;
import com.cruise.parkinglotto.global.jwt.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtUtils jwtUtils;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // 타입 캐스팅
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 현재 요청의 경로를 얻어온다.
        String path = new UrlPathHelper().getPathWithinApplication(httpRequest);

        // 인증이 필요하지 않은 경로에 대해 필터를 건너뛴다.
        if ("/api/members/login".equals(path) ||
                "/api/members/logout".equals(path) ||
                "/api/members/refresh".equals(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 요청으로부터 JWT 토큰을 추출
        String token = jwtUtils.resolveToken(httpRequest); // acccess token 검사

        log.info("token: {}", token);

        if (token == null) {
            // accessToken이 null인 경우 401 Unauthorized 응답을 반환하고 종료
            log.info("Access token is missing or expired");
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Access token is missing or expired");
            return;  // 필터 체인 실행 중단
        }

        // JWT 토큰의 유효성을 검증
        JwtTokenValidationResult validationResult = jwtUtils.validateToken(token);

        if (validationResult.isValid()) {
            // 토큰이 유효한 경우, 토큰에서 Authentication 객체를 생성하여 SecurityContextHolder에 설정
            Authentication authentication = jwtUtils.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            // 토큰이 유효하지 않은 경우 401 Unauthorized 응답을 반환하고 종료
            log.info("Invalid token: {}", validationResult.getErrorMessage());
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: " + validationResult.getErrorMessage());
            return;  // 필터 체인 실행 중단
        }

        // 필터 체인의 다음 단계로 진행
        chain.doFilter(httpRequest, httpResponse);
    }

}

