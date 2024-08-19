package com.cruise.parkinglotto.service.mailService;

import com.cruise.parkinglotto.global.mail.MailInfo;
import jakarta.mail.MessagingException;

import java.security.NoSuchAlgorithmException;

public interface MailService {

    void sendEmailForCertification(MailInfo mailInfo) throws NoSuchAlgorithmException, MessagingException;
}
