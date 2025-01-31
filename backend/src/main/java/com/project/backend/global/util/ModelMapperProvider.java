package com.project.backend.global.util;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * -- ModelMapper 유틸 클래스 --
 *
 * @author -- 정재익 --
 * @since -- 1월 31일 --
 */
@Component
public class ModelMapperProvider {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}