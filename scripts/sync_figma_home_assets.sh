#!/usr/bin/env bash
set -euo pipefail

OUT="ozdemir-launcher/app/src/main/res/drawable-nodpi/home_figma.png"
mkdir -p "$(dirname "$OUT")"

# Approved Figma frame: 68:3 — ÖZDEMİR TV OS Home V6 / Production Icon Pass
# Refresh the render during this build, then the workflow commits the PNG into the repo.
URL="https://www.figma.com/api/mcp/asset/49dee8b3-ee25-47f2-83b7-25376e19d70d.png"
echo "Fetching approved Figma Home V6 render..."
curl --fail --location --retry 3 --connect-timeout 20 "$URL" --output "$OUT"
file "$OUT"
[[ -s "$OUT" ]] || { echo "home_figma.png is empty"; exit 1; }
