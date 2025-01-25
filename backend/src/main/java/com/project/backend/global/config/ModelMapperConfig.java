package com.project.backend.global.config;

import com.project.backend.domain.book.entity.Book;
import com.project.backend.domain.book.vo.NaverBookVo;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    /**
     * -- 네이버api데이터를 db에 저장하기 위해 엔티티로 변경할 때의 설명이 빠지지 않게 하기 위한 메소드 --
     * 상세 조회가 아닐때에는 설명부분을 빼고 컨트롤러에 전달하려함
     * DB에도 저장이 안되는 현상이 일어나서 명시적으로 mapper에 설명이 전달되도록 조치함
     *
     * @return -- ModelMapper --
     * @author -- 정재익 --
     * @since -- 1월 25일 --
     */

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(NaverBookVo.Item.class, Book.class)
                .addMappings(mapper -> mapper.map(NaverBookVo.Item::getDescription, Book::setDescription));
        return modelMapper;
    }

}
