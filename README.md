# ParkingLotto
---
## File Tree
├── Application.java
├── domain
│   ├── Applicant.java
│   ├── CertificateDocs.java
│   ├── Draw.java
│   ├── DrawStatistics.java
│   ├── Member.java
│   ├── ParkingSpace.java
│   ├── WeightSectionStatistics.java
│   ├── common
│   │   └── BaseEntity.java
│   └── enums
│       ├── AccountType.java
│       ├── DrawStatus.java
│       ├── DrawType.java
│       ├── EnrollmentStatus.java
│       ├── WeightSection.java
│       ├── WinningStatus.java
│       └── WorkType.java
├── global
│   ├── exception
│   │   ├── ExceptionAdvice.java
│   │   ├── GeneralException.java
│   │   └── handler
│   │       └── ExceptionHandler.java
│   └── response
│       ├── ApiResponse.java
│       └── code
│           ├── BaseCode.java
│           ├── BaseErrorCode.java
│           ├── ErrorReasonDTO.java
│           ├── ReasonDTO.java
│           └── status
│               ├── ErrorStatus.java
│               └── SuccessStatus.java
├── repository
├── service
├── tree.md
└── web
    ├── controller
    ├── converter
    └── dto
