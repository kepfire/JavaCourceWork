package org.example.repository;

import org.example.entity.ItemEntity;
import org.example.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, UUID> {

    @Query("SELECT i FROM ItemEntity i WHERE " +
           "(:category IS NULL OR i.category = :category) AND " +
           "(:supplier IS NULL OR i.supplier = :supplier) " +
           "ORDER BY " +
           "CASE WHEN :sortBy = 'name' THEN i.name END ASC, " +
           "CASE WHEN :sortBy = 'category' THEN i.category END ASC, " +
           "CASE WHEN :sortBy = 'currentQuantity' THEN i.currentQuantity END DESC")
    List<ItemEntity> findWithFilters(@Param("category") String category,
                                   @Param("supplier") String supplier,
                                   @Param("sortBy") String sortBy);

    @Query("SELECT i FROM ItemEntity i WHERE i.currentQuantity < i.criticalQuantity")
    List<ItemEntity> findByCurrentQuantityLessThanCritical();
}
