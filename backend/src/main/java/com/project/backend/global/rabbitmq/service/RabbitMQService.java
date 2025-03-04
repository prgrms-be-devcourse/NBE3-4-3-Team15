package com.project.backend.global.rabbitmq.service;


import com.project.backend.global.rabbitmq.dto.MessageDto;
import com.project.backend.global.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RabbitMQService {

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Value("${rabbitmq.queue.name")
    private String queueName;

    private final RabbitTemplate rabbitTemplate;
    private final RabbitAdmin rabbitAdmin;
    private final DirectExchange exchange;
    private final ConnectionFactory connectionFactory;
    private final SseService sseService;


    public void sendMessage(Long memberId,MessageDto messageDto){
        String routingKey = this.routingKey+memberId;


        log.info(("message sent: {}"),messageDto.toString());
        rabbitTemplate.convertAndSend(exchangeName,routingKey,messageDto);
    }



    public SimpleMessageListenerContainer dynamicRabbitListener(Long memberId){
        System.out.println("rabbitmq 큐에서 거냄");
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(this.routingKey+memberId);

        System.out.println("Connection Factory: " + connectionFactory);

        container.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                System.out.println("onMessage");
                MessageConverter converter = rabbitTemplate.getMessageConverter();
                Object obj = converter.fromMessage(message);
                if(obj instanceof MessageDto){
                    MessageDto messageDto = (MessageDto) obj;
                    System.out.println("sse 전달전");
                    sseService.sendNotification(memberId, messageDto.getContent());
                }
            }
        });
        container.start();
        if (container.isRunning()) {
            System.out.println("✅ Listener is running!");
        } else {
            System.out.println("❌ Listener is NOT running!");
        }
        return container;
    }


    public String createMemberQueue(Long memberId){
        String queueName =this.queueName +memberId;

        Queue userQueue = QueueBuilder.durable(queueName)
//                .autoDelete()
                .exclusive()
                .build();

        rabbitAdmin.declareQueue(userQueue);
        String routingKey = "notification.routing.user." + memberId;

        Binding binding = BindingBuilder.bind(userQueue).to(exchange).with(routingKey);
        rabbitAdmin.declareBinding(binding);
        System.out.println("큐 생성 성공");
        return queueName;
    }


}
