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
import java.util.Objects;
import java.util.stream.Collectors;


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
        redisService.setValues(loginRequestDTO.getAccountId(), jwtToken.getRefreshToken(), Duration.ofDays(7));

        // 등록이 된 사용자인지 아닌지 여부 넘겨줌

        return MemberConverter.toLoginResponseDTO(member, jwtToken);
    }


    /**
     * 로그아웃 로직
     * 로그아웃 성공 시 redis에서 refresh token 삭제
     */
    @Override
    @Transactional(readOnly = true)
    public MemberResponseDTO.LogoutResponseDTO logout(MemberRequestDTO.LogoutRequestDTO logoutRequestDTO) {

        // redis에서 refresh token 삭제
        redisService.deleteValues(logoutRequestDTO.getAccountId());

        // 블랙리스트에 저장
        redisService.setBlackList(logoutRequestDTO.getRefreshToken(), logoutRequestDTO.getRefreshToken());

        return MemberConverter.toLogoutResponseDTO();
    }

    @Override
    public MemberResponseDTO.RefreshResponseDTO refreshToken(JwtToken jwtToken) {

        String refreshToken = jwtToken.getRefreshToken();
        if (refreshToken == null) {
            throw new ExceptionHandler(ErrorStatus.MEMBER_REFRESH_TOKEN_NULL);
        }

        String blacklistValue = redisService.getBlackList(refreshToken);
        if (!blacklistValue.equals("false")) {
            throw new ExceptionHandler(ErrorStatus.MEMBER_REFRESH_TOKEN_BLACKLIST);
        }

        // 만료된 access token을 활용하여 authentication을 가져옴
        Authentication authentication = jwtUtils.getAuthentication(jwtToken.getAccessToken());

        // accountId 필요함 -> redis에 있는 Refresh token이 유효한지 확인하기 위해서
        String accountId = authentication.getName();

        // 유효하다면, authentication으로 새로운 토큰을 발급
        String values = redisService.getValues(accountId);
        if (values != null) { // redis에 값이 있다면
            if (values.equals(jwtToken.getRefreshToken())) { // redis 값과 refresh token 값이 일치하는 경우
                return MemberConverter.toRefreshResponseDTO(jwtUtils.generateAccessToken(authentication));
            } else {
                throw new ExceptionHandler(ErrorStatus.MEMBER_REFRESH_TOKEN_FAILED);
            }
        }

        throw new ExceptionHandler(ErrorStatus.MEMBER_REFRESH_TOKEN_FAILED);
    }

    @Override
    @Transactional(readOnly = true)
    public Member getMemberByAccountId(String accountId) {
        return memberRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    @Override
    @Transactional
    public MemberResponseDTO.MyInfoResponseDTO saveMyInfo(Long memberId, MemberRequestDTO.MyInfoRequestDTO myInfoRequestDTO, List<MultipartFile> certificateDocs)  {

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
            throw new ExceptionHandler(ErrorStatus.CERTIFICATEDOCS_TOO_MANY);
        }

        //파일 검증 끝 =========

        List<CertificateDocs> certificateDocsList = new ArrayList<>();

        WeightDetails findWeightDetails = weightDetailsRepository.findOptionalByMemberId(memberId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.WEIGHTDETAILS_NOT_FOUND));
        findWeightDetails.updateMyInfo(myInfoRequestDTO);
        if(certificateDocs != null) {
            for(MultipartFile certificateDoc : certificateDocs) {
                String certificateDocUrl = objectStorageService.uploadObject(objectStorageConfig.getGeneralCertificateDocument(), findMember.getId()+"_"+certificateDoc.getOriginalFilename(), certificateDoc);
                CertificateDocs certificateDocument = CertificateDocsConverter.toCertificateDocument(certificateDocUrl, certificateDoc.getOriginalFilename(), findMember, -1L);
                certificateDocsList.add(certificateDocument);
                certificateDocsRepository.save(certificateDocument);
            }
        }


        return MemberConverter.toMyInfoResponseDTO(findWeightDetails.getMember(), findWeightDetails, certificateDocsList);

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
            throw new ExceptionHandler(ErrorStatus.CERTIFICATEDOCS_TOO_MANY);
        }

        // 파일 검증 끝 =========
        List<CertificateDocs> certificateDocsList = new ArrayList<>();
        List<CertificateDocs> updateCertificationDocsList = new ArrayList<>();

        // 이부분에서 입력한 가중치 정보로 업데이트
        WeightDetails findWeightDetails = weightDetailsRepository.findOptionalByMemberId(memberId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.WEIGHTDETAILS_NOT_FOUND));
        findWeightDetails.updateMyInfo(myInfoRequestDTO);

        // 여기서 사용자가 원래 있던 파일들중 남겨둔 파일의 DB id를 따로 저장
        List<Long> certificateIds = myInfoRequestDTO.getMyCertificationInfoRequests().stream().map((c) -> c.getCertificateDocsId()).collect(Collectors.toList());

        if (certificateDocs != null) {
            for (CertificateDocs certificateDoc : findMember.getCertificateDocsList()) {

                // DB 에 있는 certificate 를 조회한 후 거기있는 id 에 사용자가 보낸 id 가 포함되면 업데이트
                if (certificateIds.contains(certificateDoc.getId())) {
                    updateCertificationDocsList.add(certificateDoc);
                    MemberRequestDTO.MyCertificationInfoRequestDTO myCertificationInfoRequestDTO = myInfoRequestDTO.getMyCertificationInfoRequests().get(certificateIds.indexOf(certificateDoc.getId()));
                    MultipartFile certificateDocument = certificateDocs.get(certificateIds.indexOf(certificateDoc.getId()));
                    if (Objects.equals(certificateDocument.getOriginalFilename(), myCertificationInfoRequestDTO.getFileName())) {
                        objectStorageService.deleteObject(certificateDoc.getFileUrl());
                        String certificateDocUrl = objectStorageService.uploadObject(objectStorageConfig.getGeneralCertificateDocument(), findMember.getId() + "_" + certificateDocument.getOriginalFilename(), certificateDocument);
                        certificateDoc.updateCertificateDocs(certificateDocUrl, certificateDocument.getOriginalFilename());
                        certificateDocsList.add(certificateDoc);
                        certificateIds.remove(certificateDoc.getId());
                        certificateDocs.remove(certificateDocument);
                        List<MemberRequestDTO.MyCertificationInfoRequestDTO> myCertificationInfoRequests = myInfoRequestDTO.getMyCertificationInfoRequests();
                        myCertificationInfoRequests.remove(myCertificationInfoRequestDTO);
                    } else {
                        // 업로드 할거같은 파일의 이름과 따로 DTO 에 저장되어있던 파일이름 일치않하면 예외
                        throw new ExceptionHandler(ErrorStatus.CERTIFICATEDOCS_NAME_DIFFERENT);
                    }
                } else {
                    // 여기는 사용자가 삭제하는 경우이므로 스토리지에서도 제거
                    objectStorageService.deleteObject(certificateDoc.getFileUrl());
                }
            }
            // 여기서 Member 와 연관된 문서 싹다 지우고 업데이트
            findMember.updateCertificationDocs(updateCertificationDocsList);

            // 나머지 사용자가 새로넣은 파일들을 새로 DB에 넣고 스토리지에 저장
            for (MultipartFile certificateDoc : certificateDocs) {
                String certificateDocUrl = objectStorageService.uploadObject(objectStorageConfig.getGeneralCertificateDocument(), findMember.getId() + "_" + certificateDoc.getOriginalFilename(), certificateDoc);
                CertificateDocs certificateDocument = CertificateDocsConverter.toCertificateDocument(certificateDocUrl, certificateDoc.getOriginalFilename(), findMember, -1L);
                certificateDocsList.add(certificateDocument);
                certificateDocsRepository.save(certificateDocument);
            }
        }

        return MemberConverter.toMyInfoResponseDTO(findWeightDetails.getMember(), findWeightDetails, certificateDocsList);

    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponseDTO.MyInfoResponseDTO getMyInfo(Long memberId) {

        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));
        return MemberConverter.toMyInfoResponseDTO(findMember, findMember.getWeightDetails(), findMember.getCertificateDocsList());

    }

}

