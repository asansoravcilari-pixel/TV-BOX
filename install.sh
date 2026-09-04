#!/data/data/com.termux/files/usr/bin/bash
set -euo pipefail

REPO_URL="https://github.com/asansoravcilari-pixel/TV-BOX.git"
INSTALL_DIR="$HOME/TV-BOX"
BIN_DIR="$HOME/bin"

mkdir -p "$BIN_DIR"

if ! command -v git >/dev/null 2>&1; then
  pkg update -y
  pkg install -y git
fi

if ! command -v adb >/dev/null 2>&1; then
  pkg install -y android-tools
fi

if [ ! -d "$INSTALL_DIR/.git" ]; then
  if [ "$(pwd)" != "$INSTALL_DIR" ]; then
    rm -rf "$INSTALL_DIR"
    git clone "$REPO_URL" "$INSTALL_DIR"
  fi
fi

cat > "$BIN_DIR/tvbox" <<'EOF'
#!/data/data/com.termux/files/usr/bin/bash
set -e
cd "$HOME/TV-BOX"
git pull --ff-only >/dev/null 2>&1 || true
exec bash "$HOME/TV-BOX/tvbox.sh" "$@"
EOF
chmod +x "$BIN_DIR/tvbox"

cat > "$BIN_DIR/tvupdate" <<'EOF'
#!/data/data/com.termux/files/usr/bin/bash
exec "$HOME/bin/tvbox" update
EOF
chmod +x "$BIN_DIR/tvupdate"

case ":$PATH:" in
  *":$BIN_DIR:"*) ;;
  *)
    echo 'export PATH="$HOME/bin:$PATH"' >> "$HOME/.bashrc"
    export PATH="$BIN_DIR:$PATH"
    ;;
esac

echo "Kurulum tamam. Yeni Termux oturumunda: tvupdate"
echo "Bu oturumda da: $BIN_DIR/tvupdate"
