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
    _UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED,"COMMON405" , "권한이 없습니다."),

    // 멤버 관련 응답
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4001", "사용자가 존재하지 않습니다"),
    MEMBER_PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST, "MEMBER4002", "비밀번호가 일치하지 않습니다."),
    MEMBER_REFRESH_TOKEN_NULL(HttpStatus.FORBIDDEN, "MEMBER4003", "리프레시 토큰 값이 비워져 있습니다."),
    MEMBER_REFRESH_TOKEN_BLACKLIST(HttpStatus.FORBIDDEN, "MEMBER4004", "블랙리스트로 등록된 리프레시 토큰 입니다."),
    MEMBER_REFRESH_TOKEN_EXPIRED(HttpStatus.FORBIDDEN, "MEMBER4005", "만료된 리프레시 토큰 입니다."),
    MEMBER_REFRESH_TOKEN_INVALID(HttpStatus.FORBIDDEN, "MEMBER4006", "유효한 리프레시 토큰이 아닙니다."),


    // 등록 관련 응답
    REGISTER_REQUEST_FAILED(HttpStatus.CONFLICT, "REGISTER4001", "등록 요청에 실패했습니다."),
    REGISTER_MEMBERS_NOT_FOUND(HttpStatus.BAD_REQUEST, "REGISTER4002", "등록 상태가 잘못되었습니다."),
    REGISTER_APPROVE_FAILED(HttpStatus.CONFLICT, "REGISTER4003", "등록 요청 승인에 실패했습니다."),
    REGISTER_REFUSE_FAILED(HttpStatus.CONFLICT, "REGISTER4004", "등록 요청 거절에 실패했습니다."),
    REGISTER_SEARCH_NOT_FOUND(HttpStatus.OK, "REGISTER4005", "검색 결과가 없습니다."),
    REGISTER_INVALID_ENROLLMENTSTATUS(HttpStatus.BAD_REQUEST, "REGISTER4006", "유효한 등록 상태가 아닙니다."),

    // 신청자 관련 응답
    APPLICANT_NOT_FOUND(HttpStatus.NOT_FOUND, "APPLICANT4001", "신청자가 존재하지 않습니다"),
    APPLICANT_CERT_DOCUMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "APPLICANT4002", "신청자의 서류 정보가 존재하지 않습니다"),
    APPLICANT_DUPLICATED_APPLY(HttpStatus.CONFLICT, "APPLICANT4003", "중복된 신청입니다"),
    APPLICANT_PARKING_SPACE_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "APPLICANT4004", "신청자의 주차공간 id 가 존재하지 않습니다"),
    APPLICANT_SEARCH_NOT_FOUND(HttpStatus.OK, "APPLICANT4005", "검색 결과가 없습니다."),
    WINNER_SEARCH_NOT_FOUND(HttpStatus.OK, "APPLICANT4006", "당첨자 검색 결과가 없습니다."),
    APPLICANT_NOT_WINNING_STATUS(HttpStatus.FORBIDDEN, "APPLICANT4007", "당첨되지 않았습니다."),
    APPROVED_APPLICANTS_NOT_EXIST(HttpStatus.BAD_REQUEST, "APPLICANT4008", "승인된 신청자가 존재하지 않습니다."),
    APPLICANT_NOT_ASSIGNED(HttpStatus.BAD_REQUEST,"APPLICANT4009", "배정이 완료된 신청자가 아닙니다."),

    // 주차 공간 관련 응답
    PARKING_SPACE_NOT_FOUND(HttpStatus.BAD_REQUEST, "PARKINGSPACE4001", "주차 공간이 존재하지 않습니다"),
    NO_REMAIN_SLOTS(HttpStatus.BAD_REQUEST, "PARKINGSPACE4002", "남은 주차공간이 없습니다."),

    // 추첨 관련 응답
    DRAW_NOT_FOUND(HttpStatus.NOT_FOUND, "DRAW4001", "추첨이 존재하지 않습니다."),
    DRAW_NOT_READY(HttpStatus.FORBIDDEN, "DRAW4002", "아직 신청이 종료되지 않은 추첨입니다."),
    DRAW_ALREADY_EXECUTED(HttpStatus.FORBIDDEN, "DRAW4003", "이미 종료된 추첨입니다. 한 번 진행된 추첨은 다시 진행 될 수 없습니다."),
    DRAW_STATISTICS_NOT_EXIST(HttpStatus.NOT_FOUND, "DRAW4004", "추첨 통계가 존재하지 않습니다."),
    DRAW_NOT_IN_APPLY_PERIOD(HttpStatus.FORBIDDEN, "DRAW4005", "추첨 기간이 아닙니다."),
    DRAW_SEED_NOT_FOUND(HttpStatus.NOT_FOUND, "DRAW4006", "생성된 시드가 없습니다."),
    DRAW_MISMATCH(HttpStatus.BAD_REQUEST,"DRAW4007","알맞은 drawId를 전송해주세요."),

    //사용자 가중치 관련
    WEIGHTDETAILS_TOO_LONG_USER_SEED(HttpStatus.BAD_REQUEST, "WEIGHTDETAILS4001", "유효한 신청자의 랜덤 시드는 문자 1개입니다"),
    WEIGHTDETAILS_NOT_FOUND(HttpStatus.NOT_FOUND, "WEIGHTDETAILS4002", "가중치 정보를 찾지 못했습니다"),

    //  파일 업로드 관련
    FILE_NAME_NOT_FOUND(HttpStatus.BAD_REQUEST, "FILE4001", "업로드한 문서의 이름이 없습니다."),
    FILE_NAME_TOO_LONG(HttpStatus.BAD_REQUEST, "FILE4002", "업로드한 문서의 이름 너무 깁니다."),
    FILE_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "FILE4003", "업로드한 문서의 전체 크기가 너무 큽니다."),
    FILE_FORMAT_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "FILE4001", "업로드한 파일의 형식이 잘못 되었습니다."),
    FILE_NAME_DUPLICATED(HttpStatus.CONFLICT, "FILE4005", "업로드한 파일 이름이 중복됩니다."),
    FILE_TOO_MANY(HttpStatus.PAYLOAD_TOO_LARGE, "FILE4006", "업로드한 파일이 너무 많습니다."),

    // 추첨 디테일 관련 응답
    WORK_TYPE_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBERDETAIL4001", "근무타입이 입력되지 않았습니다."),
    ADDRESS_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBERDETAIL4002", "주소가 입력되지 않았습니다."),

    // 메일 관련 응답
    MAIL_INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "MAIL4001", "유효하지 않은 이메일 주소입니다."),

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
                .build();
    }
}
