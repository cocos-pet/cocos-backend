ALTER TABLE pet
    ADD COLUMN birth_date DATE NULL
    COMMENT '과도기: age 기반 추정 생년월일 (1월 1일 기준)';

UPDATE pet
SET birth_date = DATE(
    CONCAT(
        YEAR(CURDATE()) - age,
        '-01-01'
    )
)
WHERE birth_date IS NULL
  AND age IS NOT NULL;

ALTER TABLE pet
    MODIFY age INT NULL;
