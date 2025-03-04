package com.project.backend.global.rabbitmq.controller;


import com.project.backend.global.rabbitmq.dto.MessageDto;
import com.project.backend.global.rabbitmq.service.RabbitMQService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rabbit")
@RequiredArgsConstructor
public class RabbitController {
   private final RabbitMQService rabbitMQService;
//
//    @PostMapping("/send")
//    public ResponseEntity<String> send(@RequestBody MessageDto messageDto){
//        rabbitMQService.sendMessage(messageDto);
//        return ResponseEntity.ok("Message sent to Rabbitmq");
//    }
}
