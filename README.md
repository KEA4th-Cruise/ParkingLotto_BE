# ParkingLotto

---

## 🎰 프로젝트 개요
**Parking Lotto**의 백엔드 레포지토리입니다. 
- 로그인
  - 데이터베이스 내 존재하는 기존의 member 정보로 로그인할 수 있습니다. (회원가입이 따로 존재하지 않음)
- 
<details>
 <summary>프로젝트 구조</summary>
 
 ```
📁
.
├── build
├── build.gradle
├── gradle
├── gradlew
├── gradlew.bat
├── settings.gradle
└── src
    └── main
        ├── java
        │   └── com
        │       └── cruise
        │           └── parkinglotto
        │               ├── Application.java
        │               ├── domain
        │               │   ├── Applicant.java
        │               │   ├── CertificateDocs.java
        │               │   ├── Draw.java
        │               │   ├── DrawStatistics.java
        │               │   ├── Member.java
        │               │   ├── ParkingSpace.java
        │               │   ├── PriorityApplicant.java
        │               │   ├── WeightDetails.java
        │               │   ├── WeightSectionStatistics.java
        │               │   ├── common
        │               │   │   └── BaseEntity.java
        │               │   └── enums
        │               │       ├── AccountType.java
        │               │       ├── ApprovalStatus.java
        │               │       ├── DrawStatus.java
        │               │       ├── DrawType.java
        │               │       ├── EnrollmentStatus.java
        │               │       ├── WeightSection.java
        │               │       ├── WinningStatus.java
        │               │       └── WorkType.java
        │               ├── global
        │               │   ├── config
        │               │   │   ├── QuerydslConfig.java
        │               │   │   ├── RedisConfig.java
        │               │   │   ├── SchedulerConfig.java
        │               │   │   ├── SecurityConfig.java
        │               │   │   ├── SwaggerConfig.java
        │               │   │   └── webConfig
        │               │   │       ├── CorsConfig.java
        │               │   │       ├── OctetStreamReadMsgConverter.java
        │               │   │       └── WebConfig.java
        │               │   ├── excel
        │               │   │   ├── ByteArrayMultipartFile.java
        │               │   │   └── FileGeneration.java
        │               │   ├── exception
        │               │   │   ├── ExceptionAdvice.java
        │               │   │   ├── GeneralException.java
        │               │   │   └── handler
        │               │   │       └── ExceptionHandler.java
        │               │   ├── filter
        │               │   │   └── JwtAuthenticationFilter.java
        │               │   ├── jwt
        │               │   │   ├── JwtToken.java
        │               │   │   ├── JwtTokenValidationResult.java
        │               │   │   └── JwtUtils.java
        │               │   ├── kc
        │               │   │   ├── ObjectStorageConfig.java
        │               │   │   └── ObjectStorageService.java
        │               │   ├── mail
        │               │   │   ├── MailInfo.java
        │               │   │   └── MailType.java
        │               │   ├── response
        │               │   │   ├── ApiResponse.java
        │               │   │   └── code
        │               │   │       ├── BaseCode.java
        │               │   │       ├── BaseErrorCode.java
        │               │   │       ├── ErrorReasonDTO.java
        │               │   │       ├── ReasonDTO.java
        │               │   │       └── status
        │               │   │           ├── ErrorStatus.java
        │               │   │           └── SuccessStatus.java
        │               │   └── sse
        │               │       └── SseEmitters.java
        │               ├── repository
        │               │   ├── ApplicantRepository.java
        │               │   ├── CertificateDocsRepository.java
        │               │   ├── DrawRepository.java
        │               │   ├── DrawStatisticsRepository.java
        │               │   ├── MemberRepository.java
        │               │   ├── ParkingSpaceRepository.java
        │               │   ├── PriorityApplicantRepository.java
        │               │   ├── WeightDetailsRepository.java
        │               │   ├── WeightSectionStatisticsRepository.java
        │               │   └── querydsl
        │               │       ├── ApplicantCustomRepository.java
        │               │       ├── ApplicantCustomRepositoryImpl.java
        │               │       ├── MemberCustomRepository.java
        │               │       └── MemberCustomRepositoryImpl.java
        │               ├── service
        │               │   ├── applicantService
        │               │   │   ├── ApplicantService.java
        │               │   │   └── ApplicantServiceImpl.java
        │               │   ├── certificateDocsService
        │               │   │   ├── CertificateDocsService.java
        │               │   │   └── CertificateDocsServiceImpl.java
        │               │   ├── drawService
        │               │   │   ├── DrawService.java
        │               │   │   └── DrawServiceImpl.java
        │               │   ├── drawStatisticsService
        │               │   │   ├── DrawStatisticsService.java
        │               │   │   └── DrawStatisticsServiceImpl.java
        │               │   ├── mailService
        │               │   │   ├── MailService.java
        │               │   │   └── MailServiceImpl.java
        │               │   ├── memberService
        │               │   │   ├── MemberDetailService.java
        │               │   │   ├── MemberService.java
        │               │   │   └── MemberServiceImpl.java
        │               │   ├── parkingSpaceService
        │               │   │   ├── ParkingSpaceService.java
        │               │   │   └── ParkingSpaceServiceImpl.java
        │               │   ├── priorityApplicantService
        │               │   │   ├── PriorityApplicantService.java
        │               │   │   └── PriorityApplicantServiceImpl.java
        │               │   ├── redisService
        │               │   │   ├── RedisService.java
        │               │   │   └── RedisServiceImpl.java
        │               │   ├── registerService
        │               │   │   ├── RegisterService.java
        │               │   │   └── RegisterServiceImpl.java
        │               │   ├── weightDetailService
        │               │   │   ├── WeightDetailService.java
        │               │   │   └── WeightDetailServiceImpl.java
        │               │   └── weightSectionStatisticsService
        │               │       ├── WeightSectionStatisticsService.java
        │               │       └── WeightSectionStatisticsServiceImpl.java
        │               └── web
        │                   ├── controller
        │                   │   ├── DrawRestController.java
        │                   │   ├── DrawStatisticsRestController.java
        │                   │   ├── MemberRestController.java
        │                   │   ├── RegisterRestController.java
        │                   │   ├── SseController.java
        │                   │   └── WeightRestController.java
        │                   ├── converter
        │                   │   ├── ApplicantConverter.java
        │                   │   ├── CertificateDocsConverter.java
        │                   │   ├── DrawConverter.java
        │                   │   ├── DrawStatisticsConverter.java
        │                   │   ├── MailInfoConverter.java
        │                   │   ├── MemberConverter.java
        │                   │   ├── ParkingSpaceConverter.java
        │                   │   ├── PriorityApplicantConverter.java
        │                   │   ├── RegisterConverter.java
        │                   │   ├── WeightDetailConverter.java
        │                   │   └── WeightSectionConverter.java
        │                   └── dto
        │                       ├── applicantDTO
        │                       │   ├── ApplicantRequestDTO.java
        │                       │   └── ApplicantResponseDTO.java
        │                       ├── certificateDocsDTO
        │                       │   ├── CertificateDocsRequestDTO.java
        │                       │   └── CertificateDocsResponseDTO.java
        │                       ├── drawDTO
        │                       │   ├── DrawRequestDTO.java
        │                       │   ├── DrawResponseDTO.java
        │                       │   └── SimulationData.java
        │                       ├── drawStatisticsDTO
        │                       │   └── DrawStatisticsResponseDTO.java
        │                       ├── memberDTO
        │                       │   ├── MemberRequestDTO.java
        │                       │   └── MemberResponseDTO.java
        │                       ├── parkingSpaceDTO
        │                       │   ├── ParkingSpaceRequestDTO.java
        │                       │   └── ParkingSpaceResponseDTO.java
        │                       ├── priorityApplicantDTO
        │                       │   ├── PriorityApplicantRequestDTO.java
        │                       │   └── PriorityApplicantResponseDTO.java
        │                       ├── registerDTO
        │                       │   └── RegisterResponseDTO.java
        │                       └── weightDetailDTO
        │                           ├── WeightDetailRequestDTO.java
        │                           └── WeightDetailResponseDTO.java
        └── resources
             └── application.yml
 ```

