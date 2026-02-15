# Local k6 (Docker) Guide

## 목적

- 배포 전에 로컬에서 `search` 쓰기 경로 부하를 검증한다.
- Docker 리소스 제한으로 재현 가능한 테스트 환경을 만든다.

## 구성

- `docker-compose.perf.yml`
  - `mysql`: 테스트 DB
  - `app`: Spring Boot 앱
  - `k6`: 부하 테스트 실행기
- `perf/k6/search-write.js`
  - 테스트 로그인으로 JWT 발급
  - `/api/dev/search/hospital` 쓰기 부하 수행

## 실행

1. 앱/DB 기동

```bash
docker compose -f docker-compose.perf.yml up -d mysql app
```

2. k6 실행

```bash
docker compose -f docker-compose.perf.yml run --rm k6
```

3. 결과 확인

```bash
cat perf/results/summary.json
```

4. 종료

```bash
docker compose -f docker-compose.perf.yml down -v
```

## 리소스 제한 조정

- `docker-compose.perf.yml`에서 `mem_limit`, `cpus` 값을 변경한다.
- 예:
  - app: `mem_limit: 1500m`, `cpus: 1.5`
  - mysql: `mem_limit: 768m`, `cpus: 1.0`
  - k6: `mem_limit: 512m`, `cpus: 1.0`

## 기본 검증 지표

- `http_req_duration` p95/p99
- `http_req_failed`
- app 로그의 query count / duration (RequestLoggingFilter)

## 자동 수집 실행 (k6 + DB/App 메트릭)

다음 스크립트는 한 번에 아래를 수행한다.
- k6 실행
- Actuator(Hikari/HTTP) 메트릭 샘플링
- MySQL 슬로우쿼리/락대기/상태값 샘플링
- 종료 후 digest/innodb snapshot 저장

```bash
START_STACK=1 \
ACTUATOR_PASSWORD='<actuator-basic-auth-password>' \
K6_MODE=local \
K6_SCRIPT_LOCAL=perf/k6/search-write-heavy.js \
SAMPLE_INTERVAL_SECONDS=5 \
SLOW_QUERY_THRESHOLD_SECONDS=0.05 \
./perf/scripts/run-k6-with-metrics.sh
```

- `ACTUATOR_PASSWORD`는 필수다. 기본값이 없으므로 실행 시 반드시 지정해야 한다.

결과는 `perf/results/observability/<run-id>/` 아래에 생성된다.
- `k6-summary.json`
- `app-metrics/*.json`
- `db/slow_log_window.tsv`
- `db/lock_waits.tsv`
- `db/mysql_status.tsv`
- `db/top_digest.tsv`
- `db/innodb_status.txt`
