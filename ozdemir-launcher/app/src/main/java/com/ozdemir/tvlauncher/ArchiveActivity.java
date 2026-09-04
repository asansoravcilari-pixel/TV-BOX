package com.ozdemir.tvlauncher;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ArchiveActivity extends Activity {
    private static final String MANIFEST_URL="https://raw.githubusercontent.com/asansoravcilari-pixel/TV-BOX/main/ozdemir-launcher/app/src/main/assets/apps.json";
    private final List<AppItem> items=new ArrayList<>();
    private LinearLayout list;
    private TextView status;

    static class AppItem {
        String name,pkg,versionName,category,apkUrl,sha256,localPath;
        long versionCode;
        boolean local;
    }

    @Override protected void onCreate(Bundle b){super.onCreate(b);requestWindowFeature(Window.FEATURE_NO_TITLE);getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);hide();requestStorageIfNeeded();loadBundled();setContentView(build());refreshRemote();scanUsb();}
    @Override public void onWindowFocusChanged(boolean f){super.onWindowFocusChanged(f);if(f)hide();}
    private void hide(){getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);}

    private View build(){LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(38),dp(24),dp(38),dp(24));root.setBackground(bg(new int[]{0xFF100716,0xFF291039,0xFF08060D},24,0));
        LinearLayout head=new LinearLayout(this);head.setGravity(Gravity.CENTER_VERTICAL);TextView back=button("‹  Geri");back.setOnClickListener(v->finish());head.addView(back,new LinearLayout.LayoutParams(dp(145),dp(54)));LinearLayout title=new LinearLayout(this);title.setOrientation(LinearLayout.VERTICAL);title.setPadding(dp(24),0,0,0);TextView h=text("Uygulama Arşivi",29,true);TextView sub=text("İnternet • USB • modem/NAS kaynağı",12,false);sub.setTextColor(0xFFBDA8C8);title.addView(h);title.addView(sub);head.addView(title,new LinearLayout.LayoutParams(0,dp(62),1));status=text("Arşiv kontrol ediliyor…",12,true);status.setTextColor(0xFFDCC4EA);status.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);head.addView(status,new LinearLayout.LayoutParams(dp(310),dp(54)));root.addView(head);
        ScrollView scroll=new ScrollView(this);list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);list.setPadding(0,dp(18),0,dp(24));scroll.addView(list);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));rebuild();return root;}

    private synchronized void rebuild(){if(list==null)return;runOnUiThread(()->{list.removeAllViews();if(items.isEmpty()){TextView e=text("Arşivde uygulama bulunamadı.",18,true);e.setPadding(dp(18),dp(28),0,0);list.addView(e);return;}Collections.sort(items,Comparator.comparing(a->a.name.toLowerCase()));View first=null;for(AppItem a:items){View row=appRow(a);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(82));p.setMargins(0,dp(5),0,dp(5));list.addView(row,p);if(first==null)first=row;}if(first!=null)first.requestFocus();});}
    private View appRow(AppItem a){LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(18),dp(10),dp(18),dp(10));row.setFocusable(true);row.setClickable(true);row.setBackground(cardBg(false));TextView icon=text(a.local?"USB":"↓",25,true);icon.setGravity(Gravity.CENTER);icon.setTextColor(0xFFEACDFF);row.addView(icon,new LinearLayout.LayoutParams(dp(70),-1));LinearLayout info=new LinearLayout(this);info.setOrientation(LinearLayout.VERTICAL);TextView n=text(a.name,17,true);TextView d=text(detail(a),11,false);d.setTextColor(0xFFBDA8C8);info.addView(n);info.addView(d);row.addView(info,new LinearLayout.LayoutParams(0,-1,1));TextView act=text(actionText(a),13,true);act.setGravity(Gravity.CENTER);act.setPadding(dp(14),0,dp(14),0);act.setBackground(pill());row.addView(act,new LinearLayout.LayoutParams(dp(145),dp(44)));row.setOnClickListener(v->handle(a));row.setOnFocusChangeListener((v,f)->{v.setBackground(cardBg(f));v.animate().scaleX(f?1.015f:1).scaleY(f?1.03f:1).setDuration(100).start();});return row;}
    private String detail(AppItem a){String source=a.local?"USB / Yerel":"ÖZDEMİR Arşivi";String ver=a.versionName==null||a.versionName.isEmpty()?"":" • v"+a.versionName;return source+ver+" • "+(a.category==null?"Diğer":a.category);}
    private String actionText(AppItem a){if(a.pkg==null||a.pkg.isEmpty())return "YÜKLE";try{PackageInfo p=getPackageManager().getPackageInfo(a.pkg,0);long installed=Build.VERSION.SDK_INT>=28?p.getLongVersionCode():p.versionCode;if(a.versionCode>installed)return "GÜNCELLE";return "AÇ";}catch(Exception e){return "YÜKLE";}}

    private void handle(AppItem a){String action=actionText(a);if("AÇ".equals(action)&&a.pkg!=null){Intent i=getPackageManager().getLaunchIntentForPackage(a.pkg);if(i!=null){startActivity(i);return;}}if(a.local&&a.localPath!=null){installFile(new File(a.localPath));return;}if(a.apkUrl==null||a.apkUrl.trim().isEmpty()){Toast.makeText(this,"Bu uygulamanın APK adresi henüz eklenmedi.",Toast.LENGTH_SHORT).show();return;}downloadAndInstall(a);}
    private void downloadAndInstall(AppItem a){status.setText(a.name+" indiriliyor…");new Thread(()->{HttpURLConnection h=null;try{File dir=new File(getCacheDir(),"archive");dir.mkdirs();File out=new File(dir,safe(a.pkg==null?a.name:a.pkg)+".apk");h=(HttpURLConnection)new URL(a.apkUrl).openConnection();h.setConnectTimeout(7000);h.setReadTimeout(20000);h.setInstanceFollowRedirects(true);InputStream in=new BufferedInputStream(h.getInputStream());FileOutputStream fo=new FileOutputStream(out);byte[] buf=new byte[16384];int n;while((n=in.read(buf))>0)fo.write(buf,0,n);fo.close();in.close();if(a.sha256!=null&&!a.sha256.trim().isEmpty()&&!sha256(out).equalsIgnoreCase(a.sha256.trim()))throw new Exception("SHA256 uyuşmuyor");runOnUiThread(()->{status.setText("İndirme tamamlandı");installFile(out);});}catch(Exception e){runOnUiThread(()->{status.setText("İndirme başarısız");Toast.makeText(this,"İndirilemedi: "+e.getMessage(),Toast.LENGTH_LONG).show();});}finally{if(h!=null)h.disconnect();}}).start();}
    private void installFile(File f){if(!f.exists())return;if(Build.VERSION.SDK_INT>=26&&!getPackageManager().canRequestPackageInstalls()){try{startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,Uri.parse("package:"+getPackageName())));}catch(Exception ignored){}Toast.makeText(this,"Bir kez 'Bu kaynaktan izin ver' seçeneğini aç.",Toast.LENGTH_LONG).show();return;}try{Uri u=FileProvider.getUriForFile(this,getPackageName()+".files",f);Intent i=new Intent(Intent.ACTION_VIEW);i.setDataAndType(u,"application/vnd.android.package-archive");i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(i);}catch(Exception e){Toast.makeText(this,"Kurulum açılamadı: "+e.getMessage(),Toast.LENGTH_LONG).show();}}

    private void loadBundled(){try{parse(readAll(getAssets().open("apps.json")),false);}catch(Exception ignored){}}
    private void refreshRemote(){new Thread(()->{HttpURLConnection h=null;try{h=(HttpURLConnection)new URL(MANIFEST_URL).openConnection();h.setConnectTimeout(4000);h.setReadTimeout(6000);h.setUseCaches(false);if(h.getResponseCode()==200){parse(readAll(h.getInputStream()),false);runOnUiThread(()->status.setText("Arşiv güncel • "+items.size()+" uygulama"));rebuild();}}catch(Exception e){runOnUiThread(()->status.setText("Yerel arşiv • "+items.size()+" uygulama"));}finally{if(h!=null)h.disconnect();}}).start();}
    private synchronized void parse(String json,boolean local)throws Exception{JSONArray a=new JSONObject(json).getJSONArray("apps");for(int i=0;i<a.length();i++){JSONObject x=a.getJSONObject(i);AppItem it=new AppItem();it.name=x.optString("name","");it.pkg=x.optString("package","");it.versionName=x.optString("versionName","");it.versionCode=x.optLong("versionCode",0);it.category=x.optString("category","Diğer");it.apkUrl=x.optString("apkUrl","");it.sha256=x.optString("sha256","");if(it.name.isEmpty())continue;replaceOrAdd(it);}}
    private synchronized void replaceOrAdd(AppItem n){for(int i=0;i<items.size();i++){AppItem o=items.get(i);if(n.pkg!=null&&!n.pkg.isEmpty()&&n.pkg.equals(o.pkg)&&!o.local){items.set(i,n);return;}}items.add(n);}

    private void scanUsb(){new Thread(()->{try{File storage=new File("/storage");File[] vols=storage.listFiles();if(vols!=null)for(File v:vols){String p=v.getAbsolutePath();if(p.contains("emulated")||p.endsWith("/self"))continue;scanDir(v,0);}rebuild();runOnUiThread(()->status.setText("Arşiv hazır • "+items.size()+" uygulama"));}catch(Exception ignored){}}).start();}
    private void scanDir(File d,int depth){if(depth>3||d==null||!d.canRead())return;File[] fs=d.listFiles();if(fs==null)return;for(File f:fs){if(f.isDirectory()){scanDir(f,depth+1);continue;}if(!f.getName().toLowerCase().endsWith(".apk"))continue;AppItem it=new AppItem();it.local=true;it.localPath=f.getAbsolutePath();it.name=f.getName().replaceFirst("(?i)\\.apk$","");it.category="USB";try{PackageInfo pi=getPackageManager().getPackageArchiveInfo(f.getAbsolutePath(),0);if(pi!=null){it.pkg=pi.packageName;it.versionName=pi.versionName;it.versionCode=Build.VERSION.SDK_INT>=28?pi.getLongVersionCode():pi.versionCode;if(pi.applicationInfo!=null){pi.applicationInfo.sourceDir=f.getAbsolutePath();pi.applicationInfo.publicSourceDir=f.getAbsolutePath();CharSequence l=pi.applicationInfo.loadLabel(getPackageManager());if(l!=null)it.name=l.toString();}}}catch(Exception ignored){}synchronized(this){items.add(it);}}}

    private void requestStorageIfNeeded(){if(Build.VERSION.SDK_INT>=23&&Build.VERSION.SDK_INT<=32&&checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},7);}
    private String sha256(File f)throws Exception{MessageDigest md=MessageDigest.getInstance("SHA-256");InputStream in=new FileInputStream(f);byte[] b=new byte[16384];int n;while((n=in.read(b))>0)md.update(b,0,n);in.close();StringBuilder s=new StringBuilder();for(byte x:md.digest())s.append(String.format("%02x",x));return s.toString();}
    private String readAll(InputStream in)throws Exception{BufferedReader r=new BufferedReader(new InputStreamReader(in,"UTF-8"));StringBuilder b=new StringBuilder();String line;while((line=r.readLine())!=null)b.append(line);r.close();return b.toString();}
    private String safe(String s){return s.replaceAll("[^A-Za-z0-9._-]","_");}
    private TextView button(String s){TextView t=text(s,14,true);t.setGravity(Gravity.CENTER);t.setFocusable(true);t.setClickable(true);t.setBackground(cardBg(false));t.setOnFocusChangeListener((v,f)->v.setBackground(cardBg(f)));return t;}
    private GradientDrawable cardBg(boolean f){return bg(f?new int[]{0xFF7130A5,0xFF442061}:new int[]{0xD5261734,0xC8140E1E},16,f?0xFFF0D7FF:0x665B3D6B);}
    private GradientDrawable pill(){GradientDrawable d=bg(new int[]{0xDD3E1855,0xDD23102E},12,0x777E5B8E);return d;}
    private GradientDrawable bg(int[] c,int r,int stroke){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,c);d.setCornerRadius(dp(r));if(stroke!=0)d.setStroke(dp(1),stroke);return d;}
    private TextView text(String s,int sp,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(sp);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
