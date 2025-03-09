//package com.project.backend.global.rabbitmq.service;
//
//
//import com.project.backend.global.rabbitmq.dto.MessageDto;
//import com.project.backend.global.sse.service.SseService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.*;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitAdmin;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
//import org.springframework.amqp.support.converter.MessageConverter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
///**
// * RabbitMQService
// * RabbitMQ 서비스 단
// *
// * @author 이광석
// * @since 25.03.04
// */
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class RabbitMQService {
//
//    @Value("${rabbitmq.exchange.name}")
//    private String exchangeName;
//
//    @Value("${rabbitmq.routing.key}")
//    private String routingKey;
//
//    @Value("${rabbitmq.queue.name}")
//    private String queueName;
//
//    private final RabbitTemplate rabbitTemplate;
//    private final RabbitAdmin rabbitAdmin;
//    private final DirectExchange exchange;
//    private final ConnectionFactory connectionFactory;
//    private final SseService sseService;
//
//
//    /**
//     * 메시지를 exchange에 전달
//     * @param memberId
//     * @param messageDto (Long id,String content)
//     *
//     * @author 이광석
//     * @since 25.02.28
//     */
//    public void sendMessage(Long memberId,MessageDto messageDto){ //Producer
//        String routingKey = this.routingKey+memberId;
//
//
//        log.info(("message sent: {}"),messageDto.toString());
//
//
//        rabbitTemplate.convertAndSend(exchangeName,routingKey,messageDto);
//    }
//
//
//    /**
//     * rabbitmq listener
//     *  설정한(setQueueNames) 큐에 메시지가 추가되는것을 감지
//     *  큐에 메시지가 추가되면 onMessage 를 실행하고 message를 전달
//     *  onmessage는 messgae를 json(converter.fromMessage)으로 변경
//     *   sse(sendNotification)를 통해 클라이언트로 전달
//     *
//     * @param memberId
//     * @return container
//     *
//     * @author 이광석
//     * @since 25.02.28
//     */
//    public SimpleMessageListenerContainer dynamicRabbitListener(Long memberId){
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.setQueueNames(this.queueName+memberId);
//
//
//        container.setMessageListener(new MessageListener() {
//            @Override
//            public void onMessage(Message message) {   //Consumer
//                MessageConverter converter = rabbitTemplate.getMessageConverter(); //
//                Object obj = converter.fromMessage(message);
//                if(obj instanceof MessageDto){
//                    MessageDto messageDto = (MessageDto) obj;
//                    sseService.sendNotification(memberId, messageDto.getContent());
//                }else{
//                    System.out.println("not messageDto");
//                }
//            }
//        });
//        container.start();
//
//        return container;
//    }
//
//
//    /**
//     * rabbitmq에서 사용할 queue 생성
//     * @param memberId
//     * @return queueName
//     *
//     * @author 이광석
//     * @since 25.02.28
//     */
//    public String createMemberQueue(Long memberId){
//        String queueName =this.queueName +memberId;
//
//        Queue userQueue = QueueBuilder.durable(queueName)
////                .autoDelete()
//                .exclusive()
//                .build();
//
//        rabbitAdmin.declareQueue(userQueue);
//        String routingKey = "notification.routing.user." + memberId;
//
//        Binding binding = BindingBuilder.bind(userQueue).to(exchange).with(routingKey);
//        rabbitAdmin.declareBinding(binding);
//        System.out.println("큐 생성 성공");
//        return queueName;
//    }
//
//
//}
