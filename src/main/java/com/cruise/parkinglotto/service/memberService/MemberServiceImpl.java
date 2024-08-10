package com.cruise.parkinglotto.service.memberService;

import com.cruise.parkinglotto.domain.CertificateDocs;
import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.WeightDetails;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.jwt.JwtToken;
import com.cruise.parkinglotto.global.jwt.JwtUtils;
import com.cruise.parkinglotto.global.kc.ObjectStorageConfig;
import com.cruise.parkinglotto.global.kc.ObjectStorageService;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.CertificateDocsRepository;
import com.cruise.parkinglotto.repository.MemberRepository;
import com.cruise.parkinglotto.repository.WeightDetailsRepository;
import com.cruise.parkinglotto.service.certificateDocsService.CertificateDocsService;
import com.cruise.parkinglotto.service.redisService.RedisService;
import com.cruise.parkinglotto.web.converter.CertificateDocsConverter;
import com.cruise.parkinglotto.web.converter.MemberConverter;
import com.cruise.parkinglotto.web.converter.WeightDetailConverter;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberRequestDTO;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtils jwtUtils;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final CertificateDocsService certificateDocsService;
    private final ObjectStorageService objectStorageService;
    private final ObjectStorageConfig objectStorageConfig;
    private final WeightDetailsRepository weightDetailsRepository;
    private final CertificateDocsRepository certificateDocsRepository;

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
    @Transactional(readOnly = true)
    public MemberResponseDTO.LoginResponseDTO login(MemberRequestDTO.LoginRequestDTO loginRequestDTO) {

        Member member = getMemberByAccountId(loginRequestDTO.getAccountId());

        // 비밀번호 일치 검증
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), member.getPassword())) {
            throw new ExceptionHandler(ErrorStatus.MEMBER_PASSWORD_NOT_MATCHED);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDTO.getAccountId(), loginRequestDTO.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 토큰 생성
        JwtToken jwtToken = jwtUtils.generateToken(authentication);

        // 7일간 refresh token을 redis에 저장
        redisService.setValues(jwtToken.getRefreshToken(), loginRequestDTO.getAccountId() , Duration.ofDays(7));

        return MemberConverter.toLoginResponseDTO(member, jwtToken);
    }

    /**
     * 로그아웃 로직
     * 로그아웃 성공 시 redis에서 refresh token 삭제
     */
    @Override
    @Transactional(readOnly = true)
    public MemberResponseDTO.LogoutResponseDTO logout(String refreshToken) {

        // redis에서 refresh token 삭제
        redisService.deleteValues(refreshToken);

        // 블랙리스트에 저장
        redisService.setBlackList(refreshToken, refreshToken);

        return MemberConverter.toLogoutResponseDTO();
    }

    @Override
    public MemberResponseDTO.RefreshResponseDTO recreateToken(String refreshToken) {

        if (refreshToken == null) {
            throw new ExceptionHandler(ErrorStatus.MEMBER_REFRESH_TOKEN_NULL);
        }

        log.info("refreshToken: {}", refreshToken);

        String blacklistValue = redisService.getBlackList(refreshToken);
        if (!(blacklistValue == null)) {
            log.info("blacklistValue: {}", blacklistValue);
            throw new ExceptionHandler(ErrorStatus.MEMBER_REFRESH_TOKEN_BLACKLIST);
        }

        String accountIdFromRedis = redisService.getValues(refreshToken);
        String accountIdFromRequest = jwtUtils.parseClaims(refreshToken).getSubject();

        if (accountIdFromRedis != null) { // redis에 값이 있다면
            if (accountIdFromRedis.equals(accountIdFromRequest)) { // redis 값과 refresh token의 accountId가 일치하는 경우
                // redis에 저장되어 있는 accountId를 가져와서 authentication를 생성
                Authentication authentication = jwtUtils.getAuthentication(refreshToken);
                return MemberConverter.toRefreshResponseDTO(jwtUtils.generateAccessToken(authentication));
            } else {
                throw new ExceptionHandler(ErrorStatus.MEMBER_REFRESH_TOKEN_INVALID);
            }
        } else {
            throw new ExceptionHandler(ErrorStatus.MEMBER_REFRESH_TOKEN_EXPIRED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Member getMemberByAccountId(String accountId) {
        return memberRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    @Override
    @Transactional
    public MemberResponseDTO.MyInfoResponseDTO saveMyInfo(Long memberId, MemberRequestDTO.MyInfoRequestDTO myInfoRequestDTO, List<MultipartFile> certificateDocs) {

        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));

        //파일 검증 ==========
        //한개의 추첨에서 들어갈 수 있는 파일 개수 세는 변수
        Integer totalFileNumber = 0;

        //업로드 할 파일 검증 (길이, 확장자 등)
        if (certificateDocs != null) {
            certificateDocsService.validateCertificateFiles(certificateDocs);
            totalFileNumber += certificateDocs.size();
        }


        if (totalFileNumber > 5) {
            throw new ExceptionHandler(ErrorStatus.FILE_TOO_MANY);
        }

        //파일 검증 끝 =========

        List<CertificateDocs> certificateDocsList = new ArrayList<>();

        WeightDetails weightDetails = WeightDetailConverter.toWeightDetails(myInfoRequestDTO, findMember);
        weightDetailsRepository.save(weightDetails);
        if (certificateDocs != null) {
            for (MultipartFile certificateDoc : certificateDocs) {
                String certificateDocUrl = objectStorageService.uploadObject(objectStorageConfig.getGeneralCertificateDocument(), findMember.getId() + "_" + certificateDoc.getOriginalFilename(), certificateDoc);
                CertificateDocs certificateDocument = CertificateDocsConverter.toCertificateDocument(certificateDocUrl, certificateDoc.getOriginalFilename(), findMember, -1L);
                certificateDocsList.add(certificateDocument);
                certificateDocsRepository.save(certificateDocument);
            }
        }


        return MemberConverter.toMyInfoResponseDTO(findMember, weightDetails, certificateDocsList);

    }

    @Override
    @Transactional
    public MemberResponseDTO.MyInfoResponseDTO updateMyInfo(Long memberId, MemberRequestDTO.MyInfoRequestDTO myInfoRequestDTO, List<MultipartFile> certificateDocs) {

        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));

        //파일 검증 ==========

        //한개의 추첨에서 들어갈 수 있는 파일 개수 세는 변수
        Integer totalFileNumber = 0;

        //업로드 할 파일 검증 (길이, 확장자 등)
        if (certificateDocs != null) {
            certificateDocsService.validateCertificateFiles(certificateDocs);
            totalFileNumber += certificateDocs.size();
        }

        if (totalFileNumber > 5) {
            throw new ExceptionHandler(ErrorStatus.FILE_TOO_MANY);
        }

        //파일 검증 끝 =========

        List<CertificateDocs> certificateDocsList = new ArrayList<>();

        certificateDocsRepository.deleteAllByMemberIdAndDrawId(memberId,-1L);

        WeightDetails findWeightDetails = weightDetailsRepository.findOptionalByMemberId(memberId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.WEIGHTDETAILS_NOT_FOUND));
        findWeightDetails.updateMyInfo(myInfoRequestDTO);

        if (certificateDocs != null) {
            for (MultipartFile certificateDoc : certificateDocs) {
                if(certificateDoc != null) {
                    String certificateDocUrl = objectStorageService.uploadObject(objectStorageConfig.getGeneralCertificateDocument(), findMember.getId() + "_" + certificateDoc.getOriginalFilename(), certificateDoc);
                    CertificateDocs certificateDocument = CertificateDocsConverter.toCertificateDocument(certificateDocUrl, certificateDoc.getOriginalFilename(), findMember, -1L);
                    certificateDocsList.add(certificateDocument);
                    certificateDocsRepository.save(certificateDocument);
                }
            }
        }

        return MemberConverter.toMyInfoResponseDTO(findMember, findWeightDetails, certificateDocsList);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponseDTO.MyInfoResponseDTO getMyInfo(Long memberId) {

        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));
        List<CertificateDocs> certificateDocs = certificateDocsRepository.findCertificateDocsByMemberIdAndDrawId(memberId,-1L).orElseThrow(() -> new ExceptionHandler(ErrorStatus.APPLICANT_CERT_DOCUMENT_NOT_FOUND));
        return MemberConverter.toMyInfoResponseDTO(findMember,findMember.getWeightDetails(), certificateDocs );

    }

}

