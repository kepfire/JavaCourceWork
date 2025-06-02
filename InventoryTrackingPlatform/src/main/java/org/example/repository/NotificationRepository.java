package org.example.repository;

import org.example.domain.enums.NotificationStatus;
import org.example.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {
    
    @Query("SELECT n FROM NotificationEntity n WHERE " +
           "n.user.userId = :userId AND " +
           "(:status IS NULL OR n.status = :status) " +
           "ORDER BY n.sentTime DESC")
    List<NotificationEntity> findByUserAndStatus(@Param("userId") UUID userId, @Param("status") NotificationStatus status);
}
