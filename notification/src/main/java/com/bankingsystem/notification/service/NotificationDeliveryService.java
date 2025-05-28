package com.bankingsystem.notification.service;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.bankingsystem.notification.dto.NotificationDto;
import com.bankingsystem.notification.entity.Notification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationDeliveryService {

    private final JavaMailSender mailSender;

    public void sendNotification(NotificationDto notificationDto) {
        switch (notificationDto.getType()) {
            case SECURITY:
            case TRANSACTION:
            case LOAN:
            case CARD:
            case ACCOUNT:
            case GENERAL:
                sendEmail(notificationDto);
                break;
            default:
                throw new IllegalArgumentException("Unsupported notification type: " + notificationDto.getType());
        }
    }

    private void sendEmail(NotificationDto notificationDto) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(notificationDto.getCustomerEmail());

            helper.setSubject("Bank Notification: " + notificationDto.getType());

            helper.setText(notificationDto.getTitle() + "\n\n" + notificationDto.getMessage());

            mailSender.send(message);

            log.info("Sent {} email to customer {} ({}) : {}",
                    notificationDto.getType(),
                    notificationDto.getCustomerId(),
                    notificationDto.getCustomerEmail(),
                    notificationDto.getMessage());

        } catch (MailException | MessagingException e) {
            log.error("Failed to send {} email to customer {} ({}): {}",
                    notificationDto.getType(),
                    notificationDto.getCustomerId(),
                    notificationDto.getCustomerEmail(),
                    e.getMessage());
            throw new RuntimeException("Email sending failed for customer " + notificationDto.getCustomerId(), e);
        }
    }

}