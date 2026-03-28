package com.rubilia.exercise201.repository;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.rubilia.exercise201.entity.Card;
import com.rubilia.exercise201.entity.CardItem;
import com.rubilia.exercise201.entity.Product;

@Repository
@RepositoryRestResource(path = "cardItems")
public interface CardItemRepository extends JpaRepository<CardItem, UUID> {
    Optional<CardItem> findByCard_IdAndProduct_Id(UUID cardId, UUID productId);

    Optional<CardItem> findByCardIdAndProductId(UUID cardId, UUID productId);

    List<CardItem> findByCard(Card card); // Thêm phương thức này

    @Modifying
    @Transactional
    @Query("DELETE FROM CardItem ci WHERE ci.product.id = :productId")
    void deleteByProductId(UUID productId);
}