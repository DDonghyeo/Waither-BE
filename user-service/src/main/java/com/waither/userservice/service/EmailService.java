package com.waither.userservice.service;

import com.waither.userservice.global.exception.CustomException;
import com.waither.userservice.global.response.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

import static org.bouncycastle.pqc.crypto.util.PrivateKeyFactory.createKey;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    // 인증 번호 발송 폼 생성
    private MimeMessage createAuthMessage(String Email, String title, String auth) {
        MimeMessage message = emailSender.createMimeMessage();

        try {
            message.addRecipients(MimeMessage.RecipientType.TO, Email); //보내는 대상
            message.setSubject(title); //제목

            String msgg = "<div style='background-color:#f7f7f7; padding:20px;'>";
            msgg += "<h1 style='color:#333; font-family: Arial, sans-serif; margin-bottom:20px;'>안녕하세요, 인증번호 안내입니다.</h1>";
            msgg += "<p style='font-size: 16px; color:#666; margin-bottom:20px;'>아래 인증번호를 입력해주세요.</p>";
            msgg += "<div style='background-color:#fff; border:1px solid #ccc; padding: 20px; text-align: center; font-family:Verdana, Geneva, sans-serif;'>";
            msgg += "<h3 style='color:#5189F6; font-size: 24px; margin-bottom: 20px;'>인증 번호</h3>";
            msgg += "<div style='font-size:20px;'><strong>CODE: " + auth + "</strong></div>";
            msgg += "</div></div>";

            message.setText(msgg, "utf-8", "html");//내용
            message.setFrom(new InternetAddress("Weither","Weither"));//보내는 사람

            // 이메일 보내기
            return message;
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new CustomException(ErrorCode.NO_SUCH_ALGORITHM);
        }
    }

    // 임시 비밀번호 발송 폼 생성
    private MimeMessage createTempPasswordMessage(String Email, String title, String tempPassword) {
        MimeMessage  message = emailSender.createMimeMessage();
        try {
            message.addRecipients(MimeMessage.RecipientType.TO, Email); // 받는 사람 추가
            message.setSubject(title); // 제목 설정

            String msgg = "<div style='background-color:#f7f7f7; padding:20px;'>";
            msgg += "<h1 style='color:#333; font-family: Arial, sans-serif; margin-bottom:20px;'>안녕하세요, 임시 비밀번호 안내입니다.</h1>";
            msgg += "<p style='font-size: 16px; color:#666; margin-bottom:20px;'>임시 비밀번호를 안전하게 보관하세요.</p>";
            msgg += "<div style='background-color:#fff; border:1px solid #ccc; padding: 20px; text-align: center; font-family:Verdana, Geneva, sans-serif;'>";
            msgg += "<h3 style='color:#5189F6; font-size: 24px; margin-bottom: 20px;'>임시 비밀번호</h3>";
            msgg += "<div style='font-size:20px;'><strong>CODE: " + tempPassword + "</strong></div>";
            msgg += "</div></div>";

            message.setText(msgg, "utf-8", "html"); // 내용 설정
            message.setFrom(new InternetAddress("Weither", "Weither")); // 보내는 사람 설정

            // 이메일 보내기
            return message;
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new CustomException(ErrorCode.NO_SUCH_ALGORITHM);
        }
    }

    // 이메일 전송 메소드
    public void sendAuthEmail(String email, String title, String Auth) {
        try {
            MimeMessage message = createAuthMessage(email, title, Auth);
            emailSender.send(message);
        } catch (RuntimeException e) {
            log.debug("MailService.sendEmail exception occur toEmail: {}, " + "title: {}, authcode: {}",
                    email, title, Auth);
            throw new CustomException(ErrorCode.UNABLE_TO_SEND_EMAIL);
        }
    }

    public void sendTempPasswordEmail(String email, String title, String TempPassword){

        try {
            MimeMessage message = createTempPasswordMessage(email, title, TempPassword);
            emailSender.send(message);
        } catch (RuntimeException e) {
            log.debug("MailService.sendEmail exception occur toEmail: {}, " + "title: {}, authcode: {}",
                    email, title, TempPassword);
            throw new CustomException(ErrorCode.UNABLE_TO_SEND_EMAIL);
        }
    }

}