package com.cruise.parkinglotto.service.mailService;

import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.mail.MailInfo;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;

    @Async
    public void sendEmailForCertification(MailInfo mailInfo) throws NoSuchAlgorithmException, MessagingException {

        String content = createMailContent(mailInfo);

        try {
            sendMail(mailInfo.getEmail(), content);
        } catch (MessagingException e) { // 메일 전송에 실패한 경우
            log.error("메일 전송 실패! 유효하지 않은 이메일: {}", mailInfo.getEmail());
            throw new ExceptionHandler(ErrorStatus.MAIL_INVALID_ADDRESS);
        }
    }

    private void sendMail(String email, String content) throws MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setTo(email);
        helper.setSubject("알림 이메일");
        helper.setText(content);
        javaMailSender.send(mimeMessage);
        log.info("메일 전송 완료");
    }

    private String createMailContent(MailInfo mailInfo) {

        String receiver = String.format("이름: %s \n", mailInfo.getNameKo());
        String mailType = String.format("사유: " + mailInfo.getMailType().getMessage() + "\n");

        return receiver + mailType;
    }
}
