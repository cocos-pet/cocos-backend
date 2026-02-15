# k6 Heavy Comparison (dev vs feat/#317) - 3 Repeats (2026-02-15 KST)

## Scope
- Endpoint: `/api/dev/search/hospital`
- Scenario: `perf/k6/search-write-heavy.js`
- Profile: member pool 1,000 / keyword pool 10,000 / max 100 VU
- Method: `dev`, `feat/#317` 각각 3회 반복 (동일 조건)

## Per-Run Snapshot
| Branch | Run | TPS | p95 (ms) | p99 (ms) | error rate |
|---|---:|---:|---:|---:|---:|
| feat | 1 | 573.1453 | 163.3572 | 258.6753 | 0.0097% |
| feat | 2 | 579.7261 | 163.1360 | 249.0858 | 0.0114% |
| feat | 3 | 602.5003 | 160.0890 | 251.3620 | 0.0082% |
| dev | 1 | 611.8847 | 158.4270 | 250.0735 | 0.0161% |
| dev | 2 | 614.8951 | 152.7370 | 237.4643 | 0.0197% |
| dev | 3 | 584.8632 | 186.6804 | 378.5734 | 0.0139% |

## Aggregated Result (mean ± std)
| Metric | dev | feat/#317 | Delta (feat vs dev) |
|---|---:|---:|---:|
| TPS | 603.8810 ± 13.5036 | 585.1239 ± 12.5773 | -3.1061% |
| p95 (ms) | 165.9481 ± 14.8428 | 162.1941 ± 1.4913 | +2.2622% 개선 |
| p99 (ms) | 288.7037 ± 63.7556 | 253.0411 ± 4.0910 | +12.3527% 개선 |
| error rate | 0.0165% ± 0.0024% | 0.0098% ± 0.0013% | 개선 |

## Interpretation
- 3회 반복 기준으로 `feat/#317`은 평균 TPS가 낮다(-3.1%).
- 반면 tail latency(p95/p99)와 error rate는 `feat/#317`이 더 좋다.
- `dev`는 run3에서 p99이 크게 튀어 분산이 커졌고, `feat/#317`은 분산이 상대적으로 작아 일관성이 높다.

## Conclusion
- 처리량 절대치보다 안정성/꼬리 지연/실패율을 우선하면 `feat/#317` 채택이 합리적이다.
- TPS가 핵심 KPI라면 추가 튜닝(예: 배치/커넥션/쿼리 경로) 후 재측정이 필요하다.
