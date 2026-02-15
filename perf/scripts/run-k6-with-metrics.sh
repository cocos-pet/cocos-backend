#!/usr/bin/env bash
set -euo pipefail

# End-to-end perf runner:
# 1) optional docker compose up
# 2) enable/clear MySQL observability tables
# 3) sample Actuator + MySQL metrics while k6 runs
# 4) export snapshots and k6 summary into a timestamped folder

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

RUN_ID="${RUN_ID:-$(date +%Y%m%d-%H%M%S)}"
OUT_DIR="${OUT_DIR:-perf/results/observability/$RUN_ID}"
APP_METRICS_DIR="$OUT_DIR/app-metrics"
DB_DIR="$OUT_DIR/db"
STOP_FILE="$OUT_DIR/.stop"

START_STACK="${START_STACK:-0}"
SAMPLE_INTERVAL_SECONDS="${SAMPLE_INTERVAL_SECONDS:-5}"
SLOW_QUERY_THRESHOLD_SECONDS="${SLOW_QUERY_THRESHOLD_SECONDS:-0.05}"

K6_MODE="${K6_MODE:-local}" # local | docker
K6_BIN="${K6_BIN:-k6}"
K6_SCRIPT_LOCAL="${K6_SCRIPT_LOCAL:-perf/k6/search-write-heavy.js}"
K6_SCRIPT_DOCKER="${K6_SCRIPT_DOCKER:-/perf/k6/search-write-heavy.js}"
K6_SUMMARY_FILE="$OUT_DIR/k6-summary.json"
K6_EXTRA_ARGS="${K6_EXTRA_ARGS:-}"

BASE_URL="${BASE_URL:-http://localhost:8080}"
API_PREFIX="${API_PREFIX:-/api/dev}"
TEST_AUTH_SECRET="${TEST_AUTH_SECRET:-perf-test-secret}"

ACTUATOR_BASE_URL="${ACTUATOR_BASE_URL:-http://localhost:9090/actuator}"
ACTUATOR_USER="${ACTUATOR_USER:-cocos}"
ACTUATOR_PASSWORD="${ACTUATOR_PASSWORD:-}"

MYSQL_SERVICE="${MYSQL_SERVICE:-mysql}"
MYSQL_ROOT_USER="${MYSQL_ROOT_USER:-root}"
MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-root}"

if [ -n "${COMPOSE_ARGS:-}" ]; then
  # shellcheck disable=SC2206
  COMPOSE=( ${COMPOSE_ARGS} )
else
  COMPOSE=(docker compose -f docker-compose.perf.yml -f docker-compose.perf.benchmark.yml)
fi

mkdir -p "$APP_METRICS_DIR" "$DB_DIR"

if [ -z "$ACTUATOR_PASSWORD" ]; then
  echo "ACTUATOR_PASSWORD is required. Set it explicitly before running."
  exit 1
fi

log() {
  echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*"
}

mysql_exec() {
  local sql="$1"
  "${COMPOSE[@]}" exec -T "$MYSQL_SERVICE" \
    mysql -u"$MYSQL_ROOT_USER" -p"$MYSQL_ROOT_PASSWORD" --batch --raw --skip-column-names -e "$sql"
}

collect_actuator_metrics_once() {
  local ts="$1"
  local metrics=(
    "hikaricp.connections"
    "hikaricp.connections.acquire"
    "hikaricp.connections.pending"
    "hikaricp.connections.active"
    "hikaricp.connections.idle"
    "hikaricp.connections.usage"
    "http.server.requests"
  )

  for metric in "${metrics[@]}"; do
    local file="$APP_METRICS_DIR/${ts}_${metric//\//_}.json"
    if ! curl -fsS -u "$ACTUATOR_USER:$ACTUATOR_PASSWORD" \
      "$ACTUATOR_BASE_URL/metrics/$metric" > "$file"; then
      echo "{\"error\":\"metric fetch failed\",\"metric\":\"$metric\",\"ts\":\"$ts\"}" > "$file"
    fi
  done
}

