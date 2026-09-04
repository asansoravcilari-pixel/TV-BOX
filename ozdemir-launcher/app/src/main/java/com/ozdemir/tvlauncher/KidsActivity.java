package com.ozdemir.tvlauncher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class KidsActivity extends Activity {
 @Override protected void onCreate(Bundle b){super.onCreate(b);requestWindowFeature(Window.FEATURE_NO_TITLE);getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);hide();setContentView(build());}
 @Override public void onWindowFocusChanged(boolean f){super.onWindowFocusChanged(f);if(f)hide();}
 private void hide(){getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);}
 private View build(){LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(38),dp(24),dp(38),dp(28));root.setBackground(bg(new int[]{0xFF15091C,0xFF321345,0xFF09060E},24,0));LinearLayout head=new LinearLayout(this);head.setGravity(Gravity.CENTER_VERTICAL);TextView back=button("‹  Geri");back.setOnClickListener(v->finish());head.addView(back,new LinearLayout.LayoutParams(dp(145),dp(54)));LinearLayout title=new LinearLayout(this);title.setOrientation(LinearLayout.VERTICAL);title.setPadding(dp(22),0,0,0);TextView h=text("Çocuklar",29,true);TextView sub=text("2–5 yaş için sade ve güvenli içerik alanı",12,false);sub.setTextColor(0xFFCAB4D5);title.addView(h);title.addView(sub);head.addView(title,new LinearLayout.LayoutParams(0,dp(62),1));root.addView(head);GridLayout grid=new GridLayout(this);grid.setColumnCount(3);grid.setRowCount(2);grid.setPadding(0,dp(18),0,0);add(grid,"▶","YouTube Kids","Çocuklara özel video",()->openAppOrWeb(new String[]{"com.google.android.apps.youtube.kids","com.google.android.youtube.tvkids"},"https://www.youtubekids.com/"),0);add(grid,"★","TRT Çocuk","TRT'nin çocuk içerikleri",()->openAppOrWeb(new String[]{"com.trtcocuk.mobile","com.trtcocuk"},"https://www.trtcocuk.net.tr/"),1);add(grid,"●","Bluey","Resmî Bluey içerikleri",()->openWeb("https://www.bluey.tv/watch/"),2);add(grid,"♥","Maşa ile Koca Ayı","Resmî içerik merkezi",()->openWeb("https://www.mashabear.com/"),3);add(grid,"✦","Eğitici & Eğlenceli","Boyama • şarkı • öğrenme",()->openWeb("https://www.trtcocuk.net.tr/"),4);add(grid,"♫","TRT Diyanet Çocuk","Çocuk canlı yayın ve içerik",()->openWeb("https://www.trtdiyanetcocuk.net.tr/"),5);root.addView(grid,new LinearLayout.LayoutParams(-1,0,1));return root;}
 private void add(GridLayout g,String icon,String title,String sub,Runnable r,int style){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setGravity(Gravity.CENTER);c.setPadding(dp(18),dp(14),dp(18),dp(14));c.setFocusable(true);c.setClickable(true);c.setBackground(card(style,false));TextView i=text(icon,31,true);i.setGravity(Gravity.CENTER);TextView t=text(title,18,true);t.setGravity(Gravity.CENTER);t.setPadding(0,dp(8),0,0);TextView s=text(sub,11,false);s.setGravity(Gravity.CENTER);s.setTextColor(0xFFC7B3D2);s.setPadding(0,dp(5),0,0);c.addView(i);c.addView(t);c.addView(s);c.setOnClickListener(v->r.run());c.setOnFocusChangeListener((v,f)->{v.setBackground(card(style,f));v.animate().scaleX(f?1.04f:1f).scaleY(f?1.04f:1f).setDuration(110).start();});GridLayout.LayoutParams p=new GridLayout.LayoutParams();p.width=0;p.height=0;p.columnSpec=GridLayout.spec(GridLayout.UNDEFINED,1f);p.rowSpec=GridLayout.spec(GridLayout.UNDEFINED,1f);p.setMargins(dp(8),dp(8),dp(8),dp(8));g.addView(c,p);}
 private void openAppOrWeb(String[] pkgs,String url){for(String p:pkgs){try{Intent i=getPackageManager().getLaunchIntentForPackage(p);if(i!=null){startActivity(i);return;}}catch(Exception ignored){}}openWeb(url);}
 private void openWeb(String u){try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(u)));}catch(Exception e){Toast.makeText(this,"Bağlantıyı açacak uygulama bulunamadı.",Toast.LENGTH_SHORT).show();}}
 private TextView button(String s){TextView t=text(s,14,true);t.setGravity(Gravity.CENTER);t.setFocusable(true);t.setClickable(true);t.setBackground(card(0,false));t.setOnFocusChangeListener((v,f)->v.setBackground(card(0,f)));return t;}
 private GradientDrawable card(int s,boolean f){int[][] n={{0xD93D1B5B,0xD926123B},{0xD92D255F,0xD91B173C},{0xD91B3562,0xD9112342},{0xD95A2149,0xD9361530},{0xD947275C,0xD9251739},{0xD92E2346,0xD91B152B}};int[][] x={{0xFFB33A89,0xFF6A1D70},{0xFF5E54C8,0xFF352D88},{0xFF3386C8,0xFF21518C},{0xFFC9467C,0xFF7B2854},{0xFF8B55C8,0xFF553182},{0xFF7058A6,0xFF403163}};GradientDrawable d=bg(f?x[s]:n[s],18,f?0xFFFFFFFF:0x557D5B86);return d;}
 private GradientDrawable bg(int[] c,int r,int stroke){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,c);d.setCornerRadius(dp(r));if(stroke!=0)d.setStroke(dp(1),stroke);return d;}
 private TextView text(String s,int sp,boolean b){TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(sp);if(b)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
