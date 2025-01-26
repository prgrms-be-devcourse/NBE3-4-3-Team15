package com.project.backend.global.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    /**
     * ModelMapper Bean을 등록하기 위한 메서드
     *
     * @return -- ModelMapper --
     * @author -- 정재익 --
     * @since -- 1월 26일 --
     */

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
