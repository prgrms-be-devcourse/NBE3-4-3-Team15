package com.project.backend.domain.book.repository;

import com.project.backend.domain.book.entity.Favorite;
import com.project.backend.domain.book.key.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
    int countByIdBookId(int bookId);

    List<Favorite> findByIdMemberId(String MemberId);
}
