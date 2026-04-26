#!/usr/bin/env bash
set -u

status=0

pass() {
  printf 'OK: %s\n' "$1"
}

fail() {
  printf 'FAIL: %s\n' "$1"
  status=1
}

warn() {
  printf 'WARN: %s\n' "$1"
}

need_cmd() {
  if command -v "$1" >/dev/null 2>&1; then
    pass "$1 found at $(command -v "$1")"
    return 0
  fi
  fail "$1 is not installed or not on PATH"
  return 1
}

major_from_java_version() {
  sed -n 's/.* version "\([^"]*\)".*/\1/p' | awk -F. '{ if ($1 == "1") print $2; else print $1; }'
}

if need_cmd java; then
  java_major="$(java -version 2>&1 | major_from_java_version | head -n 1)"
  if [ "$java_major" = "21" ]; then
    pass "java reports JDK 21"
  else
    fail "java must report major version 21, got '${java_major:-unknown}'"
  fi
fi

if need_cmd mvn; then
  mvn_version="$(mvn -v 2>/dev/null | sed -n 's/^Apache Maven \([0-9][0-9.]*\).*/\1/p' | head -n 1)"
  mvn_major="$(printf '%s\n' "$mvn_version" | awk -F. '{print $1}')"
  mvn_minor="$(printf '%s\n' "$mvn_version" | awk -F. '{print $2}')"
  if [ "${mvn_major:-0}" -gt 3 ] || { [ "${mvn_major:-0}" -eq 3 ] && [ "${mvn_minor:-0}" -ge 9 ]; }; then
    pass "Maven is ${mvn_version}"
  else
    fail "Maven must be 3.9 or newer, got '${mvn_version:-unknown}'"
  fi

  mvn_java_major="$(mvn -v 2>/dev/null | sed -n 's/^Java version: \([^,]*\).*/\1/p' | awk -F. '{ if ($1 == "1") print $2; else print $1; }' | head -n 1)"
  if [ "$mvn_java_major" = "21" ]; then
    pass "Maven is using JDK 21"
  else
    fail "Maven must use JDK 21, got '${mvn_java_major:-unknown}'"
  fi
fi

need_cmd git >/dev/null
need_cmd make >/dev/null

if [ -f pom.xml ]; then
  pass "pom.xml found in current directory"
else
  warn "current directory is not an Anserini repository root"
fi

if [ -f .gitmodules ]; then
  if [ -d tools/eval ]; then
    pass "tools/eval submodule path is present"
  else
    fail "tools/eval is missing; run git submodule update --init --recursive"
  fi
fi

exit "$status"
