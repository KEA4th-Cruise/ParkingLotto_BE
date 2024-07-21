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

    // [예시]
    MEMBER_FOUND(HttpStatus.OK,"MEMBER2001", "회원을 조회했습니다."),


    //  주차 구역 관련
    PARKING_SPACE_ADDED(HttpStatus.OK, "PARKINGSPACE2001", "해당 회차에 주차구역이 추가되었습니다."),

    //  추첨 관련
    DRAW_INFO_FOUND(HttpStatus.OK,"DRAW2002", "추첨 정보를 조회했습니다."),
    DRAW_EXECUTE_RESULT(HttpStatus.OK, "DRAW2002", "추첨결과가 정상적으로 저장되었습니다.")

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
