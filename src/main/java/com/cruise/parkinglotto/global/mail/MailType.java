package com.cruise.parkinglotto.global.mail;

import lombok.Getter;

@Getter
public enum MailType {
    RESERVE("지난번 신청하신 일반 추첨의 결과는 낙첨 입니다.\n홈페이지에 접속하여 본인의 추첨 결과를 확인하세요"), // 예비 번호를 부여 받았을 때
    RESERVE_WINNER("지난번 신청하신 일반 추첨의 예비번호가 빠져 당첨되었습니다.\n홈페이지에 접속하여 본인의 추첨 결과를 확인하세요"), // 예비인원이 당첨됐을 때
    WINNER("지난번 신청하신 일반 추첨의 결과는 당첨 입니다.\n홈페이지에 접속하여 본인의 추첨 결과를 확인하세요"), // 일반 추첨에 당첨됐을 때
    PRIORITY_REJECTION("우대 신청이 거절 되었습니다."), // 우대 신청자가 거절당했을 때
    PRIORITY_APPROVAL("우대 신청 승인 및 주차구역 배정이 처리되었습니다. \\n홈페이지에 접속하여 본인의 주차 구역을 확인하세요."), // 우대 신청자가 승인받았을 때
    REGISTER_REJECTION("등록이 거절됨"), // 등록이 거절당했을 때
    REGISTER_APPROVAL("등록이 승인됨"), // 등록이 승인 됐을 때

    ;

    private final String message;

    MailType(String message) {this.message = message;}

}
