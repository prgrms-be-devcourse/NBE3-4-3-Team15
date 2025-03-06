package com.project.backend.global.rabbitmq;

import com.project.backend.global.rabbitmq.dto.MessageDto;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *RabbitMQConfig (설정)
 */
@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${spring.rabbitmq.port}")
    private int rabbitmqPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitmqUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitmqPassword;

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;




    /**
     * exchange 생성 매서드
     * direct 타입 exchange
     * @return DirectExchange
     *
     * @author 이광석
     * @since 25.02.28
     *
     */
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    /**
     * dlxExchange 생성
     *
     */



    /**
     * 바인딩 메서드
     * 매개변수로 받은 queue와 exchange를 binding(연결)한다

     * @param queue
     * @param exchange
     * @return BindingBuilder
     *
     * @author 이광석
     * @since 25.02.28
     */
    @Bean
    public Binding binding(Queue queue,@Qualifier("exchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }



    /**
     * RabbitMQ서버와의 연결을 관리하는 ConnectionFactory 객체 생성
     *
     * @return connectionFactory
     *
     * @author 이광석
     * @since 25.02.28
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitmqHost);
        connectionFactory.setPort(rabbitmqPort);
        connectionFactory.setUsername(rabbitmqUsername);
        connectionFactory.setPassword(rabbitmqPassword);
        return connectionFactory;
    }

    /**
     * RabbitMQ로 메시지 전송
     *
     * @param connectionFactory
     * @return rabbitTemplate
     *
     * @author 이광석
     * @since 25.02.28
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * JSON을 사용해 객체를 직렬화
     *
     * @return converter
     * @author 이광석
     * @since 25.02.28
     */
    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setDefaultType(MessageDto.class);
        converter.setClassMapper(classMapper);
        return converter;
    }

    /**
     * 애플리 캐이션이 실행될 때, 큐, 교환기 바인딩 정보를 자동으로 RabbitMQ 서버에 등록
     *
     * @param connectionFactory
     * @return RabbitAdmin(connectionFactory)
     *
     * @author 이광석
     * @since 25.02.28
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }


    @Value("${rabbitmq.dlqQueue.name}")
    private String dlqQueueName;

    @Value("${rabbitmq.dlqExchange.name}")
    private String dlqExchangeName;

    @Value("${rabbitmq.dlqRoutingKey.key}")
    private String dlqRoutingKey;


    /**
     * dlq를 위한 큐 생성
     * @return  Queue
     *
     * @author 이광석
     * @since 25.03.06
     */
    @Bean
    public Queue dlqQueue() {

        return QueueBuilder.durable(dlqQueueName).build();
    }

    /**
     * dlxExchange 생성
     *
     */
    @Bean
    public DirectExchange dlqExchange(){return new DirectExchange(dlqExchangeName);}


    @Bean
    public Binding dlqBinding(Queue dlqQueue,@Qualifier("dlqExchange") DirectExchange dlqExchange){
        return BindingBuilder.bind(dlqQueue).to(dlqExchange).with(dlqRoutingKey);
    }
}
