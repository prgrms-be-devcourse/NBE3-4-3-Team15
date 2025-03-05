package com.project.backend.global.rabbitmq.dto;


import lombok.*;
import org.springframework.stereotype.Service;


/**
 * MessageDTO
 * rabbitmq에서 MessageDTO를 이용해서 데이터 전달
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

    private Long id;
    private String content;


}
