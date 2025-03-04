package com.project.backend.domain.book.service;

import com.project.backend.domain.book.dto.BookDTO;
import com.project.backend.domain.book.entity.Book;
import com.project.backend.domain.book.entity.Favorite;
import com.project.backend.domain.book.exception.BookErrorCode;
import com.project.backend.domain.book.exception.BookException;
import com.project.backend.domain.book.key.FavoriteId;
import com.project.backend.domain.book.repository.BookRepository;
import com.project.backend.domain.book.repository.BookRepository2;
import com.project.backend.domain.book.repository.FavoriteRepository;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.exception.MemberErrorCode;
import com.project.backend.domain.member.exception.MemberException;
import com.project.backend.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookService2 {
    @Autowired
    private final BookRepository bookRepository;

    @Autowired
    private final BookRepository2 bookRepository2;

    @Autowired
    private final MemberRepository memberRepository;

    @Autowired
    private final FavoriteRepository favoriteRepository;

    public BookService2(BookRepository bookRepository, BookRepository2 bookRepository2, MemberRepository memberRepository, FavoriteRepository favoriteRepository) {
        this.bookRepository = bookRepository;
        this.bookRepository2 = bookRepository2;
        this.memberRepository = memberRepository;
        this.favoriteRepository = favoriteRepository;
    }


    /**
     * -- 도서 찜, 찜취소 메소드 --
     *
     * 책을 찜하는 기능 이미 찜을 했을 경우 찜 취소
     * 책이 받은 찜한 수를 Book DB에 최신화
     * 유저 정보와 책 id을 favorite DB에 생성 혹은 삭제
     * 책의 찜 수가 0이 될 시에 Book DB에서 책 데이터 삭제
     * 책의 정보가 책 DB에 이미 존재 할 시 같은 책을 추가하지 않고 favoritecount만 수정하여 중복 책 등록 방지
     *
     * @param -- bookDto -- 프론트에서 BODY로 받은 DTO
     * @param -- username --
     * @return -- boolean --
     * @author -- 정재익, 김남우 --
     * @since -- 2월 9일 --
     */
    @Transactional
    public boolean favoriteBook(BookDTO bookDto, String username) {

        Member member = memberRepository.findByUsername(username)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NON_EXISTING_USERNAME));

        Book book = bookRepository.findByIsbn(bookDto.getIsbn());
        FavoriteId favoriteId = new FavoriteId(member.getId(), book.getId());

        if (favoriteRepository.existsById(favoriteId)) {
            favoriteRepository.deleteById(favoriteId); // 먼저 favorite 테이블에서 삭제

            int favoriteCount = book.getFavoriteCount();
            if (favoriteCount == 1) {
                bookRepository.delete(book); // favoriteCount가 1이면 Book 테이블에서 도서 삭제
            }
            else {
                bookRepository2.updateFavoriteCount(book, -1); // 아니면 favoriteCount 감소
            }

            return false;
        }

        else {
            bookRepository2.updateFavoriteCount(book, +1); // favoriteCount 1 증가

            Favorite favorite = Favorite.builder()
                .id(favoriteId)
                .book(book)
                .member(member)
                .build();

            favoriteRepository.save(favorite); // favorite 테이블에 저장

            return true;
        }
    }

    /**
     * -- 찜 도서 목록 메소드 --
     * 로그인한 유저의 찜 도서 목록 반환
     *
     * @param -- username --
     * @return -- List<BookDTO> --
     * @author -- 김남우 --
     * @since -- 2월 10일 --
     */
    public Page<BookDTO> getFavoriteBooks(String username, int page, int size) {

        Member member = memberRepository.findByUsername(username)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NON_EXISTING_USERNAME));

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<BookDTO> favoriteBooks = favoriteRepository.findFavoriteBooksByMemberId(member.getId(), pageable); // 멤버 ID에 해당하는 찜 도서 목록 조회

        if (favoriteBooks.isEmpty()) {
            throw new BookException(BookErrorCode.NO_FAVORITE_BOOKS);
        }

        return favoriteBooks;
    }
}
