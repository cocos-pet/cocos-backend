package com.cocos.cocos.db.notification.repository;

import com.cocos.cocos.db.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {

    boolean existsByPostIdAndMilestone(final Long postId, final int milestone);

    boolean existsByNotifierIdAndIsReadFalse(final Long notifierId);
}
