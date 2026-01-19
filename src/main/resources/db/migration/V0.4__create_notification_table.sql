CREATE TABLE notification (
    id BIGINT NOT NULL AUTO_INCREMENT,
    notifier_id BIGINT NOT NULL,
    actor_id BIGINT NOT NULL,
    actor_nickname VARCHAR(255),
    notification_type VARCHAR(50) NOT NULL,
    notification_target_id BIGINT NOT NULL,
    milestone INT,
    post_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    PRIMARY KEY (id)
);

-- 내 알림 최신순 조회
CREATE INDEX idx_notification_user_created
    ON notification (notifier_id, created_at DESC);

-- 미읽음 알림 관련 조회 (badge, 상태 계산)
CREATE INDEX idx_notification_user_read
    ON notification (notifier_id, is_read);

-- 탭별 알림 조회 (매거진 / 일반)
CREATE INDEX idx_notification_user_type_created
    ON notification (notifier_id, notification_type, created_at DESC);
