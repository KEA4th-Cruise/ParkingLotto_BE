package com.Cruise.ParkingLotto.global.exception.handler;


import com.Cruise.ParkingLotto.global.exception.GeneralException;
import com.Cruise.ParkingLotto.global.response.code.BaseErrorCode;

public class ExceptionHandler extends GeneralException {
    public ExceptionHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}