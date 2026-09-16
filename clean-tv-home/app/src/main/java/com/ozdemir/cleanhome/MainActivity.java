package com.ozdemir.cleanhome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
    private TextView networkStatus;
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
        g.setCornerRadius(dp(radius));
        return g;
    }

    private void buildHome() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(34), dp(20), dp(34), dp(24));
        root.setBackground(bg(Color.rgb(7,11,18), Color.rgb(19,27,39), 0));

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        networkStatus = new TextView(this);
        networkStatus.setTextSize(16);
        networkStatus.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        networkStatus.setGravity(Gravity.CENTER);
        networkStatus.setFocusable(true);
        networkStatus.setClickable(true);
        networkStatus.setPadding(dp(14),dp(8),dp(14),dp(8));
        networkStatus.setBackground(cardDrawable(false,dp(15)));
        networkStatus.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));
        focusEffect(networkStatus,15);
        top.addView(networkStatus,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,dp(50)));
        top.addView(new View(this),new LinearLayout.LayoutParams(0,1,1f));

        clock = new TextView(this);
        clock.setTextColor(Color.WHITE);
        clock.setTextSize(24);
        clock.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        top.addView(clock,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView settings = new TextView(this);
        settings.setText("  ⚙  AYARLAR  ");
        settings.setTextColor(Color.WHITE);
        settings.setTextSize(18);
        settings.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        settings.setGravity(Gravity.CENTER);
        settings.setFocusable(true);
        settings.setClickable(true);
        settings.setPadding(dp(13),dp(8),dp(13),dp(8));
        settings.setBackground(cardDrawable(false,dp(15)));
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,dp(50));
        sp.setMargins(dp(22),0,0,0);
        top.addView(settings,sp);
        settings.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_SETTINGS)));
        focusEffect(settings,15);
        root.addView(top,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,dp(54)));

        root.addView(new View(this),new LinearLayout.LayoutParams(1,0,0.48f));
        TextView hint = new TextView(this);
        hint.setText("Ne izlemek istiyorsun?");
        hint.setTextColor(Color.rgb(224,230,239));
        hint.setTextSize(24);
        hint.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        hint.setPadding(dp(2),0,0,dp(14));
        root.addView(hint,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        int screen=getResources().getDisplayMetrics().widthPixels;
        int gap=dp(11);
        int available=screen-dp(68)-gap*5;
        int cardW=Math.max(dp(145),Math.min(dp(245),available/6));
        int cardH=Math.max(dp(145),Math.min(dp(194),Math.round(cardW*0.72f)));

        View first=addCard(row,"CANLI TV","app.opentv",cardW,cardH,0);
        addCard(row,"İNAT BOX","com.bp.box",cardW,cardH,gap);
        addCard(row,"YOUTUBE","com.google.android.youtube.tv",cardW,cardH,gap);
        addCard(row,"YOUTUBE KIDS","com.google.android.youtube.tvkids",cardW,cardH,gap);
        addCard(row,"TARAYICI","com.phlox.tvwebbrowser",cardW,cardH,gap);
        addCard(row,"MEDYA","org.videolan.vlc",cardW,cardH,gap);
        root.addView(row,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,cardH+dp(28)));

        TextView sub=new TextView(this);
        sub.setText("Kumandayla seç  •  OK ile aç   •   Wi-Fi kutusuna OK = ağ ayarları");
        sub.setTextColor(Color.rgb(148,162,181));
        sub.setTextSize(15);
        sub.setPadding(dp(2),dp(10),0,0);
        root.addView(sub,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        root.addView(new View(this),new LinearLayout.LayoutParams(1,0,0.72f));
        setContentView(root);
        updateStatus();
        if(first!=null) first.requestFocus();
    }

    private View addCard(LinearLayout row,String label,String pkg,int w,int h,int left) {
        LinearLayout card=new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(dp(9),dp(13),dp(9),dp(10));
        card.setFocusable(true);
        card.setClickable(true);
        card.setBackground(cardDrawable(false,dp(21)));
        ImageView icon=new ImageView(this);
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        try {
            ApplicationInfo ai=getPackageManager().getApplicationInfo(pkg,0);
            Drawable d=getPackageManager().getApplicationIcon(ai);
            icon.setImageDrawable(d);
        } catch(Exception ignored) {}
        card.addView(icon,new LinearLayout.LayoutParams(dp(72),dp(72)));
        TextView title=new TextView(this);
        title.setText(label);
        title.setTextColor(Color.WHITE);
        title.setTextSize(18);
        title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setSingleLine(false);
        title.setMaxLines(2);
        LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        tp.setMargins(0,dp(11),0,0);
        card.addView(title,tp);
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(w,h);
        cp.setMargins(left,0,0,0);
        row.addView(card,cp);
        card.setOnClickListener(v -> launch(pkg));
        focusEffect(card,21);
        return card;
    }

    private GradientDrawable cardDrawable(boolean focused,int radius) {
        GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,focused?new int[]{Color.rgb(55,73,98),Color.rgb(35,48,67)}:new int[]{Color.rgb(27,36,50),Color.rgb(18,25,36)});
        d.setCornerRadius(radius);
        d.setStroke(dp(focused?3:1),focused?Color.WHITE:Color.rgb(55,69,88));
        return d;
    }

    private void focusEffect(View v,int radiusDp) {
        v.setOnFocusChangeListener((view,has)->{
            view.animate().scaleX(has?1.055f:1f).scaleY(has?1.055f:1f).setDuration(110).start();
            view.setBackground(cardDrawable(has,dp(radiusDp)));
            view.setElevation(dp(has?14:2));
        });
    }

    private void launch(String pkg) {
        try {
            PackageManager pm=getPackageManager();
            Intent i=pm.getLeanbackLaunchIntentForPackage(pkg);
            if(i==null)i=pm.getLaunchIntentForPackage(pkg);
            if(i==null)throw new Exception();
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(i);
        } catch(Exception e) { Toast.makeText(this,"Uygulama bulunamadı",Toast.LENGTH_SHORT).show(); }
    }

    private String wifiIp() {
        try {
            WifiManager wm=(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wi=wm.getConnectionInfo();
            int ip=wi==null?0:wi.getIpAddress();
            if(ip==0)return "";
            return (ip&0xff)+"."+((ip>>8)&0xff)+"."+((ip>>16)&0xff)+"."+((ip>>24)&0xff);
        } catch(Exception e) { return ""; }
    }

    private void updateStatus() {
        if(clock!=null)clock.setText(new SimpleDateFormat("HH:mm",new Locale("tr","TR")).format(new Date()));
        if(networkStatus==null)return;
        boolean wifi=false;
        try {
            ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo n=cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            wifi=n!=null&&n.isConnected();
        } catch(Exception ignored) {}
        if(wifi) {
            String ip=wifiIp();
            networkStatus.setText(ip.length()>0?"  Wi-Fi: BAĞLI  •  "+ip+"  ":"  Wi-Fi: BAĞLI  ");
            networkStatus.setTextColor(Color.rgb(124,236,167));
        } else {
            networkStatus.setText("  Wi-Fi: BAĞLI DEĞİL  •  OK: BAĞLAN  ");
            networkStatus.setTextColor(Color.rgb(255,170,120));
        }
    }

    private final Runnable tick=new Runnable(){ @Override public void run(){ updateStatus(); handler.postDelayed(this,5000); } };
    @Override protected void onResume(){ super.onResume(); handler.removeCallbacks(tick); handler.post(tick); }
    @Override protected void onPause(){ handler.removeCallbacks(tick); super.onPause(); }
}
