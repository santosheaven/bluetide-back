#!/bin/bash
# Replace occurrences of 'javax.' with 'jakarta.' in tests and resource files.
# Safe for macOS (uses sed -i '')
set -euo pipefail
repo_root="$(cd "$(dirname "$0")/.." && pwd)"
echo "Repo root: $repo_root"
# File globs to process
paths=(
  "$repo_root/src/test/java"
  "$repo_root/src/test/resources"
  "$repo_root/src/main/resources"
)
changed=0
for p in "${paths[@]}"; do
  if [ -d "$p" ]; then
    echo "Scanning $p for 'javax.' occurrences..."
    # find text files (java, xml, properties, yml, html, json)
    files=$(grep -RIl --exclude-dir=target --exclude=*.class "javax\." "$p" || true)
    if [ -n "$files" ]; then
      echo "$files" | while IFS= read -r f; do
        echo "Replacing in: $f"
        # macOS sed needs -i '' for in-place
        sed -i '' 's/javax\./jakarta./g' "$f" || { echo "sed failed for $f"; exit 1; }
        changed=1
      done
    else
      echo "No matches in $p"
    fi
  fi
done
if [ $changed -eq 0 ]; then
  echo "No javax.* occurrences were replaced."
else
  echo "Replacements completed. Please review changes and run tests."
fi
exit 0

