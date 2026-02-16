package com.cocos.cocos.db.notification.repository;

import com.cocos.cocos.db.notification.entity.Notification;
import com.cocos.cocos.enums.notification.NotificationType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.cocos.cocos.db.notification.entity.QNotification.notification;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Notification> findNotifications(
            final Long memberId,
            final List<NotificationType> types,
            final LocalDateTime cursorCreatedAt,
            final Long cursorId,
            final int size
    ) {
        return jpaQueryFactory
                .selectFrom(notification)
                .where(
                        notifierEq(memberId),
                        typeIn(types),
                        beforeCursor(cursorCreatedAt, cursorId)
                )
                .orderBy(
                        notification.createdAt.desc(),
                        notification.id.desc()
                )
                .limit(size)
                .fetch();
    }

    private BooleanExpression notifierEq(final Long memberId) {
        return notification.notifierId.eq(memberId);
    }

    private BooleanExpression typeIn(final List<NotificationType> types) {
        return (types == null || types.isEmpty())
                ? null
                : notification.notificationType.in(types);
    }

    private BooleanExpression beforeCursor(
            final LocalDateTime cursorCreatedAt,
            final Long cursorId
    ) {
        if (cursorCreatedAt == null || cursorId == null) {
            return null;
        }

        return notification.createdAt.lt(cursorCreatedAt)
                .or(
                        notification.createdAt.eq(cursorCreatedAt)
                                .and(notification.id.lt(cursorId))
                );
    }
}
