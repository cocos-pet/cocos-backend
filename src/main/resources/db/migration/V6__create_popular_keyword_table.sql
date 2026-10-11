CREATE TABLE popular_keyword (
    id BIGINT NOT NULL AUTO_INCREMENT,
    keyword VARCHAR(255) NOT NULL,
    display_rank INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_popular_keyword_display_rank UNIQUE (display_rank)
);

-- 임의 초기 데이터 (기획 확정 시 UPDATE로 교체)
INSERT INTO popular_keyword (keyword, display_rank) VALUES
    ('강아지 설사', 1),
    ('좋은 병원 찾기', 2),
    ('강아지 사료 추천', 3),
    ('강아지 산책', 4),
    ('예방접종', 5);
