package com.cruise.parkinglotto.global.mail;

import lombok.Getter;

@Getter
public enum MailType {
    RESERVE("예비 번호 부여됨"), // 예비 번호를 부여 받았을 때
    RESERVE_WINNER("예비 인원 당첨됨"), // 예비인원이 당첨됐을 때
    WINNER("일반 추첨 당첨됨"), // 일반 추첨에 당첨됐을 때
    PRIORITY_REJECTION("우대 신청 거절됨"), // 우대 신청자가 거절당했을 때
    PRIORITY_APPROVAL("우대 신청 승인됨"), // 우대 신청자가 승인받았을 때
    REGISTER_REJECTION("등록이 거절됨"), // 등록이 거절당했을 때
    REGISTER_APPROVAL("등록이 승인됨"); // 등록이 승인 됐을 때

    private final String message;

    MailType(String message) {this.message = message;}

}
