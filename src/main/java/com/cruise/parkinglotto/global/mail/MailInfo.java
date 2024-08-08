package com.cruise.parkinglotto.global.mail;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MailInfo {

    private String email;
    private String nameKo;
    private MailType mailType;
}
