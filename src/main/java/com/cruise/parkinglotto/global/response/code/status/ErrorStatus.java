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
    MEMBER_NOT_FOUND(HttpStatus.OK, "MEMBER4001", "사용자가 존재하지 않습니다"),
    MEMBER_PASSWORD_NOT_MATCHED(HttpStatus.OK, "MEMBER4002", "비밀번호가 일치하지 않습니다."),
    MEMBER_REFRESH_TOKEN_NULL(HttpStatus.OK, "MEMBER4003", "리프레시 토큰 값이 비워져 있습니다."),
    MEMBER_REFRESH_TOKEN_BLACKLIST(HttpStatus.OK, "MEMBER4004", "블랙리스트로 등록된 리프레시 토큰 입니다."),
    MEMBER_REFRESH_TOKEN_FAILED(HttpStatus.OK, "MEMBER4005", "유효한 리프레시 토큰이 아닙니다."),


    // 등록 관련 응답
    REGISTER_REQUEST_FAILED(HttpStatus.OK, "REGISTER4001", "등록 요청에 실패했습니다."),
    REGISTER_MEMBERS_NOT_FOUND(HttpStatus.OK, "REGISTER4002", "등록 상태가 잘못되었습니다."),
    REGISTER_APPROVE_FAILED(HttpStatus.OK, "REGISTER4003", "등록 요청 승인에 실패했습니다."),
    REGISTER_REFUSE_FAILED(HttpStatus.OK, "REGISTER4004", "등록 요청 거절에 실패했습니다."),
    REGISTER_SEARCH_NOT_FOUND(HttpStatus.OK, "REGISTER4005", "검색 결과가 없습니다."),
    REGISTER_INVALID_ENROLLMENTSTATUS(HttpStatus.OK, "REGISTER4006", "유효하지 않은 상태입니다."),

    // 신청자 관련 응답
    APPLICANT_NOT_FOUND(HttpStatus.OK, "APPLICANT4001", "신청자가 존재하지 않습니다"),
    APPLICANT_CAR_NUM_NOT_FOUND(HttpStatus.OK, "APPLICANT4002", "신청자의 차량번호 정보가 존재하지 않습니다"),
    APPLICANT_CERT_DOCUMENT_NOT_FOUND(HttpStatus.OK, "APPLICANT4003", "신청자의 서류 정보가 존재하지 않습니다"),
    APPLICANT_ADDRESS_NOT_FOUND(HttpStatus.OK, "APPLICANT4004", "신청자의 거주지 주소가 존재하지 않습니다"),
    APPLICANT_WORK_TYPE_NOT_FOUND(HttpStatus.OK, "APPLICANT4005", "신청자의 근무 정보가 존재하지 않습니다"),
    APPLICANT_SEED_NOT_FOUND(HttpStatus.OK, "APPLICANT4006", "신청자의 랜덤 시드가 존재하지 않습니다"),
    APPLICANT_DUPLICATED_APPLY(HttpStatus.OK, "APPLICANT4007", "중복된 신청입니다"),
    APPLICANT_PARKING_SPACE_ID_NOT_FOUND(HttpStatus.OK, "APPLICANT4008", "신청자의 주차공간 id 가 존재하지 않습니다"),
    APPLICANT_SEARCH_NOT_FOUND(HttpStatus.OK, "APPLICANT4009", "검색 결과가 없습니다."),
    WINNER_SEARCH_NOT_FOUND(HttpStatus.OK, "APPLICANT4010", "당첨자 검색 결과가 없습니다."),
    APPLICANT_NOT_WINNING_STATUS(HttpStatus.OK, "APPLICANT4011", "당첨되지 않았습니다."),

    // 주차 공간 관련 응답
    PARKING_SPACE_NOT_FOUND(HttpStatus.OK, "PARKINGSPACE4001", "주차 공간이 존재하지 않습니다"),
    NO_REMAIN_SLOTS(HttpStatus.OK, "PARKINGSPACE4002", "남은 주차공간이 없습니다."),

    // 추첨 관련 응답
    DRAW_NOT_FOUND(HttpStatus.OK, "DRAW4001", "추첨이 존재하지 않습니다."),
    DRAW_NOT_READY(HttpStatus.OK, "DRAW4002", "아직 신청이 종료되지 않은 추첨입니다."),
    DRAW_ALREADY_EXECUTED(HttpStatus.OK, "DRAW4003", "이미 종료된 추첨입니다. 한 번 진행된 추첨은 다시 진행 될 수 없습니다."),
    DRAW_STATISTICS_NOT_EXIST(HttpStatus.OK, "DRAW4004", "추첨 통계가 존재하지 않습니다."),
    DRAW_NOT_IN_APPLY_PERIOD(HttpStatus.OK, "DRAW4005", "추첨 기간이 아닙니다."),
    DRAW_SEED_NOT_FOUND(HttpStatus.OK, "DRAW4006", "생성된 시드가 없습니다."),
    DRAW_MISMATCH(HttpStatus.BAD_REQUEST,"DRAW4007","알맞은 drawId를 전송해주세요."),

    //사용자 가중치 관련
    WEIGHTDETAILS_NOT_FOUND(HttpStatus.OK, "WEIGHTDETAILS4001", "해당 사용자의 가중치 정보가 없습니다."),
    WEIGHTDETAILS_ADDRESS_NOT_FOUND(HttpStatus.OK, "WEIGHTDETAILS4002", "해당 사용자의 주소 정보가 없습니다."),
    WEIGHTDETAILS_WORKTYPE_NOT_FOUND(HttpStatus.OK, "WEIGHTDETAILS4003", "해당 사용자의 근무 정보가 없습니다."),
    WEIGHTDETAILS_USER_SEED_NOT_FOUND(HttpStatus.OK, "WEIGHTDETAILS4004", "입력된 시드가 없습니다."),
    WEIGHTDETAILS_CHOICE_NOT_FOUND(HttpStatus.OK, "WEIGHTDETAILS4005", "입력된 주차 선택지가 없습니다."),
    WEIGHTDETAILS_TOO_LONG_USER_SEED(HttpStatus.OK, "WEIGHTDETAILS4006", "유효한 신청자의 랜덤 시드는 문자 1개입니다"),

    CERTIFICATEDOCS_NAME_NOT_FOUND(HttpStatus.OK, "CERTIFICATEDOCS4001", "업로드한 문서의 이름이 없습니다."),
    CERTIFICATEDOCS_NAME_TOO_LONG(HttpStatus.OK, "CERTIFICATEDOCS4002", "업로드한 문서의 이름 너무 깁니다."),
    CERTIFICATEDOCS_TOO_LARGE(HttpStatus.OK, "CERTIFICATEDOCS4003", "업로드한 문서의 전체 크기가 너무 큽니다."),
    CERTIFICATEDOCS_FORMAT_NOT_SUPPORTED(HttpStatus.OK, "CERTIFICATEDOCS4004", "업로드한 파일의 형식이 잘못 되었습니다."),

    CERTIFICATEDOCS_NAME_DUPLICATED(HttpStatus.OK, "CERTIFICATEDOCS4005", "업로드한 파일 이름이 중복됩니다."),
    CERTIFICATEDOCS_TOO_MANY(HttpStatus.OK, "CERTIFICATEDOCS4006", "업로드한 파일이 너무 많습니다."),
    CERTIFICATEDOCS_DRAW_ID_NOT_CORRECT(HttpStatus.OK, "CERTIFICATEDOCS4005", "drawId 가 -1 이 아닙니다" ),


    SEED_NOT_FOUND(HttpStatus.OK, "DRAW4004", "생성된 시드가 없습니다."),

    // 추첨 디테일 관련 응답
    WORK_TYPE_NOT_FOUND(HttpStatus.OK, "MEMBERDETAIL4001", "근무타입이 입력되지 않았습니다."),
    ADDRESS_NOT_FOUND(HttpStatus.OK, "MEMBERDETAIL4002", "주소가 입력되지 않았습니다."),

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
