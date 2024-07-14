package com.Cruise.ParkingLotto.global.exception.handler;


import com.Cruise.ParkingLotto.global.exception.GeneralException;
import com.Cruise.ParkingLotto.global.response.code.BaseErrorCode;

public class KafkaExceptionHandler extends GeneralException {
    public KafkaExceptionHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}