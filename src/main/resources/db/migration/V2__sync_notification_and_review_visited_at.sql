/* =========================================================
 * notification 테이블
 * ========================================================= */

CREATE TABLE IF NOT EXISTS notification (
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
    PRIMARY KEY (id),
    CONSTRAINT uq_post_milestone UNIQUE (post_id, milestone)
);

/* index: notifier_id + created_at */
SET @idx_exists := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE table_schema = DATABASE()
      AND table_name = 'notification'
      AND index_name = 'idx_notification_user_created'
);

SET @sql := IF(
    @idx_exists = 0,
    'CREATE INDEX idx_notification_user_created ON notification (notifier_id, created_at DESC)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

/* index: notifier_id + is_read */
SET @idx_exists := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE table_schema = DATABASE()
      AND table_name = 'notification'
      AND index_name = 'idx_notification_user_read'
);

SET @sql := IF(
    @idx_exists = 0,
    'CREATE INDEX idx_notification_user_read ON notification (notifier_id, is_read)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

/* index: notifier_id + type + created_at */
SET @idx_exists := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE table_schema = DATABASE()
      AND table_name = 'notification'
      AND index_name = 'idx_notification_user_type_created'
);

SET @sql := IF(
    @idx_exists = 0,
    'CREATE INDEX idx_notification_user_type_created ON notification (notifier_id, notification_type, created_at DESC)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
