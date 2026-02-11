#!/usr/bin/env bash
# Find and replace remaining javax.* occurrences to jakarta.* in source, test and resource files.
# This attempts a safe, in-place replacement but will skip binary files.
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

# file globs to scan
GLOBS=("src/**/*.java" "src/**/*.kt" "src/**/*.xml" "src/**/*.jsp" "src/**/*.properties" "src/**/*.yml" "src/**/*.yaml" "src/**/*.json" "pom.xml" "**/*.gradle" "src/test/**/*.java" "src/test/**/*.xml")

# find files matching globs
FILES=()
for g in "${GLOBS[@]}"; do
  while IFS= read -r -d $'\0' f; do
    FILES+=("$f")
  done < <(printf '%s\0' $(bash -lc "ls $g 2>/dev/null || true") ) || true
done

if [ ${#FILES[@]} -eq 0 ]; then
  echo "No candidate files found for replacement."
  exit 0
fi

echo "Found ${#FILES[@]} candidate files. Running replacements..."

for f in "${FILES[@]}"; do
  if [ -f "$f" ]; then
    # simple textual replacement
    if grep -Iq "javax\." "$f"; then
      echo "Updating: $f"
      # create a backup
      cp "$f" "$f.bak"
      sed -E 's/\bjavax\./jakarta./g' "$f.bak" > "$f"
    fi
  fi
done

echo "Replacement complete. Backups are preserved with .bak extension."

