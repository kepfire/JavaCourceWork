package org.example.repository;

import org.example.domain.enums.OperationType;
import org.example.entity.OperationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OperationRepository extends JpaRepository<OperationEntity, UUID> {
    
    @Query("SELECT o FROM OperationEntity o WHERE " +
           "(:operationType IS NULL OR o.operationType = :operationType) " +
           "ORDER BY o.timestamp DESC")
    List<OperationEntity> findByFilters(@Param("operationType") OperationType operationType);
}
