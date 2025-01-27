package com.project.backend.domain.favorite.repository;

import com.project.backend.domain.favorite.entity.Favorite;
import com.project.backend.domain.favorite.key.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
    int countByIdBookId(int bookId);
}
