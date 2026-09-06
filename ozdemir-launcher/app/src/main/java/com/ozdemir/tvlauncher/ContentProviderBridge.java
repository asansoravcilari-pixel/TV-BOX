package com.ozdemir.tvlauncher;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

/**
 * Thin bridge for an optional, locally installed content provider.
 * The provider identity stays out of OS-facing UI; this class only launches
 * the provider's exported launcher activity. No stream extraction, scraping,
 * DRM bypass or internal/non-exported activity access is performed here.
 */
public final class ContentProviderBridge {
    private static final String PROVIDER_PACKAGE = "com.bp.box";

    private ContentProviderBridge() {}

    public static boolean isInstalled(Activity activity) {
        try {
            return activity.getPackageManager().getLaunchIntentForPackage(PROVIDER_PACKAGE) != null;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean launch(Activity activity) {
        try {
            Intent launch = activity.getPackageManager().getLaunchIntentForPackage(PROVIDER_PACKAGE);
            if (launch == null) return false;
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
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
