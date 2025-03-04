package com.project.backend.global.rabbitmq.service;

import com.project.backend.global.rabbitmq.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RMQProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(MessageDto messageDto, String exchangeName, String routingKey){
        rabbitTemplate.convertAndSend(exchangeName,routingKey,messageDto);
    }
}