collect_mysql_metrics_once() {
  local ts="$1"

  # Slow query log stats for the recent window
  local slow_line
  if slow_line="$(mysql_exec "
SELECT COUNT(*),
       IFNULL(ROUND(AVG(TIME_TO_SEC(query_time))*1000,3),0),
       IFNULL(ROUND(MAX(TIME_TO_SEC(query_time))*1000,3),0)
FROM mysql.slow_log
WHERE start_time >= NOW() - INTERVAL ${SAMPLE_INTERVAL_SECONDS} SECOND;
" 2>/dev/null)"; then
    echo -e "${ts}\t${slow_line}" >> "$DB_DIR/slow_log_window.tsv"
  else
    echo -e "${ts}\tERR\tERR\tERR" >> "$DB_DIR/slow_log_window.tsv"
  fi

  # Lock wait count (performance_schema)
  local lock_wait_count
  if lock_wait_count="$(mysql_exec "SELECT COUNT(*) FROM performance_schema.data_lock_waits;" 2>/dev/null)"; then
    echo -e "${ts}\t${lock_wait_count}" >> "$DB_DIR/lock_waits.tsv"
  else
    echo -e "${ts}\tERR" >> "$DB_DIR/lock_waits.tsv"
  fi

  # Key MySQL/InnoDB status values
  local status_line
  if status_line="$(mysql_exec "
SELECT
  MAX(CASE WHEN variable_name = 'Threads_running' THEN variable_value END),
  MAX(CASE WHEN variable_name = 'Innodb_row_lock_waits' THEN variable_value END),
  MAX(CASE WHEN variable_name = 'Innodb_row_lock_time' THEN variable_value END)
FROM performance_schema.global_status
WHERE variable_name IN ('Threads_running','Innodb_row_lock_waits','Innodb_row_lock_time');
" 2>/dev/null)"; then
    echo -e "${ts}\t${status_line}" >> "$DB_DIR/mysql_status.tsv"
  else
    echo -e "${ts}\tERR\tERR\tERR" >> "$DB_DIR/mysql_status.tsv"
  fi
}

sampling_loop() {
  echo -e "ts\tslow_count_window\tslow_avg_ms_window\tslow_max_ms_window" > "$DB_DIR/slow_log_window.tsv"
  echo -e "ts\tlock_wait_count" > "$DB_DIR/lock_waits.tsv"
  echo -e "ts\tthreads_running\tinnodb_row_lock_waits\tinnodb_row_lock_time" > "$DB_DIR/mysql_status.tsv"

  while [ ! -f "$STOP_FILE" ]; do
    local ts
    ts="$(date -u +%Y%m%dT%H%M%SZ)"
    collect_actuator_metrics_once "$ts"
    collect_mysql_metrics_once "$ts"
    sleep "$SAMPLE_INTERVAL_SECONDS"
  done
}

collect_final_db_snapshots() {
  mysql_exec "
SELECT digest_text, count_star,
       ROUND(sum_timer_wait/1e12,3) AS total_s,
       ROUND(avg_timer_wait/1e9,3) AS avg_ms,
       ROUND(max_timer_wait/1e9,3) AS max_ms
FROM performance_schema.events_statements_summary_by_digest
ORDER BY sum_timer_wait DESC
LIMIT 30;
" > "$DB_DIR/top_digest.tsv" 2>/dev/null || true

  mysql_exec "SELECT * FROM performance_schema.data_lock_waits;" > "$DB_DIR/data_lock_waits.tsv" 2>/dev/null || true

  "${COMPOSE[@]}" exec -T "$MYSQL_SERVICE" \
    mysql -u"$MYSQL_ROOT_USER" -p"$MYSQL_ROOT_PASSWORD" -e "SHOW ENGINE INNODB STATUS\\G" \
    > "$DB_DIR/innodb_status.txt" 2>/dev/null || true
}

