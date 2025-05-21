package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.config.RabbitMQConfig;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.tournament.app.footycup.backend.model.EmailMessage;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class EmailProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendEmail(String to, String subject, String content) {
        EmailMessage emailMessage = new EmailMessage(to, subject, content);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                "emailRoutingKey",
                emailMessage
        );
    }
}
