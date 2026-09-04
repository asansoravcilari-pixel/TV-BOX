package com.ozdemir.tvlauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private final Handler clockHandler = new Handler();
    private TextView clockView;
    private TextView dateView;
    private TextView networkView;
    private LinearLayout root;
    private SharedPreferences prefs;
    private int themeIndex;

    private final int[][] themes = new int[][]{
            {0xFF120818, 0xFF2C0E40, 0xFF08060E},
            {0xFF070A1B, 0xFF20113D, 0xFF08070F},
            {0xFF090A18, 0xFF36144F, 0xFF0D0715},
            {0xFF16071E, 0xFF4A1764, 0xFF0A0610}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        prefs = getSharedPreferences("ozdemir_tv", MODE_PRIVATE);
        themeIndex = prefs.getInt("theme", 0);
        hideSystemUi();
        setContentView(buildHome());
        startClock();
    }

    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUi();
    }

    @Override protected void onResume() {
        super.onResume();
        updateNetwork();
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
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(34), dp(20), dp(34), dp(14));
        applyTheme();

        LinearLayout header = row();
        header.setPadding(dp(4), 0, dp(4), 0);

        LinearLayout brandBlock = new LinearLayout(this);
        brandBlock.setOrientation(LinearLayout.VERTICAL);
        brandBlock.setGravity(Gravity.CENTER_VERTICAL);
        TextView brand = text("ÖZDEMİR TV", 25, true);
        brand.setTextColor(Color.WHITE);
        TextView slogan = text("HAYAT DAİMA DAHA FAZLASI İÇİN...", 10, false);
        slogan.setTextColor(0xFFD6B8EA);
        slogan.setLetterSpacing(0.035f);
        brandBlock.addView(brand);
        brandBlock.addView(slogan);
        header.addView(brandBlock, new LinearLayout.LayoutParams(0, dp(60), 1f));

        LinearLayout centerStatus = new LinearLayout(this);
        centerStatus.setOrientation(LinearLayout.VERTICAL);
        centerStatus.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        networkView = text("Ağ kontrol ediliyor", 13, false);
        networkView.setGravity(Gravity.RIGHT);
        TextView rightSlogan = text("DAHA HIZLI  •  DAHA AKICI  •  DAHA SENİN", 9, true);
        rightSlogan.setTextColor(0xFFAA8CBD);
        rightSlogan.setGravity(Gravity.RIGHT);
        rightSlogan.setLetterSpacing(0.05f);
        centerStatus.addView(networkView);
        centerStatus.addView(rightSlogan);
        header.addView(centerStatus, new LinearLayout.LayoutParams(dp(320), dp(60)));

        LinearLayout timeBlock = new LinearLayout(this);
        timeBlock.setOrientation(LinearLayout.VERTICAL);
        timeBlock.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        clockView = text("--:--", 27, true);
        clockView.setGravity(Gravity.RIGHT);
        dateView = text("", 11, false);
        dateView.setTextColor(0xFFC2ADC9);
        dateView.setGravity(Gravity.RIGHT);
        timeBlock.addView(clockView);
        timeBlock.addView(dateView);
        header.addView(timeBlock, new LinearLayout.LayoutParams(dp(132), dp(60)));
        root.addView(header);

        root.addView(section("Ana"));
        LinearLayout main = row();
        main.addView(card("Canlı TV", "TV uygulaması", () -> launchFirst(
                new String[]{"nl.studio.tvLite", "com.tv.browser"},
                "Canlı TV uygulaması bulunamadı."), true), weightCard(8, 7));
        main.addView(card("SmartTube", "YouTube", () -> launchFirst(
                new String[]{"org.smarttuber.stable", "com.teamsmart.videomanager.tv"},
                "SmartTube kurulu değil."), true), weightCard(8, 7));
        main.addView(card("coji", "Tarayıcı / içerik", () -> launchFirst(
                new String[]{"com.tv.browser", "nl.studio.tvLite"},
                "coji uygulaması bulunamadı."), true), weightCard(8, 7));
        main.addView(card("Medya", "USB / dosyalar", this::openFiles, true), weightCard(8, 7));
        root.addView(main, new LinearLayout.LayoutParams(-1, 0, 1.9f));

        root.addView(section("Çocuklar"));
        LinearLayout kids = row();
        kids.addView(card("YouTube Kids", "", () -> launchFirst(new String[]{
                "com.google.android.apps.youtube.kids", "com.google.android.youtube.tvkids"},
                "YouTube Kids kurulu değil."), false), weightCard(5, 5));
        kids.addView(card("TRT Çocuk", "", () -> launchFirst(new String[]{
                "com.trtcocuk.mobile", "com.trtcocuk"}, "TRT Çocuk kurulu değil."), false), weightCard(5, 5));
        kids.addView(card("Maşa", "", () -> missing("Maşa uygulaması için paket adı eklenecek."), false), weightCard(5, 5));
        kids.addView(card("Pepee", "", () -> missing("Pepee uygulaması için paket adı eklenecek."), false), weightCard(5, 5));
        kids.addView(card("Oyunlar", "", () -> openSettings(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS), false), weightCard(5, 5));
        kids.addView(card("Tümünü Gör", "", () -> openSettings(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS), false), weightCard(5, 5));
        root.addView(kids, new LinearLayout.LayoutParams(-1, 0, 1.18f));

        root.addView(section("Sistem"));
        LinearLayout bottom = row();
        bottom.addView(card("Uygulamalar", "", () -> openSettings(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS), false), weightCard(4, 4));
        bottom.addView(card("Dosyalar", "", this::openFiles, false), weightCard(4, 4));
        bottom.addView(card("Ağ", "", () -> openSettings(Settings.ACTION_WIFI_SETTINGS), false), weightCard(4, 4));
        bottom.addView(card("Depolama", "", () -> openSettings(Settings.ACTION_INTERNAL_STORAGE_SETTINGS), false), weightCard(4, 4));
        bottom.addView(card("Zamanlayıcı", "", () -> missing("Uyku zamanlayıcısı sonraki sürümde bağlanacak."), false), weightCard(4, 4));
        bottom.addView(card("Tema", "", this::nextTheme, false), weightCard(4, 4));
        bottom.addView(card("Ayarlar", "", () -> openSettings(Settings.ACTION_SETTINGS), false), weightCard(4, 4));
        bottom.addView(card("Güç", "", () -> missing("Güç işlemleri sistem yetkisi gerektiriyor."), false), weightCard(4, 4));
        root.addView(bottom, new LinearLayout.LayoutParams(-1, 0, 0.93f));

        TextView footer = text("ÖZDEMİR TV OS  •  Android 10  •  2 GB RAM  •  Allwinner H616", 9, false);
        footer.setTextColor(0xFF9689A0);
        footer.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        root.addView(footer, new LinearLayout.LayoutParams(-1, dp(22)));

        View first = main.getChildAt(0);
        if (first != null) first.requestFocus();
        return root;
    }

    private void nextTheme() {
        themeIndex = (themeIndex + 1) % themes.length;
        prefs.edit().putInt("theme", themeIndex).apply();
        applyTheme();
        Toast.makeText(this, "Tema " + (themeIndex + 1) + "/4", Toast.LENGTH_SHORT).show();
    }

    private void applyTheme() {
        if (root == null) return;
        int[] c = themes[themeIndex];
        GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.TL_BR, c);
        root.setBackground(bg);
    }

    private LinearLayout row() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }

    private LinearLayout.LayoutParams weightCard(int horizontal, int vertical) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, -1, 1f);
        p.setMargins(dp(horizontal), dp(vertical), dp(horizontal), dp(vertical));
        return p;
    }

    private TextView section(String label) {
        TextView t = text(label, 14, true);
        t.setTextColor(0xFFD8B8E9);
        t.setGravity(Gravity.BOTTOM | Gravity.LEFT);
        t.setPadding(dp(8), dp(5), 0, 0);
        t.setLetterSpacing(0.025f);
        return t;
    }

    private View card(String title, String subtitle, Runnable action, boolean large) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER_VERTICAL);
        box.setPadding(dp(18), dp(13), dp(16), dp(13));
        box.setFocusable(true);
        box.setFocusableInTouchMode(true);
        box.setClickable(true);
        box.setBackground(cardBackground(false));
        box.setElevation(dp(2));

        TextView titleView = text(title, large ? 20 : 14, true);
        titleView.setSingleLine(true);
        box.addView(titleView);
        if (subtitle != null && !subtitle.isEmpty()) {
            TextView sub = text(subtitle, large ? 12 : 10, false);
            sub.setTextColor(0xFFBCAAC6);
            sub.setPadding(0, dp(5), 0, 0);
            sub.setSingleLine(true);
            box.addView(sub);
        }

        box.setOnClickListener(v -> action.run());
        box.setOnFocusChangeListener((v, focused) -> {
            v.animate().scaleX(focused ? 1.055f : 1f).scaleY(focused ? 1.055f : 1f).setDuration(115).start();
            v.setBackground(cardBackground(focused));
            v.setElevation(focused ? dp(14) : dp(2));
        });
        return box;
    }

    private GradientDrawable cardBackground(boolean focused) {
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                focused
                        ? new int[]{0xF05E1AAA, 0xF03C1268}
                        : new int[]{0xC1261833, 0xB9140E1D});
        d.setCornerRadius(dp(17));
        d.setStroke(dp(focused ? 2 : 1), focused ? 0xFFF2D7FF : 0x665D3B70);
        return d;
    }

    private TextView text(String s, int sp, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextColor(0xFFF8F3FA);
        t.setTextSize(sp);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setGravity(Gravity.CENTER_VERTICAL);
        return t;
    }

    private void launchFirst(String[] packages, String message) {
        for (String pkg : packages) {
            Intent i = getPackageManager().getLaunchIntentForPackage(pkg);
            if (i != null) {
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                return;
            }
        }
        missing(message);
    }

    private void openFiles() {
        try {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivity(i);
        } catch (Exception e) {
            openSettings(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
        }
    }

    private void openSettings(String action) {
        try {
            startActivity(new Intent(action));
        } catch (Exception e) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    private void missing(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void startClock() {
        clockHandler.post(new Runnable() {
            @Override public void run() {
                Date now = new Date();
                if (clockView != null) clockView.setText(new SimpleDateFormat("HH:mm", new Locale("tr", "TR")).format(now));
                if (dateView != null) dateView.setText(new SimpleDateFormat("d MMM EEE", new Locale("tr", "TR")).format(now));
                updateNetwork();
                clockHandler.postDelayed(this, 15000);
            }
        });
    }

    private void updateNetwork() {
        if (networkView == null) return;
        boolean online = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            Network n = cm.getActiveNetwork();
            NetworkCapabilities caps = n == null ? null : cm.getNetworkCapabilities(n);
            online = caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        } catch (Exception ignored) { }
        networkView.setText(online ? "● Wi‑Fi / İnternet bağlı" : "○ Çevrimdışı");
        networkView.setTextColor(online ? 0xFFE2C7F2 : 0xFFB0A5B8);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
