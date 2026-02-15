# Search 성능 개선 기술 결정 기록 (2026-02-15)

## Decision Context
- 비교 대상: `dev` vs `feat/#317`
- 엔드포인트: `/api/dev/search/hospital`
- 목표: 검색 write 경로의 안정성(p95/p99/error)과 처리량(TPS) 균형 개선
- 조건 통일:
  - 동일 부하 시나리오
  - 동일 benchmark migration 경로(벤치마크 전용 임시 경로)
  - 실행 전 `docker compose ... down -v` 초기화

## Experiments
1. 비교 실험 (heavy profile, 3회 반복)
- 보고서: `docs/decisions/2026-02-15-k6-dev-vs-feat317-heavy-local-repeat3.md`
- 결과(평균):
  - TPS: dev 603.88 / feat 585.12 (feat -3.11%)
  - p95: dev 165.95ms / feat 162.19ms (feat 개선)
  - p99: dev 288.70ms / feat 253.04ms (feat 개선)
  - error rate: dev 0.0165% / feat 0.0098% (feat 개선)

2. 원인 분석 실험 (observability 1회)
- run: observability 1회 측정 번들(로컬 산출물, 커밋 제외)
- 보고서: `docs/decisions/2026-02-15-k6-observability-root-cause-20260215-021141.md`
- 핵심 관측:
  - `hikaricp.connections.pending` max 87
  - `lock_wait_count` max 0
  - `innodb_row_lock_waits` max 0

## Technical Decisions
### D1. feat/#317을 안정성 개선안으로 채택
- 이유: TPS는 소폭 하락했지만, p95/p99/error/분산 안정성 개선이 확인됨
- 적용 범위: 검색 write 경로 개선 효과 판단 기준을 tail latency + failure 중심으로 설정

### D2. 현 시점 1순위 병목을 DB lock이 아닌 connection pool 대기로 규정
- 근거: lock wait 지표 0, 반면 Hikari pending 피크가 크게 관측됨
- 결론: 다음 튜닝의 중심은 lock SQL보다 커넥션 점유 시간/큐잉 완화

### D3. 운영 제약(db.t4g.micro, 2 vCPU) 하에서 “풀 대폭 확대”는 보류
- 이유: 과도한 풀 확장은 DB 경합/컨텍스트 스위칭 증가 리스크
- 방침: `maximumPoolSize`는 소폭(예: 10->12) 검증만 허용

### D4. 다음 개선 우선순위
1. 트랜잭션 점유 시간 단축
2. 불필요 update/flush/commit 비용 절감
3. 소폭 pool 튜닝 + 동일 조건 반복 측정

## Why This Decision
- 단일 수치(TPS)만으로 판단하면 안정성 리스크를 놓칠 수 있음
- 실제 사용자 체감과 장애 리스크를 고려하면 tail latency/error 개선이 우선
- 반복 실험 + 관측 데이터를 근거로 재현 가능한 의사결정 가능

## Follow-up
- [ ] `maximumPoolSize` 10 vs 12 A/B (동일 시나리오 3회)
- [ ] write 경로 트랜잭션 경계 재점검
- [ ] commit/update 비중 감소 후 재측정
