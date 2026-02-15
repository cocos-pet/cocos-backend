import http from "k6/http";
import { check, sleep } from "k6";

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const API_PREFIX = __ENV.API_PREFIX || "/api/dev";
const TEST_AUTH_SECRET = __ENV.TEST_AUTH_SECRET || "changeme-test-auth-secret";

const MEMBER_POOL_START = Number(__ENV.MEMBER_POOL_START || "1");
const MEMBER_POOL_SIZE = Number(__ENV.MEMBER_POOL_SIZE || "1000");
const KEYWORD_POOL_SIZE = Number(__ENV.KEYWORD_POOL_SIZE || "10000");
const LOGIN_BATCH_SIZE = Number(__ENV.LOGIN_BATCH_SIZE || "200");
const SLEEP_SECONDS = Number(__ENV.SLEEP_SECONDS || "0.05");

const DEFAULT_STAGES = [
  { duration: "30s", target: 50 },
  { duration: "60s", target: 50 },
  { duration: "30s", target: 100 },
  { duration: "120s", target: 100 },
  { duration: "30s", target: 0 }
];

export const options = {
  scenarios: {
    search_write_heavy: {
      executor: "ramping-vus",
      startVUs: 0,
      stages: DEFAULT_STAGES
    }
  },
  thresholds: {
    http_req_failed: ["rate<0.01"],
    http_req_duration: ["p(95)<700", "p(99)<1200"]
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

function buildMemberIds() {
  return Array.from(
    { length: MEMBER_POOL_SIZE },
    (_, idx) => MEMBER_POOL_START + idx
  );
}

function issueAccessTokens(memberIds) {
  const accessTokens = [];

  for (let i = 0; i < memberIds.length; i += LOGIN_BATCH_SIZE) {
    const batch = memberIds.slice(i, i + LOGIN_BATCH_SIZE);
    const payload = JSON.stringify({ memberIds: batch });
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
    if (!Array.isArray(tokens) || tokens.length !== batch.length) {
      throw new Error("unexpected token response length");
    }

    for (let tokenIndex = 0; tokenIndex < tokens.length; tokenIndex += 1) {
      const tokenObj = tokens[tokenIndex];
      const token = tokenObj && tokenObj.accessToken;
      if (!token) {
        throw new Error("access token missing from test login response");
      }
      accessTokens.push(token);
    }
  }

  return accessTokens;
}

export function setup() {
  waitUntilHealthy();
  const memberIds = buildMemberIds();
  const accessTokens = issueAccessTokens(memberIds);
  return { accessTokens };
}

export default function (data) {
  const tokenIndex = Math.floor(Math.random() * data.accessTokens.length);
  const keywordIndex = Math.floor(Math.random() * KEYWORD_POOL_SIZE);
  const keyword = `k6-heavy-${keywordIndex}`;

  const res = http.post(
    `${BASE_URL}${API_PREFIX}/search/hospital?keyword=${encodeURIComponent(keyword)}`,
    null,
    {
      headers: {
        Authorization: `Bearer ${data.accessTokens[tokenIndex]}`
      }
    }
  );

  check(res, {
    "search write status is 200": (r) => r.status === 200
  });

  sleep(SLEEP_SECONDS);
}
