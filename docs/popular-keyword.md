# 2. 인기 검색어

## 2.1 기능 정의

사용자가 많이 검색하는 키워드를 인기 검색어로 제공하여, 사용자가 별도의 검색어를 입력하지 않고도 관심 주제의 게시글을 탐색할 수 있음.

## 2.2 초기 운영 방식

서비스 초기에는 실제 검색 데이터가 충분하지 않으므로, 기획이 원하는 키워드를 하드코딩하여 노출

- 초기 인기 검색어 데이터는 직접 설정
- 초기 키워드 및 순서는 추후 결정 (미정, 개발 시 임의 데이터로 설정)
- 인기 검색어 선택 시 해당 키워드의 검색 결과 페이지로 이동

## 2.3 향후 운영 방식

검색 데이터가 충분히 축적된 이후에는 실제 검색 데이터를 기반으로 인기 검색어를 자동 산출하는 방식으로 변경을 검토

> 인기 검색어 = 일정 기간 내 가장 많이 검색된 키워드 순

`※ 인기 검색어 산정 기준은 추후 정의 필요`

## 2.4 노출 및 이동

- 인기 검색어는 1위~5위까지 세로 형태로 노출
- 사용자가 특정 인기 검색어를 선택하면 → 해당 키워드가 검색어로 입력된 검색 결과 화면으로 이동

예시)

1. 강아지 설사
2. 좋은 병원 찾기
3. 강아지 사료 추천
4. 강아지 산책
5. 예방접종

---

## 2.5 백엔드 결정 사항

| 항목 | 결정 | 비고 |
|------|------|------|
| 적용 범위 | 커뮤니티 검색만 | 병원 검색은 해당 없음 |
| 인증 | 비로그인 허용 | `SecurityConfig` `WHITE_LIST_URL`에 추가 (`/posts/**`와 동일 정책) |
| 키워드 저장 위치 | DB 테이블 `popular_keyword` | 배포 없이 SQL로 변경. 초기 데이터는 Flyway 마이그레이션으로 삽입 (2.8 참고) |
| 키워드 변경 방법 | 개발자가 DB에 직접 SQL 실행 | 관리자 페이지는 만들지 않음. 변경 빈도가 높아지면 검토 |
| 캐싱 | 하지 않음 | 5행 인덱스 조회라 부담 없음. 부하 측정 시 ① `Cache-Control: max-age` → ② Caffeine 로컬 캐시(TTL 필수, SQL 직접 변경은 앱이 감지 못함) → ③ Redis(다중 서버 즉시 일관성 필요 시) 순으로 적용 |
| 노출 개수 | 5개 고정 | 순서 = 리스트 순서 = 순위 |
| 향후 대비 (로그 수집) | 지금은 하지 않음 | 아래 2.7 참고 |

## 2.6 API 명세

### 인기 검색어 조회

`GET {api.prefix}/search/popular`

- 인증: 불필요
- Request: 없음
- Response

```json
{
  "code": 200,
  "message": "...",
  "data": {
    "keywords": ["강아지 설사", "좋은 병원 찾기", "강아지 사료 추천", "강아지 산책", "예방접종"]
  }
}
```

- 배열 순서가 곧 순위 (index 0 = 1위)

### 인기 검색어 선택 시 (백엔드 변경 없음)

- 검색 결과: 기존 게시글 목록 조회 API의 `keyword` 파라미터로 조회
- 최근 검색어 저장 여부: 클라이언트가 기존 `POST {api.prefix}/search?keyword=...` 호출 여부로 결정 (프론트와 합의 필요)

## 2.7 향후 자동 산출 전환 시 메모

- 기존 `search` 테이블은 (회원, 키워드, 타입) unique + `updatedAt` 갱신 구조
  → "최근 N일 내 해당 키워드를 검색한 **회원 수**" 기준 집계는 추가 테이블 없이 가능
  → "검색 **횟수**" 기준이 필요하면 검색 1회당 1행의 로그 테이블(또는 Redis Sorted Set) 필요
- 산정 기준 확정 후: 스케줄러가 주기적으로 집계하여 `popular_keyword` 테이블을 갱신
  → 조회 API/코드 변경 없음, 클라이언트 변경 없음

## 2.8 테이블 설계

```sql
-- V6__create_popular_keyword_table.sql
CREATE TABLE popular_keyword (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    keyword      VARCHAR(255) NOT NULL,
    display_rank INT          NOT NULL UNIQUE
);

-- 임의 초기 데이터 (기획 확정 시 UPDATE로 교체)
INSERT INTO popular_keyword (keyword, display_rank) VALUES
    ('강아지 설사', 1),
    ('좋은 병원 찾기', 2),
    ('강아지 사료 추천', 3),
    ('강아지 산책', 4),
    ('예방접종', 5);
```

- 조회: `ORDER BY display_rank` 상위 5개 (`findTop5ByOrderByDisplayRankAsc`)
- `rank`는 MySQL 예약어라 `display_rank` 사용
- 키워드 변경 예: `UPDATE popular_keyword SET keyword = '슬개골 탈구' WHERE display_rank = 3;`
