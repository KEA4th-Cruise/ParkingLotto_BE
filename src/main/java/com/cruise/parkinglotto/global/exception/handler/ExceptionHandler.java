package com.cruise.parkinglotto.global.exception.handler;


import com.cruise.parkinglotto.global.exception.GeneralException;
import com.cruise.parkinglotto.global.response.code.BaseErrorCode;

public class ExceptionHandler extends GeneralException {
    public ExceptionHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}