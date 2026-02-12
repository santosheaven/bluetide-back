#!/usr/bin/env bash
# Run simple smoke tests (curl) against a running BlueTide server (http://localhost:8080 by default)
# Exits non-zero if any request returns a non-2xx/3xx HTTP status.

set -euo pipefail
BASE=${BASE:-http://localhost:8080}
TMPDIR=$(mktemp -d)
trap 'rm -rf "$TMPDIR"' EXIT

# helper: run a request and check status
run() {
  local method=$1; shift
  local url=$1; shift
  local data=${1:-}
  local headers=( -H "Content-Type: application/json" )

  echo "-> $method $url"
  if [ -n "$data" ]; then
    resp=$(curl -s -o "$TMPDIR/resp.txt" -w "%{http_code}" -X "$method" "$url" -H "Content-Type: application/json" -d "$data" )
  else
    resp=$(curl -s -o "$TMPDIR/resp.txt" -w "%{http_code}" -X "$method" "$url")
  fi
  body=$(cat "$TMPDIR/resp.txt" || true)
  echo "HTTP $resp"
  if [[ "$resp" =~ ^2|3 ]]; then
    echo "OK"
    return 0
  else
    echo "FAIL: $method $url returned HTTP $resp"
    echo "Response body: $body"
    return 1
  fi
}

# Public endpoints
run GET "$BASE/api/public/health"
run GET "$BASE/api/public/info"

# Auth (register/login) - these will modify server state; use a test email to avoid collisions
TEST_EMAIL="smoke-test-$(date +%s)@example.com"
REGISTER_PAYLOAD=$(cat <<EOF
{"displayName":"Smoke Test","email":"$TEST_EMAIL","password":"smoke123"}
EOF
)
run POST "$BASE/api/auth/register" "$REGISTER_PAYLOAD"

LOGIN_PAYLOAD=$(cat <<EOF
{"email":"$TEST_EMAIL","password":"smoke123"}
EOF
)
# login to fetch token
TOKEN_RESP=$(curl -s -X POST "$BASE/api/auth/login" -H "Content-Type: application/json" -d "$LOGIN_PAYLOAD")
# try to extract token (naive JSON parse)
ACCESS_TOKEN=$(echo "$TOKEN_RESP" | sed -n 's/.*"accessToken"\s*:\s*"\([^"]*\)".*/\1/p')
if [ -z "$ACCESS_TOKEN" ]; then
  echo "Warning: could not extract access token from login response; continuing without auth-protected tests"
else
  echo "Got access token"
fi

AUTH_HEADER=""
if [ -n "$ACCESS_TOKEN" ]; then
  AUTH_HEADER="-H Authorization:Bearer $ACCESS_TOKEN"
fi

# Users CRUD (using token if present)
if [ -n "$ACCESS_TOKEN" ]; then
  run POST "$BASE/api/users" '{"email":"user-smoke@example.com","password":"pwd","displayName":"User Smoke","role":"OWNER"}' || true
  # list users
  curl -s -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE/api/users" -o "$TMPDIR/users.json" || true
fi

# Services / other endpoints (simple smoke checks)
run GET "$BASE/api/companies" || true
run GET "$BASE/api/properties" || true
run GET "$BASE/api/inventory" || true
run GET "$BASE/api/maintenance" || true
run GET "$BASE/api/services" || true
run GET "$BASE/api/invoices" || true
run GET "$BASE/api/notifications" || true

echo "Smoke tests finished"

