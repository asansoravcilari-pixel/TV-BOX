package com.ozdemir.tvlauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int DESIGN_W = 1920;
    private static final int DESIGN_H = 1080;
    private static final String PREFS = "ozdemir_tv";
    private static final String PREF_HOME_THEME = "home_theme";

    private final Handler handler = new Handler();
    private FrameLayout stage;
    private TextView clock;
    private View firstFocus;
    private ThemePalette palette;

    private static final class ThemePalette {
        final int background;
        final int surface;
        final int accent;
        final int focus;
        final int secondary;

        ThemePalette(int background, int surface, int accent, int focus) {
            this.background = background;
            this.surface = surface;
            this.accent = accent;
            this.focus = focus;
            this.secondary = 0xFFC7CDD6;
        }
    }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(1024, 1024);
        hideSystemUi();
        palette = loadPalette();
        setContentView(buildHome());
        tickClock();
    }

    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUi();
    }

    @Override protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private ThemePalette loadPalette() {
        int theme = getSharedPreferences(PREFS, MODE_PRIVATE).getInt(PREF_HOME_THEME, 0);
        if (theme == 1) return new ThemePalette(0xFF160020, 0xFF26112F, 0xFFFF3DBB, 0xFFFF3DBB);
        if (theme == 2) return new ThemePalette(0xFF000000, 0xFF101010, 0xFFFFFFFF, 0xFFFFFFFF);
        return new ThemePalette(0xFF07111F, 0xFF111D2E, 0xFF2F80FF, 0xFF2F80FF);
    }

    private void hideSystemUi() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private View buildHome() {
        FrameLayout outer = new FrameLayout(this);
        outer.setBackgroundColor(Color.BLACK);
        outer.setClipChildren(false);
        outer.setClipToPadding(false);

        stage = new FrameLayout(this);
        stage.setClipChildren(false);
        stage.setClipToPadding(false);
        stage.setBackgroundColor(palette.background);
        outer.addView(stage, new FrameLayout.LayoutParams(DESIGN_W, DESIGN_H));

        addBackdrop();
        addSystemStatus();
        addTopNavigation();
        addHero();
        addRecentRail();
        addThemeRail();
        addAmbientEdges();

        outer.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, orr, ob) -> {
            float scale = Math.min((r - l) / (float) DESIGN_W, (b - t) / (float) DESIGN_H);
            stage.setPivotX(0f);
            stage.setPivotY(0f);
            stage.setScaleX(scale);
            stage.setScaleY(scale);
            stage.setX(((r - l) - DESIGN_W * scale) / 2f);
            stage.setY(((b - t) - DESIGN_H * scale) / 2f);
        });

        outer.post(() -> { if (firstFocus != null) firstFocus.requestFocus(); });
        return outer;
    }

    private void addBackdrop() {
        View ambient = new View(this);
        GradientDrawable glow = new GradientDrawable();
        glow.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        glow.setGradientRadius(760f);
        glow.setColors(new int[]{withAlpha(palette.accent, 80), withAlpha(palette.accent, 0)});
        glow.setShape(GradientDrawable.RECTANGLE);
        glow.setCornerRadius(380f);
        ambient.setBackground(glow);
        place(ambient, 520, -260, 1320, 760);

        View scrim = new View(this);
        GradientDrawable scrimBg = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0x57000000, 0x05000000});
        scrim.setBackground(scrimBg);
        place(scrim, 0, 0, 1920, 1080);
    }

    private void addSystemStatus() {
        TextView icons = label("●   ◇   Wi-Fi   BT   Cast   ⚙   ⏻", 18, false, palette.secondary);
        icons.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        place(icons, 96, 54, 330, 34);

        clock = label("", 18, false, palette.secondary);
        clock.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        place(clock, 418, 54, 210, 34);
    }

    private void addTopNavigation() {
        int y = 100;
        addNavItem("Ana Sayfa", 96, y, 176, 57, true, () -> firstFocus.requestFocus());
        addNavItem("Canlı TV", 290, y, 153, 57, false, this::openLiveTv);
        addNavItem("Tema Merkezi", 461, y, 225, 57, false, () -> openPanel("theme"));
        addNavItem("Uygulamalar", 704, y, 206, 57, false, this::openApps);
        addNavItem("Profilim", 928, y, 142, 57, false, () -> openPanel("about"));
    }

    private void addNavItem(String text, int x, int y, int w, int h, boolean active, Runnable action) {
        TextView v = label(text, 27, active, active ? Color.WHITE : palette.secondary);
        v.setGravity(Gravity.CENTER);
        v.setFocusable(true);
        v.setClickable(true);
        v.setBackground(navDrawable(active, false));
        v.setOnClickListener(view -> action.run());
        v.setOnFocusChangeListener((view, focused) -> {
            view.animate().scaleX(focused ? 1.025f : 1f).scaleY(focused ? 1.025f : 1f)
                    .setDuration(focused ? 160 : 120).start();
            view.setBackground(navDrawable(active, focused));
        });
        place(v, x, y, w, h);
    }

    private void addHero() {
        FrameLayout hero = new FrameLayout(this);
        hero.setClipChildren(false);
        hero.setFocusable(true);
        hero.setClickable(true);
        hero.setBackground(cardDrawable(palette.surface, 32, false));
        hero.setOnClickListener(v -> ContentProviderBridge.launchOrExplain(this));
        hero.setOnFocusChangeListener((v, focused) -> {
            v.animate().scaleX(focused ? 1.015f : 1f).scaleY(focused ? 1.015f : 1f)
                    .setDuration(focused ? 170 : 130).start();
            v.setBackground(cardDrawable(palette.surface, 32, focused));
            v.setElevation(focused ? 18f : 0f);
        });
        place(hero, 96, 168, 1728, 380);

        View image = new View(this);
        GradientDrawable imageBg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{0xFF1F2B3D, 0xFF121A26});
        imageBg.setCornerRadius(28f);
        image.setBackground(imageBg);
        placeInside(hero, image, 56, 36, 1616, 220);

        TextView title = label("Öne Çıkan Yayın", 48, true, Color.WHITE);
        title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        placeInside(hero, title, 56, 274, 700, 58);
    }

    private void addRecentRail() {
        TextView heading = label("Son İzlenen Kanallar", 30, true, Color.WHITE);
        heading.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        place(heading, 96, 556, 500, 36);

        firstFocus = addMediaCard(96, 610, () -> ContentProviderBridge.launchOrExplain(this));
        addMediaCard(498, 610, this::openLiveTv);
        addMediaCard(882, 610, this::openFilms);
        addMediaCard(1266, 610, this::openKids);
    }

    private View addMediaCard(int x, int y, Runnable action) {
        FrameLayout card = new FrameLayout(this);
        card.setClipChildren(false);
        card.setFocusable(true);
        card.setClickable(true);
        card.setBackground(cardDrawable(palette.surface, 32, false));
        card.setOnClickListener(v -> action.run());
        card.setOnFocusChangeListener((v, focused) -> {
            v.animate().scaleX(focused ? 1.05f : 1f).scaleY(focused ? 1.05f : 1f)
                    .setDuration(focused ? 180 : 140).start();
            v.setElevation(focused ? 24f : 0f);
            v.setBackground(cardDrawable(palette.surface, 32, focused));
        });

        View image = new View(this);
        image.setBackground(solidDrawable(0xFF293347, 28));
        placeInside(card, image, 0, 0, 360, 162);

        TextView label = label("Yayın / Kanal", 20, true, Color.WHITE);
        label.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        placeInside(card, label, 22, 168, 300, 34);

        place(card, x, y, 360, 210);
        return card;
    }

    private void addThemeRail() {
        TextView heading = label("Hızlı Tema Seçenekleri", 30, true, Color.WHITE);
        heading.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        place(heading, 96, 839, 560, 36);

        addThemeCard("Default Dark", 0xFF2F80FF, 96, 893, 0);
        addThemeCard("Cyberpunk", 0xFFFF3DBA, 420, 893, 1);
        addThemeCard("Minimal OLED", 0xFFFFFFFF, 744, 893, 2);
    }

    private void addThemeCard(String name, int swatch, int x, int y, int themeIndex) {
        FrameLayout card = new FrameLayout(this);
        card.setFocusable(true);
        card.setClickable(true);
        card.setBackground(cardDrawable(palette.surface, 32, false));
        card.setOnClickListener(v -> {
            SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
            p.edit().putInt(PREF_HOME_THEME, themeIndex).apply();
            recreate();
        });
        card.setOnFocusChangeListener((v, focused) -> {
            v.animate().scaleX(focused ? 1.04f : 1f).scaleY(focused ? 1.04f : 1f)
                    .setDuration(focused ? 160 : 120).start();
            v.setBackground(cardDrawable(palette.surface, 32, focused));
        });

        View color = new View(this);
        color.setBackground(solidDrawable(swatch, 18));
        placeInside(card, color, 22, 20, 54, 54);

        TextView label = label(name, 22, false, Color.WHITE);
        label.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        placeInside(card, label, 22, 82, 240, 36);

        place(card, x, y, 300, 130);
    }

    private void addAmbientEdges() {
        int edge = withAlpha(palette.accent, 28);
        View top = new View(this); top.setBackgroundColor(edge); place(top, 0, 0, 1920, 16);
        View bottom = new View(this); bottom.setBackgroundColor(edge); place(bottom, 0, 1064, 1920, 16);
        View left = new View(this); left.setBackgroundColor(edge); place(left, 0, 0, 16, 1080);
        View right = new View(this); right.setBackgroundColor(edge); place(right, 1904, 0, 16, 1080);
    }

    private GradientDrawable navDrawable(boolean active, boolean focused) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(active || focused ? palette.surface : Color.TRANSPARENT);
        d.setCornerRadius(24f);
        if (focused) d.setStroke(3, palette.focus);
        return d;
    }

    private GradientDrawable cardDrawable(int color, int radius, boolean focused) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        if (focused) d.setStroke(3, palette.focus);
        return d;
    }

    private GradientDrawable solidDrawable(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    private TextView label(String text, int px, boolean bold, int color) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextColor(color);
        t.setTextSize(TypedValue.COMPLEX_UNIT_PX, px);
        t.setIncludeFontPadding(false);
        t.setSingleLine(true);
        t.setTypeface(Typeface.create("sans-serif", bold ? Typeface.BOLD : Typeface.NORMAL));
        return t;
    }

    private void place(View v, int x, int y, int w, int h) {
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(w, h);
        p.leftMargin = x;
        p.topMargin = y;
        stage.addView(v, p);
    }

    private void placeInside(FrameLayout parent, View v, int x, int y, int w, int h) {
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(w, h);
        p.leftMargin = x;
        p.topMargin = y;
        parent.addView(v, p);
    }

    private void tickClock() {
        handler.post(new Runnable() {
            @Override public void run() {
                Date now = new Date();
                if (clock != null) {
                    String text = new SimpleDateFormat("HH:mm   dd MMM", new Locale("tr", "TR")).format(now);
                    clock.setText(text);
                }
                handler.postDelayed(this, 15000);
            }
        });
    }

    private int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }

    private void openLiveTv() { startActivity(new Intent(this, LiveTvActivity.class)); }
    private void openFilms() { startActivity(new Intent(this, DiziFilmActivity.class)); }
    private void openKids() { startActivity(new Intent(this, KidsActivity.class)); }
    private void openApps() { startActivity(new Intent(this, AppsActivity.class)); }

    private void openPanel(String page) {
        Intent i = new Intent(this, SettingsPanelActivity.class);
        i.putExtra("page", page);
        startActivity(i);
    }

    private void openAndroid(String action) {
        try { startActivity(new Intent(action)); }
        catch (Exception ignored) { openPanel("settings"); }
    }
}
