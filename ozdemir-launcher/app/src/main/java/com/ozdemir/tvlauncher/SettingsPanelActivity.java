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
import android.os.Environment;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;

public class SettingsPanelActivity extends Activity {
    private LinearLayout detail;
    private SharedPreferences prefs;
    private int themeIndex;
    private final int[][] themes={{0xFF120818,0xFF2C0E40,0xFF08060E},{0xFF070A1B,0xFF20113D,0xFF08070F},{0xFF090A18,0xFF36144F,0xFF0D0715},{0xFF16071E,0xFF4A1764,0xFF0A0610}};

    @Override protected void onCreate(Bundle b){super.onCreate(b);requestWindowFeature(Window.FEATURE_NO_TITLE);getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);prefs=getSharedPreferences("ozdemir_tv",MODE_PRIVATE);themeIndex=prefs.getInt("theme",0);hideSystemUi();setContentView(build());String p=getIntent().getStringExtra("page");showPage(p==null?"settings":p);}
    @Override public void onWindowFocusChanged(boolean f){super.onWindowFocusChanged(f);if(f)hideSystemUi();}
    private void hideSystemUi(){getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);}

    private View build(){LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.HORIZONTAL);root.setPadding(dp(28),dp(24),dp(28),dp(24));root.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,themes[themeIndex]));
        LinearLayout left=new LinearLayout(this);left.setOrientation(LinearLayout.VERTICAL);left.setPadding(dp(12),dp(12),dp(20),dp(12));TextView logo=text("Ö  ÖZDEMİR TV OS",22,true);logo.setTextColor(0xFFF2DBFF);left.addView(logo);TextView sub=text("Ayarlar Merkezi",12,false);sub.setTextColor(0xFFBDA5C8);sub.setPadding(0,0,0,dp(18));left.addView(sub);
        left.addView(menu("⌁  Ağ","network"));left.addView(menu("◫  Depolama","storage"));left.addView(menu("◷  Zamanlayıcı","timer"));left.addView(menu("✦  Tema","theme"));left.addView(menu("⌨  Klavye","keyboard"));left.addView(menu("◉  Cihaz Hakkında","about"));left.addView(menu("⏻  Güç","power"));root.addView(left,new LinearLayout.LayoutParams(0,-1,.40f));
        detail=new LinearLayout(this);detail.setOrientation(LinearLayout.VERTICAL);detail.setPadding(dp(30),dp(24),dp(30),dp(24));detail.setBackground(panelBg());root.addView(detail,new LinearLayout.LayoutParams(0,-1,.60f));return root;}
    private View menu(String title,String page){TextView t=text(title,15,true);t.setPadding(dp(16),dp(12),dp(16),dp(12));t.setFocusable(true);t.setClickable(true);t.setBackground(itemBg(false));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(54));p.setMargins(0,dp(5),0,dp(5));t.setLayoutParams(p);t.setOnClickListener(v->showPage(page));t.setOnFocusChangeListener((v,f)->{v.setBackground(itemBg(f));v.animate().scaleX(f?1.025f:1f).scaleY(f?1.025f:1f).setDuration(100).start();});return t;}
    private void showPage(String page){detail.removeAllViews();switch(page){case "network":network();break;case "storage":storage();break;case "timer":timer();break;case "theme":theme();break;case "keyboard":keyboard();break;case "about":about();break;case "power":power();break;default:settings();}}
    private void title(String a,String b){TextView t=text(a,25,true);detail.addView(t);TextView s=text(b,12,false);s.setTextColor(0xFFBDA9C7);s.setPadding(0,dp(4),0,dp(18));detail.addView(s);}
    private void settings(){title("ÖZDEMİR Ayarlar","Android ekranları yerine TV için sade ve tek tasarım.");detail.addView(info("Kumandayla soldan kategori seç. Ayarlar burada açılır."));}
    private void network(){title("Ağ","Bağlantı durumu ve ağ işlemleri");boolean online=false;try{ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);Network n=cm.getActiveNetwork();NetworkCapabilities c=n==null?null:cm.getNetworkCapabilities(n);online=c!=null&&c.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);}catch(Exception ignored){}detail.addView(info(online?"● İnternet bağlantısı aktif":"○ İnternet bağlantısı yok"));detail.addView(action("Wi‑Fi ağlarını yönet","Gelişmiş ağ yönetimini aç",()->openSystem(Settings.ACTION_WIFI_SETTINGS)));}
    private void storage(){title("Depolama","Cihazın kullanılabilir alanı");File d=Environment.getDataDirectory();long total=d.getTotalSpace(),free=d.getFreeSpace();detail.addView(info("Toplam: "+gb(total)+" GB\nBoş: "+gb(free)+" GB\nKullanılan: "+gb(total-free)+" GB"));}
    private void timer(){title("Zamanlayıcı","TV'nin ne zaman kapanacağını seç");detail.addView(action("30 dakika","Uyku zamanlayıcısını ayarla",()->saveTimer(30)));detail.addView(action("60 dakika","Uyku zamanlayıcısını ayarla",()->saveTimer(60)));detail.addView(action("90 dakika","Uyku zamanlayıcısını ayarla",()->saveTimer(90)));detail.addView(action("Kapalı","Zamanlayıcıyı iptal et",()->saveTimer(0)));}
    private void saveTimer(int m){prefs.edit().putInt("sleep_minutes",m).putLong("sleep_set_at",System.currentTimeMillis()).apply();Toast.makeText(this,m==0?"Zamanlayıcı kapatıldı":m+" dakika ayarlandı",Toast.LENGTH_SHORT).show();}
    private void theme(){title("Tema","ÖZDEMİR TV OS görünümünü değiştir");for(int i=0;i<4;i++){final int x=i;detail.addView(action("Tema "+(i+1),i==themeIndex?"Şu anda kullanılıyor":"Mor premium görünüm",()->{themeIndex=x;prefs.edit().putInt("theme",x).apply();recreate();}));}}
    private void keyboard(){title("Klavye","ÖZDEMİR TV klavyesi");detail.addView(info("Koyu mor TV klavyesi uygulamaya eklendi. Kumandayla kullanılmak üzere tasarlandı."));detail.addView(action("Klavye seçimi","ÖZDEMİR TV Klavyesi'ni etkinleştir",()->openSystem(Settings.ACTION_INPUT_METHOD_SETTINGS)));}
    private void about(){title("Cihaz Hakkında","ÖZDEMİR TV OS");detail.addView(info("Android 10 tabanlı TV sistemi\nÖZDEMİR Launcher\nTV kumandası için optimize edildi\nTek arayüz • koyu mor tema"));}
    private void power(){title("Güç","Cihaz güç seçenekleri");detail.addView(action("Ana ekrana dön","ÖZDEMİR TV OS",this::finish));detail.addView(action("Yeniden başlat","Sistem yetkisi gereken işlem",()->Toast.makeText(this,"Yeniden başlatma yetkisi ADB kurulumu sonrası bağlanacak.",Toast.LENGTH_SHORT).show()));}
    private View action(String a,String b,Runnable r){LinearLayout x=new LinearLayout(this);x.setOrientation(LinearLayout.VERTICAL);x.setPadding(dp(18),dp(11),dp(18),dp(11));x.setFocusable(true);x.setClickable(true);x.setBackground(itemBg(false));TextView t=text(a,15,true);TextView s=text(b,11,false);s.setTextColor(0xFFB8A4C2);s.setPadding(0,dp(3),0,0);x.addView(t);x.addView(s);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(66));p.setMargins(0,dp(5),0,dp(5));x.setLayoutParams(p);x.setOnClickListener(v->r.run());x.setOnFocusChangeListener((v,f)->{v.setBackground(itemBg(f));v.animate().scaleX(f?1.02f:1f).scaleY(f?1.02f:1f).setDuration(100).start();});return x;}
    private View info(String s){TextView t=text(s,15,false);t.setPadding(dp(18),dp(18),dp(18),dp(18));t.setBackground(itemBg(false));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,dp(5),0,dp(10));t.setLayoutParams(p);return t;}
    private void openSystem(String a){try{startActivity(new Intent(a));}catch(Exception e){Toast.makeText(this,"Bu işlem cihaz tarafından desteklenmiyor.",Toast.LENGTH_SHORT).show();}}
    private GradientDrawable panelBg(){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xEE25132F,0xEE120A19});d.setCornerRadius(dp(24));d.setStroke(dp(1),0x88694B77);return d;}
    private GradientDrawable itemBg(boolean f){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,f?new int[]{0xF06F2BA0,0xF03D175D}:new int[]{0xA824172D,0x94140E1B});d.setCornerRadius(dp(15));d.setStroke(dp(f?2:1),f?0xFFF2D9FF:0x554F3A5A);return d;}
    private TextView text(String s,int sp,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(sp);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);t.setGravity(Gravity.CENTER_VERTICAL);return t;}
    private String gb(long b){return String.format(java.util.Locale.US,"%.1f",b/1073741824d);} private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
