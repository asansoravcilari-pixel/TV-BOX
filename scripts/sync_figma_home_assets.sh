#!/usr/bin/env bash
set -euo pipefail

OUT="ozdemir-launcher/app/src/main/res/drawable-nodpi/home_figma.png"
mkdir -p "$(dirname "$OUT")"

# Current Figma production home: 135:2 — ÖZDEMİR TV OS — Home V9 / Raster Production
# Refresh the render during this build, then the workflow commits the PNG into the repo.
URL="https://www.figma.com/api/mcp/asset/d3e5f652-f877-4296-87f8-f09e3114dd8a.png"
echo "Fetching Figma Home V9 production render..."
curl --fail --location --retry 3 --connect-timeout 20 "$URL" --output "$OUT"
file "$OUT"
[[ -s "$OUT" ]] || { echo "home_figma.png is empty"; exit 1; }