write_metadata() {
  cat > "$OUT_DIR/metadata.env" <<META
RUN_ID=$RUN_ID
K6_MODE=$K6_MODE
K6_SCRIPT_LOCAL=$K6_SCRIPT_LOCAL
K6_SCRIPT_DOCKER=$K6_SCRIPT_DOCKER
BASE_URL=$BASE_URL
API_PREFIX=$API_PREFIX
SAMPLE_INTERVAL_SECONDS=$SAMPLE_INTERVAL_SECONDS
SLOW_QUERY_THRESHOLD_SECONDS=$SLOW_QUERY_THRESHOLD_SECONDS
ACTUATOR_BASE_URL=$ACTUATOR_BASE_URL
MYSQL_SERVICE=$MYSQL_SERVICE
META
}

cleanup() {
  touch "$STOP_FILE" 2>/dev/null || true
}
trap cleanup EXIT INT TERM

if [ "$START_STACK" = "1" ]; then
  log "Starting docker compose stack (mysql, app)"
  "${COMPOSE[@]}" up -d mysql app
fi

log "Preparing MySQL observability knobs"
mysql_exec "SET GLOBAL slow_query_log = 'ON';"
mysql_exec "SET GLOBAL log_output = 'TABLE';"
mysql_exec "SET GLOBAL long_query_time = ${SLOW_QUERY_THRESHOLD_SECONDS};"
mysql_exec "TRUNCATE TABLE mysql.slow_log;"
mysql_exec "TRUNCATE TABLE performance_schema.events_statements_summary_by_digest;" 2>/dev/null || true

write_metadata

log "Starting sampler (interval=${SAMPLE_INTERVAL_SECONDS}s)"
sampling_loop &
SAMPLER_PID=$!

run_k6_local() {
  local -a extra_args=()
  if [ -n "$K6_EXTRA_ARGS" ]; then
    # shellcheck disable=SC2206
    extra_args=( $K6_EXTRA_ARGS )
  fi

  if [ "${#extra_args[@]}" -gt 0 ]; then
    BASE_URL="$BASE_URL" API_PREFIX="$API_PREFIX" TEST_AUTH_SECRET="$TEST_AUTH_SECRET" \
      "$K6_BIN" run --summary-export "$K6_SUMMARY_FILE" "${extra_args[@]}" "$K6_SCRIPT_LOCAL"
  else
    BASE_URL="$BASE_URL" API_PREFIX="$API_PREFIX" TEST_AUTH_SECRET="$TEST_AUTH_SECRET" \
      "$K6_BIN" run --summary-export "$K6_SUMMARY_FILE" "$K6_SCRIPT_LOCAL"
  fi
}

run_k6_docker() {
  local summary_container="/perf/results/summary-auto-${RUN_ID}.json"
  "${COMPOSE[@]}" run --rm \
    -e BASE_URL="$BASE_URL" \
    -e API_PREFIX="$API_PREFIX" \
    -e TEST_AUTH_SECRET="$TEST_AUTH_SECRET" \
    k6 run --summary-export="$summary_container" "$K6_SCRIPT_DOCKER"

  if [ -f "perf/results/summary-auto-${RUN_ID}.json" ]; then
    cp "perf/results/summary-auto-${RUN_ID}.json" "$K6_SUMMARY_FILE"
  fi
}

log "Running k6 (${K6_MODE})"
if [ "$K6_MODE" = "local" ]; then
  run_k6_local
elif [ "$K6_MODE" = "docker" ]; then
  run_k6_docker
else
  log "Unsupported K6_MODE: $K6_MODE"
  exit 1
fi

log "Stopping sampler"
touch "$STOP_FILE"
wait "$SAMPLER_PID" || true

log "Collecting final DB snapshots"
collect_final_db_snapshots

log "Done. Observability bundle: $OUT_DIR"
log "- k6 summary: $K6_SUMMARY_FILE"
log "- app metrics json: $APP_METRICS_DIR"
log "- db metrics: $DB_DIR"
