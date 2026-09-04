package com.ozdemir.tvlauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppsActivity extends Activity {
 private LinearLayout root;
 @Override protected void onCreate(Bundle b){super.onCreate(b);requestWindowFeature(Window.FEATURE_NO_TITLE);getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);hide();setContentView(build());}
 @Override public void onWindowFocusChanged(boolean f){super.onWindowFocusChanged(f);if(f)hide();}
 private void hide(){getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);}
 private View build(){root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(38),dp(24),dp(38),dp(24));root.setBackground(bg(new int[]{0xFF100716,0xFF291039,0xFF08060D},24,0));
  LinearLayout head=new LinearLayout(this);head.setGravity(Gravity.CENTER_VERTICAL);TextView back=button("‹  Geri");back.setOnClickListener(v->finish());head.addView(back,new LinearLayout.LayoutParams(dp(150),dp(54)));LinearLayout title=new LinearLayout(this);title.setOrientation(LinearLayout.VERTICAL);title.setPadding(dp(24),0,0,0);TextView h=text("Uygulamalar",29,true);TextView sub=text("ÖZDEMİR TV OS • yüklü uygulamalar",12,false);sub.setTextColor(0xFFBDA8C8);title.addView(h);title.addView(sub);head.addView(title,new LinearLayout.LayoutParams(0,dp(62),1));TextView archive=button("Arşiv  →");archive.setOnClickListener(v->startActivity(new Intent(this,ArchiveActivity.class)));head.addView(archive,new LinearLayout.LayoutParams(dp(165),dp(54)));root.addView(head);
  ScrollView scroll=new ScrollView(this);GridLayout grid=new GridLayout(this);grid.setColumnCount(5);grid.setPadding(0,dp(18),0,dp(20));PackageManager pm=getPackageManager();List<ApplicationInfo> list=new ArrayList<>(pm.getInstalledApplications(0));Collections.sort(list,Comparator.comparing(a->pm.getApplicationLabel(a).toString().toLowerCase()));int count=0;View first=null;for(ApplicationInfo a:list){Intent launch=pm.getLaunchIntentForPackage(a.packageName);if(launch==null||a.packageName.equals(getPackageName()))continue;String label=pm.getApplicationLabel(a).toString();View card=appCard(label,a.packageName,launch);GridLayout.LayoutParams gp=new GridLayout.LayoutParams();gp.width=0;gp.height=dp(112);gp.columnSpec=GridLayout.spec(GridLayout.UNDEFINED,1f);gp.setMargins(dp(7),dp(7),dp(7),dp(7));grid.addView(card,gp);if(first==null)first=card;count++;}if(count==0){TextView empty=text("Açılabilir uygulama bulunamadı",18,true);empty.setPadding(dp(20),dp(30),0,0);grid.addView(empty);}scroll.addView(grid);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));if(first!=null)first.requestFocus();return root;}
 private View appCard(String name,String pkg,Intent launch){LinearLayout b=new LinearLayout(this);b.setOrientation(LinearLayout.VERTICAL);b.setGravity(Gravity.CENTER);b.setPadding(dp(10),dp(8),dp(10),dp(8));b.setFocusable(true);b.setClickable(true);b.setBackground(cardBg(false));TextView icon=text(symbol(name),24,true);icon.setGravity(Gravity.CENTER);icon.setTextColor(0xFFE9C8FF);TextView t=text(name,13,true);t.setGravity(Gravity.CENTER);t.setSingleLine(true);t.setPadding(0,dp(6),0,0);b.addView(icon);b.addView(t);b.setOnClickListener(v->{launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(launch);});b.setOnFocusChangeListener((v,f)->{v.animate().scaleX(f?1.06f:1).scaleY(f?1.06f:1).translationY(f?-dp(2):0).setDuration(110).start();v.setBackground(cardBg(f));icon.setTextColor(f?Color.WHITE:0xFFE9C8FF);v.setElevation(f?dp(14):dp(2));});return b;}
 private String symbol(String n){String s=n.toLowerCase();if(s.contains("youtube")||s.contains("tube"))return "▶";if(s.contains("tv"))return "▣";if(s.contains("file")||s.contains("dosya"))return "▤";if(s.contains("browser")||s.contains("web"))return "◎";if(s.contains("ayar")||s.contains("setting"))return "⚙";return "◆";}
 private TextView button(String s){TextView t=text(s,14,true);t.setGravity(Gravity.CENTER);t.setFocusable(true);t.setClickable(true);t.setBackground(cardBg(false));t.setOnFocusChangeListener((v,f)->{v.setBackground(cardBg(f));v.animate().scaleX(f?1.04f:1).scaleY(f?1.04f:1).setDuration(100).start();});return t;}
 private GradientDrawable cardBg(boolean f){return bg(f?new int[]{0xFF7130A5,0xFF442061}:new int[]{0xD5261734,0xC8140E1E},16,f?0xFFF0D7FF:0x665B3D6B);}
 private GradientDrawable bg(int[] c,int r,int stroke){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,c);d.setCornerRadius(dp(r));if(stroke!=0)d.setStroke(dp(1),stroke);return d;}
 private TextView text(String s,int sp,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextColor(0xFFF8F3FA);t.setTextSize(sp);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
