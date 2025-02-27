package com.project.backend.global.rabbitmq;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService {
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;


    public void sendMessage(MessageDto messageDto){
        log.info(("message sent: {}"),messageDto.toString());
    }

    public void reciveMessage(MessageDto messageDto){
        log.info("received message: {}" ,messageDto.toString());
    }
}
