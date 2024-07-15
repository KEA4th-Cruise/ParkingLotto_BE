package com.Cruise.ParkingLotto.global.response;


import com.Cruise.ParkingLotto.global.response.code.BaseCode;
import com.Cruise.ParkingLotto.global.response.code.status.SuccessStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "time", "code", "message", "result"})
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final LocalDateTime time;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;   //generic type: 어떤 값이 올지 모르기 때문에 generic 으로 선언, 추후에 외부에서 지정 가능

    // 성공한 경우 응답 생성
    public static <T> ApiResponse<T> onSuccess(BaseCode code, T result) {
        return new ApiResponse<>(true, LocalDateTime.now(), code.getReasonHttpStatus().getCode(), code.getReasonHttpStatus().getMessage(), result);
    }

    // 실패한 경우 응답 생성
    public static <T> ApiResponse<T> onFailure(String code, String message, T data) {
        return new ApiResponse<>(false, LocalDateTime.now(), code, message, data);
    }
}
