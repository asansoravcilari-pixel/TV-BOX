package com.ozdemir.tvlauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    private TextView networkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideSystemUi();
        setContentView(buildHome());
        startClock();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUi();
    }

    @Override
    protected void onResume() {
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
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(42), dp(30), dp(42), dp(28));
        root.setBackgroundColor(Color.rgb(10, 13, 18));

        LinearLayout header = row();
        TextView brand = text("ÖZDEMİR TV", 25, true);
        header.addView(brand, new LinearLayout.LayoutParams(0, dp(52), 1f));

        networkView = text("Ağ kontrol ediliyor", 16, false);
        networkView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        header.addView(networkView, new LinearLayout.LayoutParams(dp(300), dp(52)));

        clockView = text("--:--", 27, true);
        clockView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        header.addView(clockView, new LinearLayout.LayoutParams(dp(150), dp(52)));
        root.addView(header);

        root.addView(section("Ana"));
        LinearLayout main = row();
        main.addView(card("Canlı TV", "TV kaynağı seçilecek", () -> missing("Canlı TV uygulaması henüz seçilmedi."), true), weightCard());
        main.addView(card("SmartTube", "YouTube", () -> launchPackage("com.teamsmart.videomanager.tv"), true), weightCard());
        main.addView(card("Coji", "Web tarayıcı", () -> launchPackage("mvl.studio.tvlite"), true), weightCard());
        main.addView(card("Medya", "USB / dosyalar", this::openFiles, true), weightCard());
        root.addView(main, new LinearLayout.LayoutParams(-1, 0, 1.7f));

        root.addView(section("Çocuklar"));
        LinearLayout kids = row();
        kids.addView(card("YouTube Kids", "Çocuk uygulaması varsa aç", () -> launchFirst(
                new String[]{"com.google.android.apps.youtube.kids", "com.google.android.youtube.tvkids"},
                "YouTube Kids bu cihazda kurulu değil."), false), weightCard());
        kids.addView(card("Çocuk Medya", "Yerel içerik", this::openFiles, false), weightCard());
        kids.addView(card("Eğitim", "Uygulama seçilecek", () -> missing("Eğitim uygulaması henüz seçilmedi."), false), weightCard());
        kids.addView(card("Favoriler", "Çocuk kısayolları", () -> missing("Çocuk favorileri sonraki aşamada yapılandırılacak."), false), weightCard());
        root.addView(kids, new LinearLayout.LayoutParams(-1, 0, 1.15f));

        root.addView(section("Sistem"));
        LinearLayout bottom = row();
        bottom.addView(card("Uygulamalar", "", () -> openSettings(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS), false), weightCard());
        bottom.addView(card("Dosyalar", "", this::openFiles, false), weightCard());
        bottom.addView(card("Ağ", "", () -> openSettings(Settings.ACTION_WIFI_SETTINGS), false), weightCard());
        bottom.addView(card("Depolama", "", () -> openSettings(Settings.ACTION_INTERNAL_STORAGE_SETTINGS), false), weightCard());
        bottom.addView(card("Zamanlayıcı", "", () -> missing("Uyku zamanlayıcısı yardımcı bileşeni Phase 4'te eklenecek."), false), weightCard());
        bottom.addView(card("Ayarlar", "", () -> openSettings(Settings.ACTION_SETTINGS), false), weightCard());
        bottom.addView(card("Güç", "", () -> missing("Güç menüsü yardımcı bileşeni Phase 4'te eklenecek."), false), weightCard());
        root.addView(bottom, new LinearLayout.LayoutParams(-1, 0, 0.95f));

        return root;
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
        TextView t = text(label, 17, true);
        t.setTextColor(Color.rgb(166, 177, 194));
        t.setGravity(Gravity.BOTTOM | Gravity.LEFT);
        t.setPadding(dp(6), dp(7), 0, dp(3));
        return t;
    }

    private View card(String title, String subtitle, Runnable action, boolean large) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER_VERTICAL);
        box.setPadding(dp(20), dp(14), dp(16), dp(14));
        box.setFocusable(true);
        box.setClickable(true);
        box.setBackground(cardBackground(false));

        TextView titleView = text(title, large ? 23 : 18, true);
        box.addView(titleView);
        if (subtitle != null && !subtitle.isEmpty()) {
            TextView sub = text(subtitle, large ? 14 : 12, false);
            sub.setTextColor(Color.rgb(156, 166, 182));
            sub.setPadding(0, dp(5), 0, 0);
            box.addView(sub);
        }

        box.setOnClickListener(v -> action.run());
        box.setOnFocusChangeListener((v, focused) -> {
            v.setBackground(cardBackground(focused));
            v.setScaleX(focused ? 1.035f : 1f);
            v.setScaleY(focused ? 1.035f : 1f);
        });
        return box;
    }

    private GradientDrawable cardBackground(boolean focused) {
        GradientDrawable d = new GradientDrawable();
        d.setCornerRadius(dp(18));
        d.setColor(focused ? Color.rgb(42, 55, 74) : Color.rgb(25, 31, 41));
        d.setStroke(dp(focused ? 2 : 1), focused ? Color.rgb(215, 225, 240) : Color.rgb(53, 63, 78));
        return d;
    }

    private TextView text(String s, int sp, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextColor(Color.rgb(239, 243, 248));
        t.setTextSize(sp);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setGravity(Gravity.CENTER_VERTICAL);
        return t;
    }

    private void launchPackage(String pkg) {
        Intent i = getPackageManager().getLaunchIntentForPackage(pkg);
        if (i == null) {
            missing(pkg + " kurulu değil.");
            return;
        }
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
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
                if (clockView != null) {
                    clockView.setText(new SimpleDateFormat("HH:mm", new Locale("tr", "TR")).format(new Date()));
                }
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
        networkView.setText(online ? "● İnternet bağlı" : "○ Çevrimdışı");
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
