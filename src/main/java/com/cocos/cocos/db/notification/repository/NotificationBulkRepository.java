package com.cocos.cocos.db.notification.repository;

import com.cocos.cocos.db.notification.entity.Notification;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class NotificationBulkRepository {
    private final SimpleJdbcInsert jdbcInsert;

    public NotificationBulkRepository(DataSource dataSource) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("notification")
                .usingGeneratedKeyColumns("id");
    }

    public void saveAllInBatch(final List<Notification> notifications) {
        if (notifications.isEmpty()) return;

        int batchSize = 1000;
        for (int i = 0; i < notifications.size(); i += batchSize) {
            final int endIndex = Math.min(i + batchSize, notifications.size());
            final List<Notification> subList = notifications.subList(i, endIndex);

            final SqlParameterSource[] params = subList.stream()
                    .map(this::createParameterSource)
                    .toArray(SqlParameterSource[]::new);

            jdbcInsert.executeBatch(params);
        }
    }

    private SqlParameterSource createParameterSource(final Notification notification) {
        final LocalDateTime now = LocalDateTime.now();

        return new MapSqlParameterSource()
                .addValue("notifier_id", notification.getNotifierId())
                .addValue("actor_id", notification.getActorId())
                .addValue("actor_nickname", notification.getActorNickname())
                .addValue("notification_type", notification.getNotificationType().name())
                .addValue("notification_target_id", notification.getNotificationTargetId())
                .addValue("milestone", notification.getMilestone())
                .addValue("post_id", notification.getPostId())
                .addValue("title", notification.getTitle())
                .addValue("content", notification.getContent())
                .addValue("is_read", notification.isRead())
                .addValue("created_at", now)
                .addValue("updated_at", now);
    }
}
