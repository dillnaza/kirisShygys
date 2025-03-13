package KirisShygys.service.impl;

import KirisShygys.exception.EmailSendingException;
import KirisShygys.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            logger.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            logger.error("Failed to send email to {}", to, e);
            throw new EmailSendingException("Failed to send email", e);
        }
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlMessage) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlMessage, true);
            helper.setFrom("no-reply@yourdomain.com");
            mailSender.send(message);
            logger.info("HTML email sent successfully to {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send HTML email to {}", to, e);
            throw new EmailSendingException("Failed to send email", e);
        }
    }
}
