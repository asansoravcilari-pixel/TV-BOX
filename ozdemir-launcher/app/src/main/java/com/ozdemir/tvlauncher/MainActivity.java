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
            {0xFF120818, 0xFF2B0F3D, 0xFF090711},
            {0xFF070A1D, 0xFF26134A, 0xFF080913},
            {0xFF090A18, 0xFF30124B, 0xFF10051D},
            {0xFF17071F, 0xFF451760, 0xFF0B0712}
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
        root.setPadding(dp(44), dp(26), dp(44), dp(22));
        applyTheme();

        LinearLayout header = row();
        LinearLayout brandBlock = new LinearLayout(this);
        brandBlock.setOrientation(LinearLayout.VERTICAL);
        TextView brand = text("ÖZDEMİR TV", 27, true);
        brand.setTextColor(Color.WHITE);
        TextView slogan = text("HAYAT DAİMA DAHA FAZLASI İÇİN...", 11, false);
        slogan.setTextColor(0xFFCC9DEB);
        brandBlock.addView(brand);
        brandBlock.addView(slogan);
        header.addView(brandBlock, new LinearLayout.LayoutParams(0, dp(58), 1f));

        networkView = text("Ağ kontrol ediliyor", 15, false);
        networkView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        header.addView(networkView, new LinearLayout.LayoutParams(dp(230), dp(58)));

        LinearLayout timeBlock = new LinearLayout(this);
        timeBlock.setOrientation(LinearLayout.VERTICAL);
        timeBlock.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        clockView = text("--:--", 28, true);
        clockView.setGravity(Gravity.RIGHT);
        dateView = text("", 12, false);
        dateView.setTextColor(0xFFBFA9CF);
        dateView.setGravity(Gravity.RIGHT);
        timeBlock.addView(clockView);
        timeBlock.addView(dateView);
        header.addView(timeBlock, new LinearLayout.LayoutParams(dp(150), dp(58)));
        root.addView(header);

        root.addView(section("Ana"));
        LinearLayout main = row();
        main.addView(card("Canlı TV", "TV uygulaması", () -> launchFirst(
                new String[]{"nl.studio.tvLite", "com.tv.browser"},
                "Canlı TV uygulaması bulunamadı."), true), weightCard());
        main.addView(card("SmartTube", "YouTube", () -> launchFirst(
                new String[]{"org.smarttuber.stable", "com.teamsmart.videomanager.tv"},
                "SmartTube kurulu değil."), true), weightCard());
        main.addView(card("coji", "Tarayıcı / içerik", () -> launchFirst(
                new String[]{"com.tv.browser", "nl.studio.tvLite"},
                "coji uygulaması bulunamadı."), true), weightCard());
        main.addView(card("Medya", "USB / dosyalar", this::openFiles, true), weightCard());
        root.addView(main, new LinearLayout.LayoutParams(-1, 0, 1.7f));

        root.addView(section("Çocuklar"));
        LinearLayout kids = row();
        kids.addView(card("YouTube Kids", "", () -> launchFirst(new String[]{
                "com.google.android.apps.youtube.kids", "com.google.android.youtube.tvkids"},
                "YouTube Kids kurulu değil."), false), weightCard());
        kids.addView(card("TRT Çocuk", "", () -> launchFirst(new String[]{
                "com.trtcocuk.mobile", "com.trtcocuk"}, "TRT Çocuk kurulu değil."), false), weightCard());
        kids.addView(card("Maşa", "", () -> missing("Maşa uygulaması için paket adı eklenecek."), false), weightCard());
        kids.addView(card("Pepee", "", () -> missing("Pepee uygulaması için paket adı eklenecek."), false), weightCard());
        kids.addView(card("Oyunlar", "", () -> openSettings(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS), false), weightCard());
        kids.addView(card("Tümünü Gör", "", () -> openSettings(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS), false), weightCard());
        root.addView(kids, new LinearLayout.LayoutParams(-1, 0, 1.15f));

        root.addView(section("Sistem"));
        LinearLayout bottom = row();
        bottom.addView(card("Uygulamalar", "", () -> openSettings(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS), false), weightCard());
        bottom.addView(card("Dosyalar", "", this::openFiles, false), weightCard());
        bottom.addView(card("Ağ", "", () -> openSettings(Settings.ACTION_WIFI_SETTINGS), false), weightCard());
        bottom.addView(card("Depolama", "", () -> openSettings(Settings.ACTION_INTERNAL_STORAGE_SETTINGS), false), weightCard());
        bottom.addView(card("Zamanlayıcı", "", () -> missing("Uyku zamanlayıcısı sonraki sürümde bağlanacak."), false), weightCard());
        bottom.addView(card("Tema", "", this::nextTheme, false), weightCard());
        bottom.addView(card("Ayarlar", "", () -> openSettings(Settings.ACTION_SETTINGS), false), weightCard());
        bottom.addView(card("Güç", "", () -> missing("Güç işlemleri sistem yetkisi gerektiriyor."), false), weightCard());
        root.addView(bottom, new LinearLayout.LayoutParams(-1, 0, 0.95f));

        TextView footer = text("ÖZDEMİR TV OS  •  Android 10  •  2 GB RAM  •  Allwinner H616", 10, false);
        footer.setTextColor(0xFF9B8BA8);
        footer.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        root.addView(footer, new LinearLayout.LayoutParams(-1, dp(24)));

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

    private LinearLayout.LayoutParams weightCard() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, -1, 1f);
        p.setMargins(dp(6), dp(5), dp(6), dp(5));
        return p;
    }

    private TextView section(String label) {
        TextView t = text(label, 16, true);
        t.setTextColor(0xFFC9A7DD);
        t.setGravity(Gravity.BOTTOM | Gravity.LEFT);
        t.setPadding(dp(6), dp(6), 0, dp(2));
        return t;
    }

    private View card(String title, String subtitle, Runnable action, boolean large) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER_VERTICAL);
        box.setPadding(dp(18), dp(13), dp(15), dp(13));
        box.setFocusable(true);
        box.setFocusableInTouchMode(true);
        box.setClickable(true);
        box.setBackground(cardBackground(false));

        TextView titleView = text(title, large ? 22 : 16, true);
        box.addView(titleView);
        if (subtitle != null && !subtitle.isEmpty()) {
            TextView sub = text(subtitle, large ? 13 : 11, false);
            sub.setTextColor(0xFFB9A9C5);
            sub.setPadding(0, dp(5), 0, 0);
            box.addView(sub);
        }

        box.setOnClickListener(v -> action.run());
        box.setOnFocusChangeListener((v, focused) -> {
            v.animate().scaleX(focused ? 1.045f : 1f).scaleY(focused ? 1.045f : 1f).setDuration(120).start();
            v.setBackground(cardBackground(focused));
            v.setElevation(focused ? dp(12) : dp(2));
        });
        return box;
    }

    private GradientDrawable cardBackground(boolean focused) {
        GradientDrawable d = new GradientDrawable();
        d.setCornerRadius(dp(18));
        d.setColor(focused ? 0xE83A1854 : 0xB01A1022);
        d.setStroke(dp(focused ? 2 : 1), focused ? 0xFFE6B5FF : 0x556F4B82);
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
        networkView.setTextColor(online ? 0xFFD9B2EF : 0xFFB0A5B8);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
