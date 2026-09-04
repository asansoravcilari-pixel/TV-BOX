#!/data/data/com.termux/files/usr/bin/bash
set -e

REPO="$HOME/TV-BOX"
AGENT="$HOME/.tvbox-agent"
mkdir -p "$AGENT"

cat > "$AGENT/check.sh" <<'EOF'
#!/data/data/com.termux/files/usr/bin/bash
set -u
REPO="$HOME/TV-BOX"
LOG="$HOME/.tvbox-agent/agent.log"
mkdir -p "$(dirname "$LOG")"
[ -d "$REPO/.git" ] || exit 0
cd "$REPO" || exit 0
OLD=$(git rev-parse HEAD 2>/dev/null || true)
git fetch origin main --quiet 2>>"$LOG" || exit 0
NEW=$(git rev-parse origin/main 2>/dev/null || true)
[ -n "$NEW" ] || exit 0
[ "$OLD" = "$NEW" ] && exit 0
git reset --hard origin/main >>"$LOG" 2>&1 || exit 0
bash "$REPO/tvbox.sh" update >>"$LOG" 2>&1 || true
echo "$(date '+%F %T') applied $NEW" >>"$LOG"
EOF
chmod +x "$AGENT/check.sh"

cat > "$AGENT/loop.sh" <<'EOF'
#!/data/data/com.termux/files/usr/bin/bash
while true; do
  "$HOME/.tvbox-agent/check.sh" || true
  sleep 60
done
EOF
chmod +x "$AGENT/loop.sh"

mkdir -p "$HOME/.termux/boot"
cat > "$HOME/.termux/boot/tvbox-agent" <<'EOF'
#!/data/data/com.termux/files/usr/bin/bash
pkill -f "$HOME/.tvbox-agent/loop.sh" 2>/dev/null || true
nohup "$HOME/.tvbox-agent/loop.sh" >/dev/null 2>&1 &
EOF
chmod +x "$HOME/.termux/boot/tvbox-agent"

pkill -f "$AGENT/loop.sh" 2>/dev/null || true
nohup "$AGENT/loop.sh" >/dev/null 2>&1 &
echo $! > "$AGENT/pid"
echo "TV-BOX Agent aktif: GitHub her 60 saniyede kontrol edilecek."
echo "Log: $AGENT/agent.log"
echo "Not: Telefon yeniden basladiginda otomatik baslamasi icin Termux:Boot gerekir."
