#!/usr/bin/env bash
set -euo pipefail

ROOT="ozdemir-launcher/app/src/main/java/com/ozdemir/tvlauncher"
FILES=(
  "$ROOT/MainActivity.java"
  "$ROOT/SettingsPanelActivity.java"
  "$ROOT/AppsActivity.java"
  "$ROOT/ArchiveActivity.java"
  "$ROOT/FilesActivity.java"
  "$ROOT/KidsActivity.java"
  "$ROOT/LiveTvActivity.java"
)

# Prototype / emoji / Unicode glyphs that previously made the TV UI look toy-like.
PATTERN='⌁|ᛒ|▣|▧|▤|☺|★|◆|✦|◷|⚙|⏻|◫|⌨|◉'

failed=0
for f in "${FILES[@]}"; do
  [[ -f "$f" ]] || continue
  if grep -nE "$PATTERN" "$f"; then
    echo "UI guard: prototype glyph found in $f"
    failed=1
  fi
done

if [[ "$failed" -ne 0 ]]; then
  echo "Replace prototype glyphs with production raster assets / Android drawable resources before merging."
  exit 1
fi

echo "TV UI design guard passed."
