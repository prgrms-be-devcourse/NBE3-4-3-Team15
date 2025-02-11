package com.project.backend.domain.book.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.backend.domain.book.dto.BookDTO;
import com.project.backend.domain.book.dto.KakaoDTO;
import com.project.backend.domain.book.dto.NaverDTO;
import com.project.backend.domain.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * --도서에 쓰이는 유틸관련 클래스--
 *
 * @author -- 정재익 --
 * @since -- 2월 5일 --
 */
@Component
public class BookUtil {

    /**
     * -- ISBN 정제 메소드 --
     * 카카오 API에서 제공하는 ISBN은 "10자리ISBN 13자리ISBN" 형식이므로
     * 13자리 ISBN만 추출하여 반환한다.
     *
     * @param --String isbn --
     * @return String 정제된 13자리 ISBN
     * @author -- 정재익 --
     * @since -- 2월 5일 --
     */
    public static String extractIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return "";
        }
        String[] parts = isbn.split(" ");
        return (parts.length > 1) ? parts[1] : parts[0]; // 13자리 ISBN이 있으면 사용
    }

    /**
     * -- 중복 도서 제거 메소드 --
     * ISBN이 동일한 도서가 있을 경우 하나만 남긴다.
     *
     * @param books 중복이 포함된 도서 리스트
     *  * @return 중복 제거된 도서 리스트
     * @author 정재익
     * @since 2월 5일
     */
    public static List<Book> removeDuplicateBooks(List<Book> books) {
        Set<String> isbns = new HashSet<>();
        return books.stream()
                .filter(book -> isbns.add(book.getIsbn()))
                .toList();
    }

    /**
     * -- Book 변환 메소드 --
     *
     * @param -- Object item 데이터 --
     * @param -- String apiType 네이버와 카카오 구분 --
     * @return Book
     * @author 정재익
     * @since 2월 7일
     */
    public static Book convertToBook(Object item, String apiType, ObjectMapper objectMapper) {
        if ("kakao".equalsIgnoreCase(apiType)) {
            KakaoDTO kakaoBook = objectMapper.convertValue(item, KakaoDTO.class);
            return Book.builder()
                    .title(kakaoBook.getTitle())
                    .author(kakaoBook.getAuthor())
                    .description(kakaoBook.getDescription())
                    .image(kakaoBook.getImage())
                    .isbn(BookUtil.extractIsbn(kakaoBook.getIsbn()))
                    .favoriteCount(0)
                    .build();
        } else {
            NaverDTO naverBook = objectMapper.convertValue(item, NaverDTO.class);
            return Book.builder()
                    .title(naverBook.getTitle())
                    .author(naverBook.getAuthor())
                    .description(naverBook.getDescription())
                    .image(naverBook.getImage())
                    .isbn(naverBook.getIsbn())
                    .favoriteCount(0)
                    .build();
        }
    }

    /**
     * -- 페이징 처리 메소드 --
     *
     * @param -- page 페이지 수--
     * @param -- size 페이지 마다 표시되는 책 수--
     * @param -- bookList 중복을 제거한 bookList --
     * @return -- Page<BookDTO> --
     * @author -- 정재익 --
     * @since -- 2월 11일 --
     */
    public static Page<BookDTO> pagingBooks(int page, int size, List<BookDTO> bookList) {
        Pageable pageable = PageRequest.of(page, size);
        int start = page * size;

        if (start >= bookList.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, bookList.size());
        }

        int end = Math.min(start + size, bookList.size());
        List<BookDTO> pagedBooks = bookList.subList(start, end);

        return new PageImpl<>(pagedBooks, pageable, bookList.size());
    }

    /**
     * -- 이진 검색 메소드 --
     * 중복 데이터 검색시 효율적인 검색을 위해 도입
     *
     * @param -- targetIsbn 타겟 isbn--
     * @param -- sortedBook 검사 책 리스트--
     * @return boolean
     * @author 정재익
     * @since 2월 7일
     */
    public static boolean binarySearch(List<Book> sortedBooks, String targetIsbn) {
        int left = 0, right = sortedBooks.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int cmp = sortedBooks.get(mid).getIsbn().compareTo(targetIsbn);

            if (cmp == 0) return true;  // 중복 발견
            if (cmp < 0) left = mid + 1;
            else right = mid - 1;
        }
        return false; // 중복 없음
    }
}
