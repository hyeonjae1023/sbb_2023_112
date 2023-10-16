package com.sbs.exam2;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class TempPasswordMail {

    private final JavaMailSender javaMailSender; //이메일 보내기 위한 javaMailSender 주입

    private String ePw; // 임시 비번을 저장하는 변수, 메일 생성 시 사용

    public MimeMessage createMessage(String to)
        throws UnsupportedEncodingException, MessagingException {
        //이메일 메세지 생성 메서드. 수신자 이메일 주소('to')를 인수로 받는다.

        MimeMessage message = javaMailSender.createMimeMessage();
        // MimeMessage 객체 생성. 이메일 메시지의 내용과 헤더 설정 때 사용

        message.addRecipients(Message.RecipientType.TO, to);
        message.setSubject("sbb 임시 비밀번호");

        String msgg = "";
        msgg += "<div style = 'margin:100px;'>";
        msgg += "<div align = 'center' style='border:1px solid black; font-family:verdana';>";
        msgg += "<h3 style='color:blue;'>임시 비밀번호이니다.</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "CODE : <strong>";
        msgg += ePw + "</strong><div><br/>";
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html");
        message.setFrom(new InternetAddress("hyeonjae1023111@gmail.com", "sbb_Admin"));

        return message;
    }


    public void sendSimpleMessage(String to, String pw) {
    //임시 비번을 메일로 보내는 메서드, 수신자 이메일 주소(to)와 임시 비번(pw)을 인수로 받음

        this.ePw = pw;

        MimeMessage message;
        try {
            message = createMessage(to);
        } catch (UnsupportedEncodingException | MessagingException e) {
            e.printStackTrace();
            throw new EmailException("이메일 생성 에러");
        }
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
            throw new EmailException("이메일 전송 에러");
        }
    }

    public class EmailException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public EmailException(String message) {
            super(message);
        }
    }
}
