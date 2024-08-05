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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtUtils jwtUtils;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // 타입 캐스팅
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 요청으로부터 JWT 토큰을 추출
        String token = jwtUtils.resolveToken(httpRequest);

        if (token != null) {
            // JWT 토큰의 유효성을 검증
            JwtTokenValidationResult validationResult = jwtUtils.validateToken(token);

            if (validationResult.isValid()) {
                // 토큰이 유효한 경우, 토큰에서 Authentication 객체를 생성하여 SecurityContextHolder에 설정
                Authentication authentication = jwtUtils.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // 토큰이 유효하지 않은 경우, 401 Unauthorized 응답을 반환
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, validationResult.getErrorMessage());
                return;
            }
        }

        chain.doFilter(request, response);
    }

}

