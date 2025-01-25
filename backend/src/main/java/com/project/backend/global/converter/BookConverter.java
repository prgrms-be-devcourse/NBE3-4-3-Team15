package com.project.backend.global.converter;

import com.project.backend.domain.book.entity.Book;
import com.project.backend.domain.book.vo.NaverBookVo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookConverter {

    @Autowired
    private ModelMapper modelMapper;
    /**
     * -- 네이버 api를 통해받은 결과값을 Book리스트로 바꾸는 메서드 --
     * <p>
     * 1. 데이터베이스에 네이버 api의 결과값을 반환하기 위한 용도
     * 2. 설명부분이 누락되지 않기 위해 addMappings 설명값을 추가했다.
     *
     * @param -- List<NaverBookVo.Item> --
     * @return -- List<Book> --
     * @author -- 정재익 --
     * @since -- 1월 25일 --
     */

    public List<Book> apiToListBook(List<NaverBookVo.Item> items) {
        this.modelMapper.typeMap(NaverBookVo.Item.class, Book.class)
                .addMappings(mapper -> mapper.map(NaverBookVo.Item::getDescription, Book::setDiscription));

        return items.stream()
                .map(item -> modelMapper.map(item, Book.class))
                .collect(Collectors.toList());
        }
    }
