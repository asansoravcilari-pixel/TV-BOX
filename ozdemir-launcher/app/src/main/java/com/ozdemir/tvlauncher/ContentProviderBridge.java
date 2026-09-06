package com.ozdemir.tvlauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

/**
 * Thin bridge for an optional, locally installed content provider.
 * The provider identity stays out of OS-facing UI; this class only launches
 * the provider's exported TV/launcher activity. No stream extraction,
 * scraping, DRM bypass or internal/non-exported activity access is performed.
 */
public final class ContentProviderBridge {
    private static final String PROVIDER_PACKAGE = "com.bp.box";

    private ContentProviderBridge() {}

    private static Intent resolveLaunchIntent(Activity activity) {
        try {
            PackageManager pm = activity.getPackageManager();

            // Prefer the Android TV / Leanback entry point when the provider exposes one.
            Intent launch = pm.getLeanbackLaunchIntentForPackage(PROVIDER_PACKAGE);
            if (launch == null) {
                launch = pm.getLaunchIntentForPackage(PROVIDER_PACKAGE);
            }

            if (launch != null) {
                launch.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
            return launch;
        } catch (Exception ignored) {
            return null;
        }
    }

    public static boolean isInstalled(Activity activity) {
        return resolveLaunchIntent(activity) != null;
    }

    public static boolean launch(Activity activity) {
        try {
            Intent launch = resolveLaunchIntent(activity);
            if (launch == null) return false;
            activity.startActivity(launch);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void launchOrExplain(Activity activity) {
        if (!launch(activity)) {
            Toast.makeText(activity, "İçerik Merkezi bu cihazda kurulu değil.", Toast.LENGTH_SHORT).show();
        }
    }
}