</details>

---

## ERD
<img src="https://github.com/user-attachments/assets/efe2ccd5-fc14-4369-9390-65629a9a20c2"  alt="ERD" width="1000"/>

---
## 🚀 시작하기
### ⚙️ 개발환경
> JDK: 17  
> Framework: SpringBoot 3.2.7  
> ORM: JPA  
> DBMS: MySQL 8.0.38, Redis 7.2.5
> Build: Gradle

1. Cloning Repository
   ```bash
      git clone https://github.com/KEA4th-Cruise/ParkingLotto_BE.git
      cd ParkingLotto_BE
   ```

2. Set up Database(MySQL)
   데이터베이스 생성
   ```sql
      CREATE DATABASE your_database_name;
   ```
   데이터베이스 연결 설정 업데이트
   ```bash
      cd src/main/resources/application.yml
   ```
   ```YAML
      spring:
       datasource:
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://localhost:3306/YOUR_DATABASE_NAME
        username: YOUR_USERNAME
        password: YOUR_PASSWORD
   ```
3. Set up etc.
   ```bash
      cd src/main/resources/application.yml
   ```
   - Redis (JWT 관리 및 블랙리스트)
     ```YAML
        spring:
           data:
            redis:
              host: YOUR_REDIS_HOST
              port: 6379
     ```
   - Mail (등록 승인 여부, 당첨 결과,당첨 강제 취소 등의 알림을 메일로 전송)
     ```YAML
          mail:
            host: smtp.gmail.com
            port: 587
            username: YOUR_USERNAME
            password: YOUR_PASSWORD
            properties:
              mail:
                debug: true
                smtp.auth: true
                smtp.timeout: 60000
                smtp.starttls.enable: true
     ```
   - JWT Secret Key
     ```YAML
      jwt:
        secret: YOUR_JWT_SECRET
     ```
   - Cloud (파일 업르드)
     
     [Kakao Cloud 공식문서](https://docs.kakaocloud.com/service/bss/object-storage)
     ```YAML
        cloud:
          kc:
            s3:
              url: ${KC_ENDPOINT}
              bucket: ${KC_S3_BUCKET_NAME}
              path: # 파일이 업로드 될 폴더명
                parking-space-image: ${PARKING_SPACE_IMAGE}
                map-image: ${MAP_IMAGE}
                general-certificate-docs: ${GENERAL_CERTIFICATE_DOCS}
                priority-certificate-docs: ${PRIORITY_CERTIFICATE_DOCS}
                draw-result-docs: ${DRAW_RESULT_DOCS}
              region:
                static: ${KC_REGION}
              credentials:
                accessKey: ${KC_S3_ACCESS_KEY}
                secretKey: ${KC_S3_SECRET_ACCESS_KEY}
              project-id: ${KC_PROJECT_ID}
     ```

5. 의존성 설치 및 프로젝트 빌드
   ```bash 
      ./gradlew build (macOS)
      gradlew.bat build (window)
   ```

---

## 🗓️ 개발기간
24.07.01 ~ 24.08.23 (종료예정)


## 👥 팀원 및 역할
| Profile |         Name & Github          |               Role               |                                             Task                                             |
| :---: |:---------------------:|:--------------------------------:|:--------------------------------------------------------------------------------------------:|
| <img src="https://github.com/user-attachments/assets/873b868c-d364-438d-bcad-54940bb1c404"  alt="Member 1" width="150"/> | 이윤서(PL) <br> <a href="https://github.com/yseo14">yseo14</a> |  추첨 생성 및 관리, 조회   | task |
| <img src="https://github.com/user-attachments/assets/71cb9e15-2b7b-4776-80b5-409966ae6ad2" alt="Member 2" width="150"/> | 김성호 <br> <a href="https://github.com/opp-13">opp-13</a> |  추첨 신청 및 취소  | task |
| <img src="https://github.com/user-attachments/assets/5ae38b0e-c681-4b4d-a38c-5a315de198f1" alt="Member 3" width="150"/> | 신해철(PM) <br> <a href="https://github.com/haecheol-shin">haecheol-shin</a> | 로그인, 사용자 등록 및 관리   | task |
| <img src="https://github.com/user-attachments/assets/417aa85a-29de-416d-8ca2-d18b658225c1" alt="Member 4" width="150"/> | 이정균 <br> <a href="https://github.com/LeeJungKyun">LeeJungKyun</a> |  추첨 로직 구현 및 추첨 결과   | task |
| <img src="https://github.com/user-attachments/assets/433f3a92-d6b5-4dd3-a4d5-2296d1a9d856" alt="Member 5" width="150"/> | 최준범 <br> <a href="https://github.com/Junbum-hub">Junbum-hub</a> |  내 결과 보기 및 사용자 입력 정보 관리   | task |
---
