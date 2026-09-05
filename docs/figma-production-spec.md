# ÖZDEMİR TV OS — Figma Production Spec

Bu dosya Figma tasarımı ile Android TV uygulamasının aynı doğrultuda kalması için kontrol listesi olarak kullanılacaktır.

## Ana yön
- Vektör/emoji/Unicode ikon görünümü kullanılmayacak.
- Uygulama markaları ve medya görselleri raster/gerçek görsel ağırlıklı olacak.
- Sistem ikonları TV seviyesinde profesyonel, tek ailede ve optik olarak dengeli olacak.
- Ana ekran sinematik, koyu mor/siyah, cam katmanlı ve uzaktan okunabilir olacak.
- Focus durumu ince parlak çerçeve + kontrollü glow + hafif scale/elevation ile gösterilecek.
- Tüm ekranlar D-pad ile doğal sırada gezilecek; focus kaybolmayacak.

## Kodla eşleştirilecek gerçek özellikler
### MainActivity
- Canlı TV
- Dizi & Film
- Medya/Dosyalar
- Çocuklar
- SmartTube / YouTube / TRT Çocuk / tabii / Uygulamalar
- Wi-Fi / Bluetooth / Yansıtma / USB / Dosya / Galeri / Tema / Sayaç / Ayarlar / Güç
- Saat + tarih

### SettingsPanelActivity
- Ağ
- Depolama
- Zamanlayıcı
- Tema
- Klavye
- Cihaz Hakkında
- Güç
- Tema isimleri: Modern Salon, Neon City, Uzay / Nebula, Mor Cam

## Üretim ekranları
- Home
- Settings shell
- Network details
- Storage / cleanup
- Timer
- Theme selector
- Keyboard setup
- About
- Power
- Quick Settings
- Notification Center
- Input Source / HDMI / Cast
- Live TV + EPG + buffering/error states
- Apps + Archive
- Files
- Kids
- Profiles
- Player states
- Boot / Shutdown / Restart / Sleep / Wake

## Motion
- Focus enter/exit kısa ve tutarlı.
- Panel ve dialog geçişleri sakin, TV odaklı.
- OSD'ler hızlı görünür, sonra otomatik fade olur.
- Reduced-motion alternatifi düşünülür.
- Boot/shutdown logo merkezli değil, gerçek sistem operasyon durumlarını gösterir.

## Figma kalite kontrol
- 1920x1080 TV safe-area korunur.
- Hero kart ölçüleri/hizaları eşit.
- Raster görsellerde crop ve keskinlik tutarlı.
- Glass yüzeylerde aynı radius, stroke, opacity ve depth sistemi kullanılır.
- Normal / focused / pressed / disabled / loading / error / offline durumları kontrol edilir.
- Kullanıcı tarafından onaylanmadan “final” denmez.
