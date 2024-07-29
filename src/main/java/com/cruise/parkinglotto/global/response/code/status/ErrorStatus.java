package com.cruise.parkinglotto.global.response.code.status;


import com.cruise.parkinglotto.global.response.code.BaseErrorCode;
import com.cruise.parkinglotto.global.response.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    _EMPTY_FIELD(HttpStatus.NO_CONTENT, "COMMON404", "입력 값이 누락되었습니다."),

    // 멤버 관련 응답
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4001", "사용자가 존재하지 않습니다"),
    MEMBER_PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST, "MEMBER4002", "비밀번호가 일치하지 않습니다."),

    // 등록 관련 응답
    REGISTER_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "REGISTER4001", "등록 요청에 실패했습니다."),
    REGISTER_MEMBERS_NOT_FOUND(HttpStatus.BAD_REQUEST, "REGISTER4002", "enrollmentStatus가 잘못되었습니다."),
    REGISTER_APPROVE_FAILED(HttpStatus.BAD_REQUEST, "REGISTER4003", "등록 요청 승인에 실패했습니다."),
    REGISTER_REFUSE_FAILED(HttpStatus.BAD_REQUEST, "REGISTER4004", "등록 요청 거절에 실패했습니다."),

    //신청자 관련 응답
    APPLICANT_NOT_FOUND(HttpStatus.NOT_FOUND, "APPLICANT4001", "신청자가 존재하지 않습니다"),

    // 주차 공간 관련 응답
    PARKING_SPACE_NOT_FOUND(HttpStatus.NOT_FOUND, "PARKINGSPACE4001", "주차 공간이 존재하지 않습니다"),
    NO_REMAIN_SLOTS(HttpStatus.BAD_REQUEST, "PARKINGSPACE4002", "남은 주차공간이 없습니다."),

    // 추첨 관련 응답
    DRAW_NOT_FOUND(HttpStatus.NOT_FOUND, "DRAW4001", "추첨이 존재하지 않습니다."),
    DRAW_NOT_READY(HttpStatus.NOT_FOUND, "DRAW4002", "아직 신청이 종료되지 않은 추첨입니다."),
    DRAW_ALREADY_EXECUTED(HttpStatus.NOT_FOUND, "DRAW4003", "이미 종료된 추첨입니다. 한 번 진행된 추첨은 다시 진행 될 수 없습니다."),


    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
