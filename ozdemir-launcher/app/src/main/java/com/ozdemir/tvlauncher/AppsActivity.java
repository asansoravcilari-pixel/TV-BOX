package com.ozdemir.tvlauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppsActivity extends Activity {
    private static final int DESIGN_W = 1920;
    private static final int DESIGN_H = 1080;
    private static final int BG = 0xFF07111F;
    private static final int SURFACE = 0xFF111D2E;
    private static final int SURFACE_2 = 0xFF17253A;
    private static final int ACCENT = 0xFF2F80FF;
    private static final int TEXT_2 = 0xFFC7CDD6;

    private FrameLayout stage;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideSystemUi();
        setContentView(buildUi());
    }

    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUi();
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

    private View buildUi() {
        FrameLayout outer = new FrameLayout(this);
        outer.setBackgroundColor(Color.BLACK);
        outer.setClipChildren(false);
        outer.setClipToPadding(false);

        stage = new FrameLayout(this);
        stage.setBackgroundColor(BG);
        stage.setClipChildren(false);
        stage.setClipToPadding(false);
        outer.addView(stage, new FrameLayout.LayoutParams(DESIGN_W, DESIGN_H));

        TextView title = text("Uygulamalar", 46, true, Color.WHITE);
        place(title, 96, 64, 520, 58);

        addTab("Yerel Mağaza", 96, 135, false);
        addTab("Yüklü", 248, 135, true);
        addTab("Güncellemeler", 350, 135, false);
        addTab("Kaynaklar", 526, 135, false);

        FrameLayout source = new FrameLayout(this);
        source.setBackground(round(SURFACE, 22, 0, 0));
        place(source, 96, 205, 700, 82);
        TextView sourceLabel = text("Aktif Kaynak:   Cihaz / Dahili Depolama", 18, true, TEXT_2);
        placeInside(source, sourceLabel, 24, 0, 540, 82);
        TextView sourceState = text("• Gerçek uygulama ikonları", 18, true, ACCENT);
        placeInside(source, sourceState, 420, 0, 260, 82);

        ScrollView scroll = new ScrollView(this);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setFillViewport(false);
        scroll.setBackgroundColor(Color.TRANSPARENT);
        FrameLayout.LayoutParams sp = new FrameLayout.LayoutParams(1728, 655);
        sp.leftMargin = 96;
        sp.topMargin = 315;
        stage.addView(scroll, sp);

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        grid.setPadding(0, 0, 0, 30);
        scroll.addView(grid, new ScrollView.LayoutParams(-1, -2));

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> installed = new ArrayList<>(pm.getInstalledApplications(0));
        Collections.sort(installed, Comparator.comparing(a -> pm.getApplicationLabel(a).toString().toLowerCase()));

        View first = null;
        int count = 0;
        for (ApplicationInfo app : installed) {
            Intent launch = pm.getLaunchIntentForPackage(app.packageName);
            if (launch == null || app.packageName.equals(getPackageName())) continue;
            String label = pm.getApplicationLabel(app).toString();
            Drawable icon;
            try { icon = pm.getApplicationIcon(app.packageName); }
            catch (Exception e) { icon = pm.getDefaultActivityIcon(); }

            View card = appCard(label, app.packageName, icon, launch);
            GridLayout.LayoutParams gp = new GridLayout.LayoutParams();
            gp.width = 390;
            gp.height = 230;
            gp.setMargins(0, 0, 24, 18);
            grid.addView(card, gp);
            if (first == null) first = card;
            count++;
        }

        if (count == 0) {
            TextView empty = text("Açılabilir uygulama bulunamadı", 24, true, TEXT_2);
            grid.addView(empty, new GridLayout.LayoutParams());
        }

        FrameLayout status = new FrameLayout(this);
        status.setBackground(round(SURFACE, 22, 0, 0));
        place(status, 96, 980, 980, 70);
        TextView statusText = text("Yüklü uygulamalar   •   Paket bilgisi   •   Sistemden alınan gerçek ikonlar", 17, false, TEXT_2);
        placeInside(status, statusText, 24, 0, 930, 70);

        addAmbientEdges();

        View initial = first;
        if (initial != null) stage.post(initial::requestFocus);

        outer.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, orr, ob) -> {
            float scale = Math.min((r - l) / (float) DESIGN_W, (b - t) / (float) DESIGN_H);
            stage.setPivotX(0f);
            stage.setPivotY(0f);
            stage.setScaleX(scale);
            stage.setScaleY(scale);
            stage.setX(((r - l) - DESIGN_W * scale) / 2f);
            stage.setY(((b - t) - DESIGN_H * scale) / 2f);
        });

        return outer;
    }

    private void addTab(String label, int x, int y, boolean active) {
        TextView tab = text(label, 18, true, active ? Color.WHITE : TEXT_2);
        tab.setGravity(Gravity.CENTER);
        tab.setBackground(round(active ? ACCENT : SURFACE, 18, 0, 0));
        int w = label.equals("Güncellemeler") ? 160 : label.equals("Yerel Mağaza") ? 140 : 96;
        place(tab, x, y, w, 48);
    }

    private View appCard(String name, String pkg, Drawable iconDrawable, Intent launch) {
        FrameLayout card = new FrameLayout(this);
        card.setFocusable(true);
        card.setFocusableInTouchMode(true);
        card.setClickable(true);
        card.setBackground(round(SURFACE, 28, 0, 0));

        ImageView icon = new ImageView(this);
        icon.setImageDrawable(iconDrawable);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        icon.setBackground(round(SURFACE_2, 18, 0, 0));
        icon.setPadding(10, 10, 10, 10);
        placeInside(card, icon, 24, 22, 84, 84);

        TextView label = text(name, 24, true, Color.WHITE);
        label.setSingleLine(true);
        placeInside(card, label, 24, 120, 340, 36);

        TextView packageName = text(pkg, 14, false, TEXT_2);
        packageName.setSingleLine(true);
        placeInside(card, packageName, 24, 160, 340, 28);

        TextView state = text("Yüklü", 16, true, ACCENT);
        placeInside(card, state, 24, 192, 140, 28);

        card.setOnClickListener(v -> {
            Intent i = new Intent(launch);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(i);
        });
        card.setOnFocusChangeListener((v, focused) -> {
            v.animate().scaleX(focused ? 1.05f : 1f).scaleY(focused ? 1.05f : 1f)
                    .setDuration(focused ? 160 : 120).start();
            v.setElevation(focused ? 24f : 0f);
            v.setBackground(round(SURFACE, 28, focused ? 3 : 0, focused ? ACCENT : 0));
        });
        return card;
    }

    private void addAmbientEdges() {
        View top = new View(this); top.setBackgroundColor(0x242F80FF); place(top, 0, 0, 1920, 8);
        View bottom = new View(this); bottom.setBackgroundColor(0x242F80FF); place(bottom, 0, 1072, 1920, 8);
        View left = new View(this); left.setBackgroundColor(0x242F80FF); place(left, 0, 0, 8, 1080);
        View right = new View(this); right.setBackgroundColor(0x242F80FF); place(right, 1912, 0, 8, 1080);
    }

    private TextView text(String s, int px, boolean bold, int color) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextColor(color);
        t.setTextSize(TypedValue.COMPLEX_UNIT_PX, px);
        t.setIncludeFontPadding(false);
        t.setGravity(Gravity.CENTER_VERTICAL);
        t.setTypeface(Typeface.create("sans-serif", bold ? Typeface.BOLD : Typeface.NORMAL));
        return t;
    }

    private GradientDrawable round(int color, int radius, int strokeWidth, int strokeColor) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        if (strokeWidth > 0) d.setStroke(strokeWidth, strokeColor);
        return d;
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
}
