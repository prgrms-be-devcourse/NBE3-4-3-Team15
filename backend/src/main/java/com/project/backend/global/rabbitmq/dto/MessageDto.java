package com.project.backend.global.rabbitmq.dto;


import lombok.*;
import org.springframework.stereotype.Service;

@Getter
@Setter
@ToString
public class MessageDto {

    private Long id;
    private String content;

    public MessageDto(Long id,String content ){
        this.id = id;
        this.content = content;
    }
}
