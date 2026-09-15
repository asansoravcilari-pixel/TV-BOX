package com.ozdemir.tvshortcuts;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public final class ShortcutActivities {
    private ShortcutActivities() {}

    public static abstract class RedirectActivity extends Activity {
        protected abstract String targetPackage();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            String pkg = targetPackage();
            try {
                PackageManager pm = getPackageManager();
                Intent i = pm.getLeanbackLaunchIntentForPackage(pkg);
                if (i == null) i = pm.getLaunchIntentForPackage(pkg);
                if (i == null) throw new IllegalStateException("Uygulama bulunamadı");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(i);
            } catch (Exception e) {
                Toast.makeText(this, "Uygulama bulunamadı: " + pkg, Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    public static final class CanliTv extends RedirectActivity {
        @Override protected String targetPackage() { return "app.opentv"; }
    }

    public static final class FilmDizi extends RedirectActivity {
        @Override protected String targetPackage() { return "com.bp.box"; }
    }

    public static final class Spor extends RedirectActivity {
        @Override protected String targetPackage() { return "com.bp.box"; }
    }

    public static final class Youtube extends RedirectActivity {
        @Override protected String targetPackage() { return "org.smarttube.stable"; }
    }

    public static final class Medya extends RedirectActivity {
        @Override protected String targetPackage() { return "org.videolan.vlc"; }
    }
}
