package com.cruise.parkinglotto.global.response.code.status;


import com.cruise.parkinglotto.global.response.code.BaseCode;
import com.cruise.parkinglotto.global.response.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    // 일반적인 응답
    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),

    // 멤버 관련 응답
    MEMBER_LOGIN_SUCCESS(HttpStatus.OK, "MEMBER2001", "로그인 성공"),
    MEMBER_LOGOUT_SUCCESS(HttpStatus.OK, "MEMBER2002", "로그아웃 성공"),

    // 등록 관련 응답
    REGISTER_REQUEST_SUCCESS(HttpStatus.OK, "REGISTER2001", "등록 요청 성공"),
    REGISTER_MEMBER_INFO_FOUND(HttpStatus.OK, "REGISTER2002", "세부 정보 조회 성공"),
    REGISTER_MEMBERS_FOUND(HttpStatus.OK, "REGISTER2003", "사용자 목록 조회 성공"),
    REGISTER_REQUEST_APPROVED(HttpStatus.OK, "REGISTER2004", "등록 승인 성공"),
    REGISTER_REQUEST_REFUSED(HttpStatus.OK, "REGISTER2005", "등록 거절 성공"),
    REGISTER_SEARCH_FOUND(HttpStatus.OK, "REGISTER2006", "사용자 검색 성공"),

    // 추첨 관련
    DRAW_INFO_FOUND(HttpStatus.OK, "DRAW2001", "추첨 정보를 조회했습니다."),
    DRAW_EXECUTE_RESULT(HttpStatus.OK, "DRAW2002", "추첨결과가 정상적으로 저장되었습니다."),
    DRAW_INFO_SAVED(HttpStatus.OK, "DRAW2003", "추첨 생성 정보가 저장되었습니다."),
    DRAW_CREATION_CONFIRMED(HttpStatus.OK, "DRAW2004", "추첨 생성이 완료되었습니다."),
    CALCULATE_MEMBER_WEIGHT_COMPLETED(HttpStatus.OK, "DRAW2005", "계산이 완료되었습니다."),

    //  주차 구역 관련
    PARKING_SPACE_ADDED(HttpStatus.OK, "PARKINGSPACE2001", "해당 회차에 주차구역이 추가되었습니다."),
    PARKING_SPACE_INFO_FOUND(HttpStatus.OK, "PARKINGSPACE2002", "주차 공간 정보를 조회했습니다"),

    //  신청자 관련
    APPLICANT_LIST_FOUND(HttpStatus.OK, "APPLICANT2001", "신청자 목록을 조회하였습니다."),
    APPLICANT_APPLY_LIST_FOUND(HttpStatus.OK, "APPLICANT2003", "사용자가 신청했던 회차 리스트 조회를 성공했습니다."),

    //  우대 신청자 관련
    PRIORITY_APPLICANT_LIST_FOUND(HttpStatus.OK, "PRIORITY2001", "우대 신청자 목록을 조회하였습니다."),
    PRIORITY_APPLICANT_APPROVED(HttpStatus.OK, "PRIORITY2002", "해당 사용자에게 우대 신청 승인 및 주차 공간 배정을 완료했습니다."),


    //  가중치정보 관련
    WEIGHT_DETAIL_FOUND(HttpStatus.OK, "WEIGHTDETAIL2001", "가중치 정보를 조회했습니다."),

    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
