//package com.project.backend.global.rabbitmq;
//
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.*;
//import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class RabbitMQDLComponent {
//    @Value("${rabbitmq.dlqQueue.name}")
//    private String dlqQueueName;
//
//    @Value("${rabbitmq.dlqExchange.name}")
//    private String dlqExchangeName;
//
//    @Value("${rabbitmq.dlqRoutingKey.key")
//    private String dlqRoutingKey;
//
//    private final ConnectionFactory connectionFactory;
//
//    /**
//     * dlq를 위한 큐 생성
//     * @return  Queue
//     *
//     * @author 이광석
//     * @since 25.03.06
//     */
//    @Bean
//    public Queue dlqQueue() {
//
//        return QueueBuilder.durable(dlqQueueName).build();
//    }
//
//    /**
//     * dlxExchange 생성
//     *
//     */
//    @Bean
//    public DirectExchange dlqExchange(){return new DirectExchange(dlqExchangeName);}
//
//
//    @Bean
//    public Binding dlqBinding(Queue dlqQueue, DirectExchange dlqExchange){
//        return BindingBuilder.bind(dlqQueue).to(dlqExchange).with(dlqExchangeName);
//    }
//
////    @Bean
////    public SimpleMessageListenerContainer dlqListener(){
////        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
////        container.setConnectionFactory(connectionFactory);
////        container.setQueueNames(dlqQueueName);
////        container.setMessageListener((MessageListener) message ->{
////            log.error("DLQ로 이동된 메시지 : {}",new String(message.getBody()));
////        });
////        container.start();
////        return container;
////    }
//}
