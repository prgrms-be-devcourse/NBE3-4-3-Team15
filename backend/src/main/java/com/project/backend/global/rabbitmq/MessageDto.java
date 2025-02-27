package com.project.backend.global.rabbitmq;


import lombok.*;
import org.springframework.stereotype.Service;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {

    private String title;
    private String content;
}
