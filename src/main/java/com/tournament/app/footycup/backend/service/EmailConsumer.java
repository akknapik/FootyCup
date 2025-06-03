package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.config.RabbitMQConfig;
import com.tournament.app.footycup.backend.model.EmailMessage;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailConsumer {
    private final JavaMailSender mailSender;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receiveMessage(EmailMessage message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(message.getTo());
            mail.setSubject(message.getSubject());
            mail.setText(message.getContent());
            mailSender.send(mail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
