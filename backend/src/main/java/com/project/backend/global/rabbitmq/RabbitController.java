package com.project.backend.global.rabbitmq;


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
   private final MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestBody MessageDto messageDto){
        messageService.sendMessage(messageDto);
        return ResponseEntity.ok("Message sent to Rabbitmq");
    }
}
