import http from "k6/http";
import { check, sleep } from "k6";

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const API_PREFIX = __ENV.API_PREFIX || "/api/dev";
const TEST_AUTH_SECRET = __ENV.TEST_AUTH_SECRET || "perf-test-secret";
const TEST_MEMBER_ID = Number(__ENV.TEST_MEMBER_ID || "1");

export const options = {
  scenarios: {
    search_write_load: {
      executor: "ramping-vus",
      startVUs: 0,
      stages: [
        { duration: "30s", target: 20 },
        { duration: "60s", target: 20 },
        { duration: "30s", target: 0 }
      ]
    }
  },
  thresholds: {
    http_req_failed: ["rate<0.01"],
    http_req_duration: ["p(95)<500", "p(99)<900"]
  }
};

function waitUntilHealthy(maxRetries = 60) {
  for (let i = 0; i < maxRetries; i += 1) {
    const res = http.get(`${BASE_URL}${API_PREFIX}/test/health-check`);
    if (res.status === 200) {
      return;
    }
    sleep(1);
  }
  throw new Error("app did not become healthy in time");
}

function issueAccessToken(memberId) {
  const payload = JSON.stringify({ memberIds: [memberId] });
  const res = http.post(`${BASE_URL}${API_PREFIX}/test/auth/login`, payload, {
    headers: {
      "Content-Type": "application/json",
      "X-Test-Auth-Secret": TEST_AUTH_SECRET
    }
  });

  check(res, {
    "login status is 200": (r) => r.status === 200
  });

  if (res.status !== 200) {
    throw new Error(`login failed: status=${res.status} body=${res.body}`);
  }

  const tokens = res.json();
  if (!Array.isArray(tokens) || tokens.length === 0 || !tokens[0].accessToken) {
    throw new Error("access token missing from test login response");
  }
  return tokens[0].accessToken;
}

let accessToken = "";

export function setup() {
  waitUntilHealthy();
  accessToken = issueAccessToken(TEST_MEMBER_ID);
  return { accessToken };
}

export default function (data) {
  const keyword = `k6-${Math.floor(Math.random() * 100)}`;
  const res = http.post(
    `${BASE_URL}${API_PREFIX}/search/hospital?keyword=${encodeURIComponent(keyword)}`,
    null,
    {
      headers: {
        Authorization: `Bearer ${data.accessToken}`
      }
    }
  );

  check(res, {
    "search write status is 200": (r) => r.status === 200
  });

  sleep(0.1);
}

