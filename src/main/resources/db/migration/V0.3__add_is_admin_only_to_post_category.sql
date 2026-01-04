ALTER TABLE post_category
ADD COLUMN is_admin_only BOOLEAN NOT NULL DEFAULT FALSE
COMMENT '관리자 전용 카테고리 여부';
