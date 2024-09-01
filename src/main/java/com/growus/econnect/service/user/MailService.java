package com.growus.econnect.service.user;

import com.growus.econnect.dto.user.EmailDTO;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public interface MailService{
    public String createRandomCode();
    public String sendMail(EmailDTO dto) throws MessagingException, UnsupportedEncodingException;
}
