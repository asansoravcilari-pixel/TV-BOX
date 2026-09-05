# ÖZDEMİR TV OS — Figma Production Parity Contract

This file is the design/runtime cross-check for the active Figma production file.

## Non-negotiable visual rules

- Final production icons and app marks shown in the TV UI are raster/image assets, not exposed SVG/vector artwork.
- Do not use Unicode glyphs as production icons.
- Home is image-first and cinematic: dark luxury-room ambience, purple light, glass surfaces, restrained text.
- Focus is TV-readable: thin bright white/purple edge, soft controlled glow, small depth/scale change.
- Keep D-pad navigation obvious and avoid mobile-style clutter or playful/game-like iconography.
- Boot/shutdown visuals show the real operation state; they are not logo-centric.

## MainActivity parity

Source: `ozdemir-launcher/app/src/main/java/com/ozdemir/tvlauncher/MainActivity.java`

### Hero actions

1. Canlı TV -> `LiveTvActivity`
2. Dizi & Film -> `DiziFilmActivity`
3. Medya -> `FilesActivity`
4. Çocuklar -> `KidsActivity`

### App row

1. SmartTube
2. YouTube
3. TRT Çocuk
4. tabii
5. Uygulamalar -> `AppsActivity`

### System row

1. Wi-Fi -> network panel
2. Bluetooth -> Android Bluetooth settings
3. Yansıt -> Android Cast settings
4. USB -> Files
5. Dosya -> Files
6. Galeri -> Files
7. Tema -> theme panel
8. Sayaç -> timer panel
9. Ayar -> settings panel
10. Güç -> power panel

### Runtime behavior to preserve in design

- Clock/date refresh.
- Network-connected/disconnected state.
- First hero receives initial focus.
- Focused item gets scale/elevation feedback.
- HOME must remain usable entirely by D-pad/OK/BACK.

## Figma production review gate

Before a screen is called final, check all of the following:

- Raster/icon asset quality at TV distance.
- Default / focused / pressed / disabled where applicable.
- Loading / error / offline / disconnected states where applicable.
- D-pad order and BACK behavior.
- Overlay depth and readability over live content.
- Consistent glass, radius, spacing, focus and typography system.
- Feature name/action matches Android runtime above.
- No placeholder symbols or prototype-only glyphs remain.

## Figma advanced-feature direction

Use Figma variables/modes for theme/state tokens, component variants for interaction states, and motion specs for focus/panel/OSD transitions. Advanced shader fills/effects may be used only as restrained ambient/pixel effects; they must not replace real raster app artwork or make the UI look game-like.
