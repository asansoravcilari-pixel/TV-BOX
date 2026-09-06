#!/usr/bin/env bash
set -euo pipefail

OUT="ozdemir-launcher/app/src/main/res/drawable-nodpi/home_figma.png"
mkdir -p "$(dirname "$OUT")"

if [[ -s "$OUT" ]]; then
  echo "Figma home asset already present: $OUT"
  exit 0
fi

URL="https://www.figma.com/api/mcp/asset/509b6b09-a75c-4408-9afc-2e378cec05c7.png"
echo "Fetching approved Figma home render..."
curl --fail --location --retry 3 --connect-timeout 20 "$URL" --output "$OUT"
file "$OUT"
[[ -s "$OUT" ]] || { echo "home_figma.png is empty"; exit 1; }
