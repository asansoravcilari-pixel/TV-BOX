package com.ozdemir.cleanhome;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private final Handler handler = new Handler();
    private TextView clock;

    private int dp(float v) { return Math.round(v * getResources().getDisplayMetrics().density); }

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        buildHome();
    }

    private GradientDrawable bg(int start, int end, float radius) {
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{start, end});
        g.setCornerRadius(dp(radius)); return g;
    }

    private void buildHome() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(42), dp(30), dp(42), dp(34));
        root.setBackground(bg(Color.rgb(8,13,21), Color.rgb(21,29,42), 0));

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        clock = new TextView(this);
        clock.setTextColor(Color.WHITE); clock.setTextSize(23); clock.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        top.addView(clock, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView settings = new TextView(this);
        settings.setText("  ⚙  AYARLAR  "); settings.setTextColor(Color.WHITE); settings.setTextSize(18);
        settings.setGravity(Gravity.CENTER); settings.setFocusable(true); settings.setClickable(true);
        settings.setPadding(dp(12), dp(9), dp(12), dp(9)); settings.setBackground(cardDrawable(false, dp(16)));
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dp(52));
        sp.setMargins(dp(24),0,0,0); top.addView(settings,sp);
        settings.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_SETTINGS))); focusEffect(settings,16);
        root.addView(top,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,dp(58)));

        root.addView(new View(this),new LinearLayout.LayoutParams(1,0,1f));
        TextView hint = new TextView(this);
        hint.setText("Ne izlemek istiyorsun?"); hint.setTextColor(Color.rgb(210,218,230)); hint.setTextSize(22);
        hint.setTypeface(Typeface.DEFAULT,Typeface.BOLD); hint.setPadding(dp(3),0,0,dp(18));
        root.addView(hint,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout row = new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL); row.setGravity(Gravity.CENTER);
        int screen=getResources().getDisplayMetrics().widthPixels, gap=dp(12);
        int available=screen-dp(84)-gap*5; int cardW=Math.max(dp(150),Math.min(dp(245),available/6));
        int cardH=Math.max(dp(130),Math.min(dp(180),Math.round(cardW*0.68f)));

        View first=addCard(row,"CANLI TV","app.opentv",cardW,cardH,0);
        addCard(row,"FİLM & DİZİ","com.bp.box",cardW,cardH,gap);
        addCard(row,"SPOR","com.bp.box",cardW,cardH,gap);
        addCard(row,"YOUTUBE","com.google.android.youtube.tv",cardW,cardH,gap);
        addCard(row,"YOUTUBE KIDS","com.google.android.youtube.tvkids",cardW,cardH,gap);
        addCard(row,"MEDYA","org.videolan.vlc",cardW,cardH,gap);
        root.addView(row,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,cardH+dp(22)));

        TextView sub=new TextView(this); sub.setText("Kumandayla seç • OK ile aç"); sub.setTextColor(Color.rgb(130,145,165));
        sub.setTextSize(15); sub.setPadding(dp(3),dp(13),0,0);
        root.addView(sub,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        root.addView(new View(this),new LinearLayout.LayoutParams(1,0,1.15f));
        setContentView(root); updateClock(); if(first!=null) first.requestFocus();
    }

    private View addCard(LinearLayout row,String label,String pkg,int w,int h,int left) {
        LinearLayout card=new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setGravity(Gravity.CENTER);
        card.setPadding(dp(10),dp(13),dp(10),dp(10)); card.setFocusable(true); card.setClickable(true); card.setBackground(cardDrawable(false,dp(22)));
        ImageView icon=new ImageView(this); icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        try { ApplicationInfo ai=getPackageManager().getApplicationInfo(pkg,0); Drawable d=getPackageManager().getApplicationIcon(ai); icon.setImageDrawable(d); } catch(Exception ignored) {}
        card.addView(icon,new LinearLayout.LayoutParams(dp(68),dp(68)));
        TextView title=new TextView(this); title.setText(label); title.setTextColor(Color.WHITE); title.setTextSize(18); title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        title.setGravity(Gravity.CENTER); title.setSingleLine(false);
        LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT); tp.setMargins(0,dp(11),0,0); card.addView(title,tp);
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(w,h); cp.setMargins(left,0,0,0); row.addView(card,cp);
        card.setOnClickListener(v -> launch(pkg)); focusEffect(card,22); return card;
    }

    private GradientDrawable cardDrawable(boolean focused,int radius) {
        GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,focused?new int[]{Color.rgb(48,64,86),Color.rgb(37,49,68)}:new int[]{Color.rgb(28,37,51),Color.rgb(20,28,40)});
        d.setCornerRadius(radius); d.setStroke(dp(focused?2:1),focused?Color.WHITE:Color.rgb(55,69,88)); return d;
    }

    private void focusEffect(View v,int radiusDp) {
        v.setOnFocusChangeListener((view,has)->{ view.animate().scaleX(has?1.065f:1f).scaleY(has?1.065f:1f).setDuration(120).start(); view.setBackground(cardDrawable(has,dp(radiusDp))); view.setElevation(dp(has?12:2)); });
    }

    private void launch(String pkg) {
        try { PackageManager pm=getPackageManager(); Intent i=pm.getLeanbackLaunchIntentForPackage(pkg); if(i==null)i=pm.getLaunchIntentForPackage(pkg); if(i==null)throw new Exception(); i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED); startActivity(i); }
        catch(Exception e){ Toast.makeText(this,"Uygulama bulunamadı",Toast.LENGTH_SHORT).show(); }
    }

    private final Runnable tick=new Runnable(){ @Override public void run(){ updateClock(); handler.postDelayed(this,30000); } };
    private void updateClock(){ if(clock!=null)clock.setText(new SimpleDateFormat("HH:mm",new Locale("tr","TR")).format(new Date())); }
    @Override protected void onResume(){ super.onResume(); handler.removeCallbacks(tick); handler.post(tick); }
    @Override protected void onPause(){ handler.removeCallbacks(tick); super.onPause(); }
}
