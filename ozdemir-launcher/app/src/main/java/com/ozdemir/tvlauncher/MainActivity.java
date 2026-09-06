package com.ozdemir.tvlauncher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int DESIGN_W = 1920;
    private static final int DESIGN_H = 1080;
    private final Handler handler = new Handler();
    private FrameLayout stage;
    private TextView clock;
    private TextView date;
    private View firstFocus;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(1024, 1024);
        hideSystemUi();
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
        FrameLayout.LayoutParams stageParams = new FrameLayout.LayoutParams(DESIGN_W, DESIGN_H);
        outer.addView(stage, stageParams);

        ImageView artwork = new ImageView(this);
        artwork.setImageResource(R.drawable.home_figma);
        artwork.setScaleType(ImageView.ScaleType.FIT_XY);
        stage.addView(artwork, new FrameLayout.LayoutParams(DESIGN_W, DESIGN_H));

        addHeroZones();
        addAppZones();
        addUtilityZones();
        addStatusZones();
        addLiveClock();

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

    private void addHeroZones() {
        firstFocus = addFocusZone(88, 180, 370, 300, this::openLiveTv);
        addFocusZone(514, 180, 370, 300, this::openFilms);
        addFocusZone(940, 180, 370, 300, this::openFiles);
        addFocusZone(1366, 180, 370, 300, this::openKids);
    }

    private void addAppZones() {
        int[] x = {96, 346, 596, 846, 1096, 1346, 1596};
        Runnable[] actions = {
                () -> launch(new String[]{"com.google.android.youtube.tv", "com.google.android.youtube"}),
                () -> launch(new String[]{"com.netflix.ninja"}),
                this::openLiveTv,
                this::openFilms,
                this::openKids,
                this::openFiles,
                this::openApps
        };
        for (int i = 0; i < x.length; i++) addFocusZone(x[i], 512, 228, 154, actions[i]);
    }

    private void addUtilityZones() {
        int[] x = {96,255,414,573,732,891,1050,1209,1368,1527,1686};
        Runnable[] actions = {
                () -> {},
                () -> openAndroid(Settings.ACTION_DISPLAY_SETTINGS),
                this::openFiles,
                this::openFiles,
                this::openApps,
                () -> openAndroid(Settings.ACTION_SOUND_SETTINGS),
                () -> openAndroid(Settings.ACTION_BLUETOOTH_SETTINGS),
                () -> openPanel("network"),
                () -> openAndroid(Settings.ACTION_DISPLAY_SETTINGS),
                () -> openPanel("theme"),
                () -> openPanel("power")
        };
        for (int i = 0; i < x.length; i++) addFocusZone(x[i], 720, 138, 130, actions[i]);
    }

    private void addStatusZones() {
        addFocusZone(1396, 28, 52, 64, () -> openPanel("network"));
        addFocusZone(1452, 28, 52, 64, () -> openAndroid(Settings.ACTION_BLUETOOTH_SETTINGS));
        addFocusZone(1508, 28, 52, 64, () -> openAndroid("android.settings.CAST_SETTINGS"));
        addFocusZone(1564, 28, 66, 64, () -> openPanel("settings"));
    }

    private void addLiveClock() {
        View cover = new View(this);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xD9050409);
        bg.setCornerRadius(18f);
        place(cover, 1656, 20, 172, 80);

        clock = new TextView(this);
        clock.setTextColor(Color.WHITE);
        clock.setTextSize(40f);
        clock.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        clock.setGravity(Gravity.LEFT | Gravity.TOP);
        place(clock, 1664, 24, 150, 48);

        date = new TextView(this);
        date.setTextColor(0xB8EBE5F5);
        date.setTextSize(13f);
        date.setGravity(Gravity.LEFT | Gravity.TOP);
        place(date, 1664, 68, 160, 24);
    }

    private View addFocusZone(int x, int y, int w, int h, Runnable action) {
        View zone = new View(this);
        zone.setFocusable(true);
        zone.setClickable(true);
        zone.setBackground(clearDrawable());
        zone.setOnClickListener(v -> action.run());
        zone.setOnFocusChangeListener((v, focused) -> {
            v.animate()
                    .scaleX(focused ? 1.025f : 1f)
                    .scaleY(focused ? 1.025f : 1f)
                    .setDuration(focused ? 180 : 150)
                    .start();
            v.setElevation(focused ? 22f : 0f);
            v.setBackground(focused ? focusDrawable() : clearDrawable());
        });
        place(zone, x, y, w, h);
        return zone;
    }

    private GradientDrawable clearDrawable() {
        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.TRANSPARENT);
        d.setCornerRadius(24f);
        return d;
    }

    private GradientDrawable focusDrawable() {
        GradientDrawable d = new GradientDrawable();
        d.setColor(0x12000000);
        d.setCornerRadius(28f);
        d.setStroke(3, 0xFFF5EBFF);
        return d;
    }

    private void place(View v, int x, int y, int w, int h) {
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(w, h);
        p.leftMargin = x;
        p.topMargin = y;
        stage.addView(v, p);
    }

    private void tickClock() {
        handler.post(new Runnable() {
            @Override public void run() {
                Date now = new Date();
                if (clock != null) clock.setText(new SimpleDateFormat("HH:mm", new Locale("tr", "TR")).format(now));
                if (date != null) date.setText(new SimpleDateFormat("d MMMM · EEEE", new Locale("tr", "TR")).format(now));
                handler.postDelayed(this, 15000);
            }
        });
    }

    private void openLiveTv() { startActivity(new Intent(this, LiveTvActivity.class)); }
    private void openFilms() { startActivity(new Intent(this, DiziFilmActivity.class)); }
    private void openKids() { startActivity(new Intent(this, KidsActivity.class)); }
    private void openFiles() { startActivity(new Intent(this, FilesActivity.class)); }
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

    private void launch(String[] packages) {
        for (String pkg : packages) {
            Intent i = getPackageManager().getLaunchIntentForPackage(pkg);
            if (i != null) { startActivity(i); return; }
        }
        openApps();
    }
}
