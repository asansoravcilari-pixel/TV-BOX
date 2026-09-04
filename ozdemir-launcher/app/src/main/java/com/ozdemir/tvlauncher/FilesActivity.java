package com.ozdemir.tvlauncher;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class FilesActivity extends Activity {
 private static final int REQ=41; private LinearLayout list; private File current;
 @Override protected void onCreate(Bundle b){super.onCreate(b);requestWindowFeature(Window.FEATURE_NO_TITLE);getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);hide();current=Environment.getExternalStorageDirectory();setContentView(build());if(android.os.Build.VERSION.SDK_INT>=23&&checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQ);else load(current);}
 @Override public void onWindowFocusChanged(boolean f){super.onWindowFocusChanged(f);if(f)hide();}
 @Override public void onBackPressed(){File root=Environment.getExternalStorageDirectory();if(current!=null&&current.getParentFile()!=null&&!current.equals(root)){load(current.getParentFile());}else super.onBackPressed();}
 @Override public void onRequestPermissionsResult(int r,String[] p,int[] g){super.onRequestPermissionsResult(r,p,g);load(current);}
 private void hide(){getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);}
 private View build(){LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(38),dp(24),dp(38),dp(24));root.setBackground(bg(new int[]{0xFF100716,0xFF291039,0xFF08060D},24,0));LinearLayout head=new LinearLayout(this);head.setGravity(Gravity.CENTER_VERTICAL);TextView back=button("‹  Geri");back.setOnClickListener(v->onBackPressed());head.addView(back,new LinearLayout.LayoutParams(dp(150),dp(54)));LinearLayout title=new LinearLayout(this);title.setOrientation(LinearLayout.VERTICAL);title.setPadding(dp(24),0,0,0);TextView h=text("Dosyalar",29,true);TextView sub=text("USB • video • fotoğraf • belgeler",12,false);sub.setTextColor(0xFFBDA8C8);title.addView(h);title.addView(sub);head.addView(title,new LinearLayout.LayoutParams(0,dp(62),1));root.addView(head);ScrollView sc=new ScrollView(this);list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);list.setPadding(0,dp(16),0,dp(20));sc.addView(list);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));return root;}
 private void load(File dir){current=dir;list.removeAllViews();File[] files=null;try{files=dir.listFiles();}catch(Exception ignored){}if(files==null){TextView t=text("Bu klasöre erişilemiyor",17,true);t.setPadding(dp(16),dp(30),0,0);list.addView(t);return;}Arrays.sort(files,Comparator.comparing((File f)->!f.isDirectory()).thenComparing(f->f.getName().toLowerCase()));View first=null;for(File f:files){if(f.isHidden())continue;View row=fileRow(f);list.addView(row,new LinearLayout.LayoutParams(-1,dp(64)));((LinearLayout.LayoutParams)row.getLayoutParams()).setMargins(0,dp(4),0,dp(4));if(first==null)first=row;}if(first!=null)first.requestFocus();}
 private View fileRow(File f){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.setPadding(dp(18),0,dp(18),0);r.setFocusable(true);r.setClickable(true);r.setBackground(cardBg(false));TextView i=text(f.isDirectory()?"▣":icon(f),18,true);i.setGravity(Gravity.CENTER);i.setTextColor(0xFFE5C9F5);r.addView(i,new LinearLayout.LayoutParams(dp(48),-1));TextView n=text(f.getName(),14,true);n.setSingleLine(true);r.addView(n,new LinearLayout.LayoutParams(0,-1,1));TextView meta=text(f.isDirectory()?"Klasör":size(f.length()),11,false);meta.setTextColor(0xFFB9A7C3);meta.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);r.addView(meta,new LinearLayout.LayoutParams(dp(170),-1));r.setOnClickListener(v->{if(f.isDirectory())load(f);else open(f);});r.setOnFocusChangeListener((v,focus)->{v.setBackground(cardBg(focus));v.animate().scaleX(focus?1.015f:1).scaleY(focus?1.015f:1).setDuration(100).start();v.setElevation(focus?dp(10):dp(1));i.setTextColor(focus?Color.WHITE:0xFFE5C9F5);});return r;}
 private String icon(File f){String n=f.getName().toLowerCase();if(n.endsWith(".mp4")||n.endsWith(".mkv")||n.endsWith(".avi"))return "▶";if(n.endsWith(".jpg")||n.endsWith(".jpeg")||n.endsWith(".png")||n.endsWith(".webp"))return "◫";if(n.endsWith(".mp3")||n.endsWith(".wav"))return "♪";return "◆";}
 private void open(File f){try{Intent i=new Intent(Intent.ACTION_VIEW);i.setDataAndType(Uri.fromFile(f),"*/*");i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(i);}catch(Exception e){android.widget.Toast.makeText(this,"Bu dosyayı açacak uygulama bulunamadı.",android.widget.Toast.LENGTH_SHORT).show();}}
 private String size(long n){if(n<1024)return n+" B";if(n<1024*1024)return (n/1024)+" KB";if(n<1024L*1024*1024)return (n/(1024*1024))+" MB";return String.format(java.util.Locale.US,"%.1f GB",n/(1024d*1024d*1024d));}
 private TextView button(String s){TextView t=text(s,14,true);t.setGravity(Gravity.CENTER);t.setFocusable(true);t.setClickable(true);t.setBackground(cardBg(false));t.setOnFocusChangeListener((v,f)->v.setBackground(cardBg(f)));return t;}
 private GradientDrawable cardBg(boolean f){return bg(f?new int[]{0xFF7130A5,0xFF442061}:new int[]{0xD5261734,0xC8140E1E},15,f?0xFFF0D7FF:0x665B3D6B);}
 private GradientDrawable bg(int[] c,int r,int stroke){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,c);d.setCornerRadius(dp(r));if(stroke!=0)d.setStroke(dp(1),stroke);return d;}
 private TextView text(String s,int sp,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextColor(0xFFF8F3FA);t.setTextSize(sp);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
