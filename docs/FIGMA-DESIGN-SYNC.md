# ÖZDEMİR TV OS — Figma / Android Design Sync

This file is the visual implementation contract between the Figma production file and the Android launcher in this repository.

## Figma source

- File: `ÖZDEMİR TV OS — Premium TV UI`
- File key: `Q0otYXiZZqVt5EbBlz0OQP`
- Home production frame: `68:3`
- Settings production frame: `70:4`
- Live TV production frame: `135:167`

## Android source mapping

| Figma surface | Android source |
| --- | --- |
| Home | `ozdemir-launcher/app/src/main/java/com/ozdemir/tvlauncher/MainActivity.java` |
| Settings / Quick settings family | `ozdemir-launcher/app/src/main/java/com/ozdemir/tvlauncher/SettingsPanelActivity.java` |
| Live TV / channel list / playback states | `ozdemir-launcher/app/src/main/java/com/ozdemir/tvlauncher/LiveTvActivity.java` |
| Apps | `ozdemir-launcher/app/src/main/java/com/ozdemir/tvlauncher/AppsActivity.java` |
| Archive | `ozdemir-launcher/app/src/main/java/com/ozdemir/tvlauncher/ArchiveActivity.java` |
| Files / Media | `ozdemir-launcher/app/src/main/java/com/ozdemir/tvlauncher/FilesActivity.java` |
| Kids | `ozdemir-launcher/app/src/main/java/com/ozdemir/tvlauncher/KidsActivity.java` |
| Dizi & Film | `ozdemir-launcher/app/src/main/java/com/ozdemir/tvlauncher/DiziFilmActivity.java` |
| TV keyboard | `ozdemir-launcher/app/src/main/java/com/ozdemir/tvlauncher/OzdemirKeyboardService.java` |

## Non-negotiable visual rules

1. Production UI must not use Unicode characters as visual icons (`⌁`, `ᛒ`, `▣`, `◖`, `▶`, `▧`, `☺`, `✦`, `◷`, `⚙`, `⏻`, etc.).
2. Home application logos and system symbols are raster/image assets in the approved art direction. Do not replace them with crude vector placeholders.
3. Hero cards use image-heavy cinematic artwork. No geometric placeholder art in production.
4. Focus is TV-specific: thin bright edge, restrained purple/white glow, slight depth/scale. No mobile-style bounce.
5. Background remains cinematic and visible behind glass surfaces; avoid flat black/purple walls of cards.
6. Settings use a stable category rail plus contextual detail pane. Do not turn settings into a home-screen card wall.
7. Live TV keeps video visible while channel list/EPG overlays slide over it. Playback status, offline/error/loading and current-channel OSD must remain distinct states.
8. Boot/shutdown/wake/sleep visuals show the actual system operation/state, not a logo splash as the primary visual.
9. Every production screen must account for focus, pressed, disabled/unavailable, loading, error/offline and back-navigation behavior where applicable.
10. Design must remain readable at TV distance and lightweight enough for the target Android 10 / H616-class box.

## Current code gaps to remove during implementation

`MainActivity.java` currently renders multiple UI icons as text glyphs. These are temporary implementation placeholders and must be replaced by the approved raster assets before a release is considered visually complete.

`SettingsPanelActivity.java` currently renders menu icons as text glyphs and only implements a subset of the full Figma OS-shell settings architecture. Figma is the forward design target; implementation must progressively close this gap without regressing the production visual rules above.

`LiveTvActivity.java` already has the core interaction model we want to preserve: Media3 player behind the UI, right-side channel list, D-pad channel change, OK to open/close the list, playback status, catalog refresh and offline state. Figma should improve the presentation without inventing a different interaction model unless intentionally redesigned.

## Review gate

A UI change is not visually approved just because it compiles. Before calling a screen complete, compare it against its production Figma frame and verify:

- no Unicode/emoji icon placeholders remain;
- raster assets are sharp and optically centered;
- D-pad focus path is predictable;
- focus is clearly visible without covering content;
- text is readable at 1080p TV distance;
- overlays preserve context behind them;
- loading/error/offline states are designed;
- spacing/radii/glass depth match the shared system language;
- Android implementation remains performant on the target box.
