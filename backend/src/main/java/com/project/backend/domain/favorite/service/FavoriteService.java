package com.project.backend.domain.favorite.service;

import com.project.backend.domain.favorite.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    /**
     * -- 해당책의 추천받은 개수를 반환하는 메소드 --
     * <p>
     * 책의 Id값을 받아서 추천받은 개수를 반환
     *
     * @param -- id (책의 id) --
     * @return -- int --
     * @author -- 정재익 --
     * @since -- 1월 26일 --
     */
    public int getFavoriteCountByBook(int id) {
        return favoriteRepository.countByIdBookId(id);
    }
}
