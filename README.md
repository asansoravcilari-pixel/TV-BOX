# TV-BOX

Android TV Box bakım, temizlik ve özelleştirme deposu.

## Hedef
- TV Box: `192.168.2.85:5555`
- Android 10
- Projectivy Launcher korunur
- Coji TV Browser korunur
- Mouse cursor paketi korunur
- SmartTube kurulabilir/güncellenebilir
- Güvenli cache/log temizliği yapılır

## İlk kurulum
Termux'ta depoyu klonla ve kur:

```bash
git clone https://github.com/asansoravcilari-pixel/TV-BOX.git
cd TV-BOX
bash install.sh
```

Kurulumdan sonra günlük kullanım:

```bash
tvupdate
```

Ek komutlar:

```bash
tvbox status
tvbox clean
tvbox smarttube
tvbox packages
```

> Bu sistem ADB'yi yalnızca yerel ağdaki TV Box'a bağlanmak için kullanır. ADB 5555 portunu internete yönlendirmeyin.
