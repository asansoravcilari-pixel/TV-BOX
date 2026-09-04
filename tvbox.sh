#!/data/data/com.termux/files/usr/bin/bash
set -u

TV="${TVBOX_ADB:-192.168.2.85:5555}"
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
TMP_DIR="$HOME/.cache/tvbox-control"
mkdir -p "$TMP_DIR"

connect_tv() {
  adb start-server >/dev/null 2>&1 || true
  adb connect "$TV" >/dev/null 2>&1 || true
  if ! adb -s "$TV" get-state 2>/dev/null | grep -q device; then
    echo "TV Box'a ADB ile ulasilamadi: $TV"
    echo "Telefon ve TV ayni yerel agda olmali."
    exit 1
  fi
}

status_tv() {
  connect_tv
  echo "=== CIHAZ ==="
  adb -s "$TV" shell 'getprop ro.product.model; getprop ro.build.version.release; getprop ro.build.version.security_patch'
  echo
  echo "=== DEPOLAMA ==="
  adb -s "$TV" shell 'df -h /data'
  echo
  echo "=== HOME ==="
  adb -s "$TV" shell 'cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME 2>/dev/null | tail -1'
}

clean_tv() {
  connect_tv
  echo "Guvenli cache/log temizligi basliyor..."
  adb -s "$TV" root >/dev/null 2>&1 || true
  sleep 1
  adb -s "$TV" shell '
    pm trim-caches 8G 2>/dev/null || true
    rm -rf /data/local/tmp/* 2>/dev/null
    rm -rf /cache/* 2>/dev/null
    rm -rf /data/cache/* 2>/dev/null
    rm -rf /data/tombstones/* 2>/dev/null
    rm -rf /data/anr/* 2>/dev/null
    rm -rf /data/system/dropbox/* 2>/dev/null
    rm -rf /data/media/awlog/* 2>/dev/null
    rm -rf /data/data/com.android.providers.downloads/cache/* 2>/dev/null
    rm -rf /data/data/com.android.providers.downloads/code_cache/* 2>/dev/null
    find /data/data -type d -name cache -exec sh -c '\''rm -rf "$1"/*'\'' _ {} \; 2>/dev/null
    find /data/data -type d -name code_cache -exec sh -c '\''rm -rf "$1"/*'\'' _ {} \; 2>/dev/null
    df -h /data
  '
}

packages_tv() {
  connect_tv
  echo "=== UCUNCU TARAF ==="
  adb -s "$TV" shell 'pm list packages -3'
  echo
  echo "=== KORUNAN TEMEL PAKETLER ==="
  for p in \
    com.spocky.projengmenu \
    mvl.studio.tvlite \
    com.tv.mscursor \
    com.google.android.youtube.tv \
    com.google.android.gms \
    com.google.android.gsf \
    com.android.vending
  do
    if adb -s "$TV" shell "pm path $p" 2>/dev/null | grep -q '^package:'; then
      echo "OK   $p"
    else
      echo "YOK  $p"
    fi
  done
}

smarttube_tv() {
  connect_tv
  APK="$TMP_DIR/smarttube_stable.apk"
  URL="https://github.com/yuliskov/SmartTube/releases/download/latest/smarttube_stable.apk"
  echo "SmartTube resmi stable APK indiriliyor..."
  rm -f "$APK"
  if command -v curl >/dev/null 2>&1; then
    curl -fL --retry 3 -o "$APK" "$URL"
  elif command -v wget >/dev/null 2>&1; then
    wget -O "$APK" "$URL"
  else
    echo "curl veya wget gerekli. Termux: pkg install curl"
    exit 1
  fi
  echo "APK boyutu: $(du -h "$APK" | awk '{print $1}')"
  adb -s "$TV" install -r "$APK"
}

update_all() {
  if [ -d "$ROOT_DIR/.git" ]; then
    git -C "$ROOT_DIR" pull --ff-only || true
  fi
  connect_tv
  clean_tv
  echo
  packages_tv
  echo
  status_tv
  echo
  echo "TV Box bakimi tamamlandi."
}

case "${1:-help}" in
  update) update_all ;;
  status) status_tv ;;
  clean) clean_tv ;;
  packages) packages_tv ;;
  smarttube) smarttube_tv ;;
  *)
    echo "Kullanim:"
    echo "  tvupdate"
    echo "  tvbox status"
    echo "  tvbox clean"
    echo "  tvbox packages"
    echo "  tvbox smarttube"
    ;;
esac
