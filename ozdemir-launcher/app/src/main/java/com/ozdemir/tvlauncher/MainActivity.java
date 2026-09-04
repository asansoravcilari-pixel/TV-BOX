package com.ozdemir.tvlauncher;

import android.app.Activity;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.*;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
 private final Handler handler=new Handler(); private TextView clock,date,wifi; private LinearLayout root;
 @Override public void onCreate(Bundle b){super.onCreate(b);requestWindowFeature(Window.FEATURE_NO_TITLE);getWindow().setFlags(1024,1024);hide();setContentView(build());tick();}
 @Override public void onWindowFocusChanged(boolean f){super.onWindowFocusChanged(f);if(f)hide();}
 private void hide(){getWindow().getDecorView().setSystemUiVisibility(5894|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
 private View build(){
  root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(38),dp(22),dp(38),dp(24));root.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xFF080410,0xFF21102E,0xFF08040D}));
  LinearLayout top=row(); View space=new View(this);top.addView(space,new LinearLayout.LayoutParams(0,1,1));
  wifi=topChip("⌁");top.addView(wifi,chip());top.addView(topChip("ᛒ"),chip());TextView cast=topChip("▣");cast.setOnClickListener(v->openPanel("settings"));top.addView(cast,chip());
  LinearLayout tm=new LinearLayout(this);tm.setOrientation(LinearLayout.VERTICAL);tm.setGravity(Gravity.RIGHT);clock=txt("--:--",30,true);clock.setGravity(Gravity.RIGHT);date=txt("",11,false);date.setTextColor(0xFFBCAFC5);date.setGravity(Gravity.RIGHT);tm.addView(clock);tm.addView(date);LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(dp(155),dp(68));tp.setMargins(dp(16),0,0,0);top.addView(tm,tp);root.addView(top,new LinearLayout.LayoutParams(-1,dp(72)));

  LinearLayout hero=row();
  hero.addView(card("▣","Canlı TV",this::live,0),weight(7));hero.addView(card("▶","Dizi & Film",this::films,1),weight(7));hero.addView(card("▧","Medya",this::files,2),weight(7));hero.addView(card("☺","Çocuklar",this::kids,3),weight(7));root.addView(hero,new LinearLayout.LayoutParams(-1,0,2.15f));

  LinearLayout apps=row();
  apps.addView(appCard("▶","SmartTube",()->launch(new String[]{"com.liskovsoft.smarttubetv.beta","org.smarttuber.stable"})),weight(6));
  apps.addView(appCard("●","YouTube",()->launch(new String[]{"com.google.android.youtube.tv","com.google.android.youtube"})),weight(6));
  apps.addView(appCard("★","TRT Çocuk",this::kids),weight(6));
  apps.addView(appCard("◆","tabii",this::films),weight(6));
  apps.addView(appCard("+","Uygulamalar",this::apps),weight(6));root.addView(apps,new LinearLayout.LayoutParams(-1,0,1.08f));

  LinearLayout sys=row();
  sys.addView(sys("⌁","Wi‑Fi",()->panel("network")),weight(5));sys.addView(sys("ᛒ","Bluetooth",()->openAndroid(Settings.ACTION_BLUETOOTH_SETTINGS)),weight(5));sys.addView(sys("▣","Yansıt",()->openAndroid("android.settings.CAST_SETTINGS")),weight(5));sys.addView(sys("USB","USB",this::files),weight(5));sys.addView(sys("▤","Dosya",this::files),weight(5));sys.addView(sys("▧","Galeri",this::files),weight(5));sys.addView(sys("✦","Tema",()->panel("theme")),weight(5));sys.addView(sys("◷","Sayaç",()->panel("timer")),weight(5));sys.addView(sys("⚙","Ayar",()->panel("settings")),weight(5));sys.addView(sys("⏻","Güç",()->panel("power")),weight(5));root.addView(sys,new LinearLayout.LayoutParams(-1,0,.92f));
  hero.getChildAt(0).requestFocus();return root;
 }
 private TextView topChip(String s){TextView v=txt(s,23,true);v.setGravity(Gravity.CENTER);v.setFocusable(true);v.setBackground(bg(0xA52B173A,0xA5160C20,false,16));focus(v);return v;}
 private LinearLayout.LayoutParams chip(){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(dp(58),dp(52));p.setMargins(dp(5),0,dp(5),0);return p;}
 private View card(String icon,String title,Runnable r,int style){LinearLayout b=box();b.setPadding(dp(20),dp(18),dp(20),dp(15));TextView i=txt(icon,48,true);i.setGravity(Gravity.CENTER);b.addView(i,new LinearLayout.LayoutParams(-1,0,1));TextView t=txt(title,18,true);t.setGravity(Gravity.CENTER);b.addView(t);int[][] c={{0xDD2A1050,0xCC0D1837},{0xDD421343,0xCC16102E},{0xDD172C54,0xCC151027},{0xDD452046,0xCC17102B}};b.setBackground(bg(c[style][0],c[style][1],false,24));clickFocus(b,r,c[style]);return b;}
 private View appCard(String icon,String title,Runnable r){LinearLayout b=box();TextView i=txt(icon,27,true);i.setGravity(Gravity.CENTER);b.addView(i,new LinearLayout.LayoutParams(-1,0,1));TextView t=txt(title,12,true);t.setGravity(Gravity.CENTER);b.addView(t);int[] c={0xC5251532,0xB7110A19};b.setBackground(bg(c[0],c[1],false,20));clickFocus(b,r,c);return b;}
 private View sys(String icon,String title,Runnable r){LinearLayout b=box();TextView i=txt(icon,20,true);i.setGravity(Gravity.CENTER);b.addView(i,new LinearLayout.LayoutParams(-1,0,1));TextView t=txt(title,10,true);t.setGravity(Gravity.CENTER);t.setTextColor(0xFFE4D8E9);b.addView(t);int[] c={0xB6251731,0xA5110A18};b.setBackground(bg(c[0],c[1],false,17));clickFocus(b,r,c);return b;}
 private LinearLayout box(){LinearLayout b=new LinearLayout(this);b.setOrientation(LinearLayout.VERTICAL);b.setGravity(Gravity.CENTER);b.setPadding(dp(10),dp(10),dp(10),dp(9));b.setFocusable(true);b.setClickable(true);b.setElevation(dp(3));return b;}
 private void clickFocus(View v,Runnable r,int[] c){v.setOnClickListener(x->r.run());v.setOnFocusChangeListener((x,f)->{x.animate().scaleX(f?1.065f:1).scaleY(f?1.065f:1).translationY(f?-dp(3):0).setDuration(120).start();x.setElevation(f?dp(18):dp(3));x.setBackground(f?bg(0xEE8D2DD2,0xEE40176E,true,22):bg(c[0],c[1],false,22));});}
 private void focus(View v){v.setOnFocusChangeListener((x,f)->{x.animate().scaleX(f?1.08f:1).scaleY(f?1.08f:1).setDuration(100).start();});}
 private GradientDrawable bg(int a,int b,boolean focus,int rad){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{a,b});d.setCornerRadius(dp(rad));d.setStroke(dp(focus?2:1),focus?0xFFFFE8FF:0x554F345C);return d;}
 private LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.HORIZONTAL);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
 private LinearLayout.LayoutParams weight(int m){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-1,1);p.setMargins(dp(m),dp(m),dp(m),dp(m));return p;}
 private TextView txt(String s,int size,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(Color.WHITE);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}
 private void tick(){handler.post(new Runnable(){public void run(){Date n=new Date();clock.setText(new SimpleDateFormat("HH:mm",new Locale("tr","TR")).format(n));date.setText(new SimpleDateFormat("d MMMM • EEEE",new Locale("tr","TR")).format(n));updateWifi();handler.postDelayed(this,15000);}});}
 private void updateWifi(){boolean ok=false;try{ConnectivityManager cm=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);Network n=cm.getActiveNetwork();NetworkCapabilities c=n==null?null:cm.getNetworkCapabilities(n);ok=c!=null&&c.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);}catch(Exception ignored){}wifi.setText(ok?"⌁":"×");}
 private void live(){startActivity(new Intent(this,LiveTvActivity.class));}private void films(){startActivity(new Intent(this,DiziFilmActivity.class));}private void kids(){startActivity(new Intent(this,KidsActivity.class));}private void files(){startActivity(new Intent(this,FilesActivity.class));}private void apps(){startActivity(new Intent(this,AppsActivity.class));}
 private void panel(String p){Intent i=new Intent(this,SettingsPanelActivity.class);i.putExtra("page",p);startActivity(i);}private void openPanel(String p){panel(p);}private void openAndroid(String a){try{startActivity(new Intent(a));}catch(Exception e){panel("settings");}}
 private void launch(String[] pkgs){for(String p:pkgs){Intent i=getPackageManager().getLaunchIntentForPackage(p);if(i!=null){startActivity(i);return;}}apps();}
 private int dp(int x){return Math.round(x*getResources().getDisplayMetrics().density);}
}
