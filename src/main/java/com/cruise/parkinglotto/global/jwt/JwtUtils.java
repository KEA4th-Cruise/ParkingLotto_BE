package com.cruise.parkinglotto.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtils {

    private final Key key;

    // application.yml에서 secret 값 가져와서 key에 저장
    public JwtUtils(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // Jwt토큰을 생성하는 메서드
    public JwtToken generateToken(Authentication authentication) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        log.info("authentication.getName() = {}", authentication.getName());

        // Access Token 생성
        Long now = new Date().getTime();
        Date accessTokenExpiresIn = new Date(now + 86400000); // 1일
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .setAudience("domain.com") // 해당 부분은 도메인이 나온 후에 제대로 작성
                .setIssuedAt(new Date())
                .setIssuer("parkinglotto")
                .setExpiration(accessTokenExpiresIn)
                .claim("auth", authorities)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        Date refreshTokenExpiresIn = new Date(now + 86400000L * 7); // 7일
        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpiresIn) // 일주일
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        // Jwt 토큰 복호화

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        log.info("claims: {}", claims);

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication return
        // UserDetails: interface, User: UserDetails를 구현한 class
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 정보를 검증하는 메서드
    public JwtTokenValidationResult validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return new JwtTokenValidationResult(true, "Valid JWT Token"); // 토큰 유효성 검사 성공
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
            return new JwtTokenValidationResult(false, "Invalid JWT Token");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            return new JwtTokenValidationResult(false, "Expired JWT Token");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            return new JwtTokenValidationResult(false, "Unsupported JWT Token");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
            return new JwtTokenValidationResult(false, "JWT claims string is empty.");
        }
    }

    // 요청에서 토큰을 뽑는 메서드
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰에서 claim을 뽑는 메서드
    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // httpServletRequest에서 멤버의 accountId를 뽑는 메서드
    public String getAccountIdFromRequest(HttpServletRequest httpServletRequest) {

        String token = resolveToken(httpServletRequest);
        Authentication authentication = getAuthentication(token);
        return authentication.getName();
    }
}