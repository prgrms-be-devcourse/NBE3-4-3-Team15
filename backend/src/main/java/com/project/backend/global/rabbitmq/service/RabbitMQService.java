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
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * RabbitMQService
 * RabbitMQ 서비스 단
 *
 * @author 이광석
 * @since 25.03.04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RabbitMQService {

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.dlqQueue.name}")
    private String dlqQueueName;

    @Value("${rabbitmq.dlqExchange.name}")
    private String dlqExchangeName;

    @Value("${rabbitmq.dlqRoutingKey.key}")
    private String dlqRoutingKey;

    private final RabbitTemplate rabbitTemplate;
    private final RabbitAdmin rabbitAdmin;

    private final DirectExchange exchange;
    private final ConnectionFactory connectionFactory;
    private final SseService sseService;
    private final DirectExchange dlqExchange;

    @Autowired
    public RabbitMQService(
            RabbitTemplate rabbitTemplate,
            RabbitAdmin rabbitAdmin,
            ConnectionFactory connectionFactory,
            SseService sseService,
            @Qualifier("exchange") DirectExchange exchange,  // ✅ 생성자에서 @Qualifier 사용
            @Qualifier("dlqExchange") DirectExchange directExchange

    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitAdmin = rabbitAdmin;
        this.connectionFactory = connectionFactory;
        this.sseService = sseService;
        this.exchange = exchange;
        this.dlqExchange = directExchange;
    }
    /**
     * rabbitmq에서 사용할 queue 생성
     * @param memberId
     * @return queueName
     *
     * @author 이광석
     * @since 25.02.28
     */
    public String createMemberQueue(Long memberId){
        String queueName =this.queueName +memberId;

        Queue userQueue = QueueBuilder.durable(queueName)
//                .autoDelete()
                .deadLetterExchange(dlqExchangeName)
                .deadLetterRoutingKey(dlqRoutingKey)
                .exclusive()
                .build();

        rabbitAdmin.declareQueue(userQueue);
        String routingKey = "notification.routing.user." + memberId;

        Binding binding = BindingBuilder.bind(userQueue).to(exchange).with(routingKey);
        rabbitAdmin.declareBinding(binding);
        System.out.println("큐 생성 성공");
        return queueName;
    }



    /**
     * 메시지를 exchange에 전달
     * Producer
     * @param memberId
     * @param messageDto (Long id,String content)
     *
     * @author 이광석
     * @since 25.02.28
     */
    public void sendMessage(Long memberId,MessageDto messageDto){
        String routingKey = this.routingKey+memberId;

        log.info(("message sent: {}"),messageDto.toString());

        rabbitTemplate.convertAndSend(exchangeName,routingKey,messageDto);
    }


    /**
     * rabbitmq listener
     *  설정한(setQueueNames) 큐에 메시지가 추가되는것을 감지
     *  큐에 메시지가 추가되면 onMessage 를 실행하고 message를 전달
     *  onmessage는 messgae를 json(converter.fromMessage)으로 변경
     *   sse(sendNotification)를 통해 클라이언트로 전달
     *
     * @param memberId
     * @return container
     *
     * @author 이광석
     * @since 25.02.28
     */
    public SimpleMessageListenerContainer dynamicRabbitListener(Long memberId){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(this.queueName+memberId);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        container.setMessageListener((ChannelAwareMessageListener)(message,channel)->{
            try{
                MessageConverter converter = rabbitTemplate.getMessageConverter();
                Object obj = converter.fromMessage(message);
                if(obj instanceof  MessageDto){
                    MessageDto messageDto = (MessageDto) obj;
                    System.out.println("메시지 수신 성공");
                    sseService.sendNotification(memberId,messageDto.getContent());
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
                }else{
                    System.out.println("메시지 변환 오류");
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
                }
            }catch (Exception e){
                System.out.println("메시지 수신 오류");
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);

            }
        });




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
        container.start();

        return container;
    }




    public SimpleMessageListenerContainer dlqListener(){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(dlqQueueName);
        container.setMessageListener((MessageListener) message ->{
            log.error("DLQ로 이동된 메시지: {}",new String(message.getBody()));
        });
        container.start();
        return container;
    }


}
