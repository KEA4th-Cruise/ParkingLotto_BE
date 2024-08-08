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
    MEMBER_LOGIN_SUCCESS(HttpStatus.OK, "MEMBER2001", "로그인에 성공했습니다."),
    MEMBER_LOGOUT_SUCCESS(HttpStatus.OK, "MEMBER2002", "로그아웃에 성공했습니다."),
    MEMBER_INFO_FOUND(HttpStatus.OK, "MEMBER2003", "맴버 정보를 조회했습니다."),
    MEMBER_REFRESH_TOKEN_SUCCESS(HttpStatus.CREATED, "MEMBER2004", "토큰 재발급을 성공했습니다."),
    MEMBER_INFO_SAVED(HttpStatus.CREATED, "MEMBER2005", "해당 정보를 저장했습니다"),


    // 등록 관련 응답
    REGISTER_REQUEST_SUCCESS(HttpStatus.OK, "REGISTER2001", "등록 요청을 보냈습니다."),
    REGISTER_MEMBER_INFO_FOUND(HttpStatus.OK, "REGISTER2002", "멤버 세부 정보를 조회했습니다."),
    REGISTER_MEMBERS_FOUND(HttpStatus.OK, "REGISTER2003", "사용자 목록을 조회했습니다."),
    REGISTER_REQUEST_APPROVED(HttpStatus.OK, "REGISTER2004", "등록을 승인했습니다."),
    REGISTER_REQUEST_REJECTED(HttpStatus.OK, "REGISTER2005", "등록을 거절했습니다."),
    REGISTER_SEARCH_FOUND(HttpStatus.OK, "REGISTER2006", "검색을 완료했습니다."),

    // 추첨 관련
    DRAW_INFO_FOUND(HttpStatus.OK, "DRAW2001", "추첨 정보를 조회했습니다."),
    DRAW_EXECUTE_RESULT(HttpStatus.CREATED, "DRAW2002", "추첨결과가 정상적으로 저장되었습니다."),
    DRAW_INFO_SAVED(HttpStatus.CREATED, "DRAW2003", "추첨 생성 정보가 저장되었습니다."),
    DRAW_CREATION_CONFIRMED(HttpStatus.CREATED, "DRAW2004", "추첨 생성이 완료되었습니다."),
    CALCULATE_MEMBER_WEIGHT_COMPLETED(HttpStatus.OK, "DRAW2005", "계산이 완료되었습니다."),
    DRAW_SIMULATE_COMPLETED(HttpStatus.OK, "DRAW2006", "추첨 시뮬레이션이 완료되었습니다."),
    DRAW_OVERVIEW_FOUND(HttpStatus.OK, "DRAW2007", "추첨 개요 정보를 조회하였습니다."),
    DRAW_STATISTICS_FOUND(HttpStatus.OK, "DRAW2008", "추첨의 통계를 조회하였습니다."),
    DRAW_RESULT_EXCEL_DOWNLOADED(HttpStatus.OK, "DRAW2009", "추첨결과의 URL이 정상적으로 전송되었습니다."),
    DRAW_RESULT_FOUND(HttpStatus.OK, "DRAW2010", "추첨 결과를 조회했습니다."),
    DRAW_LIST_FOUND(HttpStatus.OK, "DRAW2011", "추첨 목록을 조회하였습니다."),
    DRAW_YEAR_LIST_FOUND(HttpStatus.OK, "DRAW2012", "추첨이 존재하는 연도 목록을 조회하였습니다."),
    DRAW_ADMIN_CANCEL(HttpStatus.OK, "DRAW2013", "강제 취소에 성공하였습니다."),
    DRAW_SELF_CANCEL(HttpStatus.OK, "DRAW2014", "취소에 성공하였습니다."),

    //  주차 구역 관련
    PARKING_SPACE_ADDED(HttpStatus.CREATED, "PARKINGSPACE2001", "해당 회차에 주차구역이 추가되었습니다."),
    PARKING_SPACE_INFO_FOUND(HttpStatus.OK, "PARKINGSPACE2002", "주차 공간 정보를 조회했습니다"),

    //  신청자 관련
    APPLICANT_LIST_FOUND(HttpStatus.OK, "APPLICANT2001", "신청자 목록을 조회하였습니다."),
    APPLICANT_PRIORITY_APPROVED(HttpStatus.CREATED, "APPLICANT2002", "해당 사용자에게 우대 신청 승인 및 주차 공간 배정을 완료했습니다."),
    APPLICANT_APPLY_SUCCESS(HttpStatus.CREATED, "APPLICANT2003", "신청에 성공했습니다."),
    APPLICANT_APPLY_INFO_FOUND(HttpStatus.OK, "APPLICANT2004", "해당 사용자의 특정 회차 추첨 결과 정보 조회에 성공했습니다"),
    APPLICANT_APPLY_LIST_FOUND(HttpStatus.OK, "APPLICANT2005", "사용자가 신청했던 회차 리스트 조회를 성공했습니다."),
    APPLICANT_SEARCH_FOUND(HttpStatus.OK, "APPLICANT2006", "신청자 검색을 완료했습니다."),
    WINNER_SEARCH_FOUND(HttpStatus.OK, "APPLICANT2007", "당첨자 검색을 완료했습니다."),
    APPLICANT_CANCEL_SUCCESS(HttpStatus.OK, "APPLICANT2008", "일반 취소 신청에 성공했습니다."),

    //  우대 신청자 관련
    PRIORITY_APPLICANT_LIST_FOUND(HttpStatus.OK, "PRIORITY2001", "우대 신청자 목록을 조회하였습니다."),
    PRIORITY_APPLICANT_APPROVED(HttpStatus.OK, "PRIORITY2002", "해당 사용자에게 우대 신청 승인을 완료했습니다."),
    PRIORITY_APPLICANT_DETAILS_FOUND(HttpStatus.OK, "PRIORITY2003", "해당 우대 신청자의 신청 정보를 조회하였습니다."),
    PRIORITY_APPLICANT_REJECTED(HttpStatus.OK, "PRIORITY2004", "해당 사용자의 우대신청을 거절하였습니다."),
    PRIORITY_APPLICANT_ASSIGNED(HttpStatus.OK, "PRIORITY2005", "승인 목록의 신청자들에게 주차구역 배정을 완료했습니다."),
    PRIORITY_APPLICANT_CANCEL_SUCCESS(HttpStatus.OK, "PRIORITY2006", "우대 취소 신청에 성공했습니다."),
    PRIORITY_APPLICANT_ASSIGN_CANCELED(HttpStatus.OK, "PRIORITY2007", "해당 우대 신청자의 승인 및 구역 배정을 취소하였습니다."),


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
