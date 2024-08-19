package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.global.mail.MailInfo;
import com.cruise.parkinglotto.global.mail.MailType;

public class MailInfoConverter {

    public static MailInfo toMailInfo(String email, String nameKo, MailType mailType) {
        return MailInfo.builder()
                .email(email)
                .nameKo(nameKo)
                .mailType(mailType)
                .build();
    }
}
