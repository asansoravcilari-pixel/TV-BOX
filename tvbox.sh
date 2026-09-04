#!/data/data/com.termux/files/usr/bin/bash
set -u

TV="${TVBOX_ADB:-192.168.2.85:5555}"
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
TMP_DIR="$HOME/.cache/tvbox-control"
STATE_DIR="$HOME/.tvbox-agent"
mkdir -p "$TMP_DIR" "$STATE_DIR"

connect_tv() {
  adb start-server >/dev/null 2>&1 || true
  adb connect "$TV" >/dev/null 2>&1 || true
  if ! adb -s "$TV" get-state 2>/dev/null | grep -q device; then
    echo "TV Box'a ADB ile ulasilamadi: $TV"
    echo "Telefon ve TV ayni yerel agda olmali."
    exit 1
  fi
}

has_pkg() { adb -s "$TV" shell "pm path $1" 2>/dev/null | grep -q '^package:'; }

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
    rm -rf /data/local/tmp/* /cache/* /data/cache/* /data/tombstones/* /data/anr/* /data/system/dropbox/* /data/media/awlog/* 2>/dev/null
    rm -rf /data/data/com.android.providers.downloads/cache/* /data/data/com.android.providers.downloads/code_cache/* 2>/dev/null
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
  for p in com.spocky.projengmenu mvl.studio.tvlite com.tv.mscursor com.google.android.youtube.tv com.google.android.gms com.google.android.gsf com.android.vending; do
    if has_pkg "$p"; then echo "OK   $p"; else echo "YOK  $p"; fi
  done
}

smarttube_tv() {
  connect_tv
  if has_pkg com.teamsmart.videomanager.tv; then
    echo "SmartTube zaten kurulu."
    return 0
  fi
  APK="$TMP_DIR/smarttube_stable.apk"
  URL="https://github.com/yuliskov/SmartTube/releases/download/latest/smarttube_stable.apk"
  echo "SmartTube resmi stable APK indiriliyor..."
  rm -f "$APK"
  if command -v curl >/dev/null 2>&1; then
    curl -fL --retry 3 -o "$APK" "$URL"
  elif command -v wget >/dev/null 2>&1; then
    wget -O "$APK" "$URL"
  else
    echo "curl/wget yok; SmartTube bu turda atlandi."
    return 1
  fi
  [ -s "$APK" ] || { echo "SmartTube APK indirilemedi."; return 1; }
  echo "APK boyutu: $(du -h "$APK" | awk '{print $1}')"
  adb -s "$TV" install -r "$APK"
}

ensure_home() {
  connect_tv
  if has_pkg com.spocky.projengmenu; then
    adb -s "$TV" shell 'cmd package set-home-activity com.spocky.projengmenu/.ui.home.MainActivity >/dev/null 2>&1 || true'
    echo "Projectivy HOME kontrol edildi."
  fi
}

performance_tv() {
  connect_tv
  echo "Hafif arayuz performans ayarlari uygulanıyor..."
  adb -s "$TV" shell '
    settings put global window_animation_scale 0.5 2>/dev/null || true
    settings put global transition_animation_scale 0.5 2>/dev/null || true
    settings put global animator_duration_scale 0.5 2>/dev/null || true
  '
}

safe_debloat() {
  connect_tv
  echo "Bilinen zararli ve gereksiz kullanici paketleri kontrol ediliyor..."
  for p in \
    com.android.hservice \
    com.android.server.cache \
    com.google.system.processes.gp \
    com.android.negro \
    com.apple.atve.androidtv.appletv \
    org.mozilla.tv.firefox \
    com.vstrong.iptv \
    com.next.iptv \
    x.cpe \
    com.yokatv.market \
    com.droidlogic.vsota \
    com.videosstrong.factorytest \
    com.vs.newlauncher \
    com.vstrong.resetcheck
  do
    if has_pkg "$p"; then
      adb -s "$TV" shell "pm uninstall --user 0 $p" >/dev/null 2>&1 || true
      echo "Kaldirildi/etkisiz: $p"
    fi
  done
  # Google stack, mouse cursor, Projectivy and Coji intentionally preserved.
}

ensure_agent() {
  AGENT_LOOP="$STATE_DIR/loop.sh"
  if [ ! -f "$AGENT_LOOP" ] || ! grep -q 'sleep 60' "$AGENT_LOOP" 2>/dev/null; then
    if [ -f "$ROOT_DIR/agent-install.sh" ]; then
      echo "1 dakikalik otomatik guncelleme agenti kuruluyor..."
      bash "$ROOT_DIR/agent-install.sh" || true
    fi
  fi
}

bootstrap_once() {
  FLAG="$STATE_DIR/bootstrap-v2.done"
  [ -f "$FLAG" ] && return 0
  echo "=== OTOMATIK TV BOX KURULUMU v2 ==="
  safe_debloat
  ensure_home
  performance_tv
  if smarttube_tv; then
    echo "SmartTube kurulumu tamamlandi/kontrol edildi."
  else
    echo "SmartTube kurulumu daha sonra tekrar denenecek."
    return 0
  fi
  date > "$FLAG"
}

update_all() {
  if [ -d "$ROOT_DIR/.git" ]; then git -C "$ROOT_DIR" pull --ff-only || true; fi
  ensure_agent
  connect_tv
  bootstrap_once
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
  bootstrap) bootstrap_once ;;
  performance) performance_tv ;;
  *) echo "Kullanim: tvupdate | tvbox status | clean | packages | smarttube | bootstrap | performance" ;;
esac
