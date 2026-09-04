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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DiziFilmActivity extends Activity {
    @Override protected void onCreate(Bundle b){super.onCreate(b);requestWindowFeature(Window.FEATURE_NO_TITLE);getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);hide();setContentView(build());}
    @Override public void onWindowFocusChanged(boolean f){super.onWindowFocusChanged(f);if(f)hide();}
    private void hide(){getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);}

    private View build(){LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(42),dp(28),dp(42),dp(30));root.setBackground(bg(new int[]{0xFF100716,0xFF291039,0xFF08060D},24,0));
        LinearLayout head=new LinearLayout(this);head.setGravity(Gravity.CENTER_VERTICAL);TextView back=button("‹  Geri");back.setOnClickListener(v->finish());head.addView(back,new LinearLayout.LayoutParams(dp(150),dp(54)));LinearLayout title=new LinearLayout(this);title.setOrientation(LinearLayout.VERTICAL);title.setPadding(dp(24),0,0,0);TextView h=text("Dizi & Film",30,true);TextView sub=text("Ücretsiz ve resmî içerik kaynakları",12,false);sub.setTextColor(0xFFBDA8C8);title.addView(h);title.addView(sub);head.addView(title,new LinearLayout.LayoutParams(0,dp(64),1));root.addView(head);
        TextView note=text("Bir servis cihazda kuruluysa uygulaması açılır; kurulu değilse resmî web sayfası açılır.",12,false);note.setTextColor(0xFFCDB9D7);note.setPadding(dp(6),dp(18),0,dp(8));root.addView(note);
        LinearLayout cards=new LinearLayout(this);cards.setOrientation(LinearLayout.HORIZONTAL);cards.setGravity(Gravity.CENTER);
        cards.addView(card("▶","puhutv","Ücretsiz dizi ve film",()->openWeb("https://puhutv.com/")),lp());
        cards.addView(card("T","tabii","TRT dizi • film • belgesel",()->openAppOrWeb("com.trt.tabii.android","https://www.tabii.com/tr")),lp());
        cards.addView(card("TRT","TRT İzle","TRT yapımları ve arşiv",()->openWeb("https://www.trtizle.com/")),lp());
        cards.addView(card("P","Plex","Reklam destekli ücretsiz film ve TV",()->openAppOrWeb("com.plexapp.android","https://www.plex.tv/watch-free-tv/")),lp());
        root.addView(cards,new LinearLayout.LayoutParams(-1,0,1));View first=cards.getChildAt(0);if(first!=null)first.requestFocus();return root;}

    private View card(String icon,String title,String sub,Runnable action){LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setGravity(Gravity.CENTER);box.setPadding(dp(18),dp(18),dp(18),dp(18));box.setFocusable(true);box.setClickable(true);box.setBackground(cardBg(false));TextView i=text(icon,icon.length()>1?20:36,true);i.setGravity(Gravity.CENTER);i.setTextColor(0xFFE9C8FF);TextView t=text(title,22,true);t.setGravity(Gravity.CENTER);t.setPadding(0,dp(12),0,0);TextView s=text(sub,11,false);s.setGravity(Gravity.CENTER);s.setTextColor(0xFFBDA8C8);s.setPadding(0,dp(7),0,0);s.setMaxLines(2);box.addView(i);box.addView(t);box.addView(s);box.setOnClickListener(v->action.run());box.setOnFocusChangeListener((v,f)->{v.animate().scaleX(f?1.05f:1).scaleY(f?1.05f:1).translationY(f?-dp(3):0).setDuration(120).start();v.setBackground(cardBg(f));i.setTextColor(f?Color.WHITE:0xFFE9C8FF);});return box;}
    private void openAppOrWeb(String pkg,String url){try{Intent i=getPackageManager().getLaunchIntentForPackage(pkg);if(i!=null){startActivity(i);return;}}catch(Exception ignored){}openWeb(url);}
    private void openWeb(String url){try{Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse(url));startActivity(i);}catch(Exception e){Toast.makeText(this,"Bu bağlantıyı açacak tarayıcı bulunamadı.",Toast.LENGTH_SHORT).show();}}
    private LinearLayout.LayoutParams lp(){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(250),1);p.setMargins(dp(10),dp(22),dp(10),dp(22));return p;}
    private TextView button(String s){TextView t=text(s,14,true);t.setGravity(Gravity.CENTER);t.setFocusable(true);t.setClickable(true);t.setBackground(cardBg(false));t.setOnFocusChangeListener((v,f)->v.setBackground(cardBg(f)));return t;}
    private GradientDrawable cardBg(boolean f){return bg(f?new int[]{0xFF7130A5,0xFF442061}:new int[]{0xD5261734,0xC8140E1E},18,f?0xFFF0D7FF:0x665B3D6B);}
    private GradientDrawable bg(int[] c,int r,int stroke){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,c);d.setCornerRadius(dp(r));if(stroke!=0)d.setStroke(dp(1),stroke);return d;}
    private TextView text(String s,int sp,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(sp);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}
    private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
