package com.ozdemir.tvlauncher;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class LiveTvActivity extends Activity {
    private final String[] channels={"TRT 1","TRT Haber","TRT Spor","ATV","Kanal D","Show TV","Star TV","TV8","NOW","NTV","CNN TÜRK","A Haber"};
    private int channelIndex=0;
    private TextView channelName,channelNumber,hint;
    private LinearLayout channelList;
    private ScrollView listWrap;
    private boolean listOpen=false;

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideSystemUi();setContentView(buildUi());showChannel(0,false);
    }
    @Override public void onWindowFocusChanged(boolean f){super.onWindowFocusChanged(f);if(f)hideSystemUi();}
    private void hideSystemUi(){getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);}

    private View buildUi(){
        FrameLayout root=new FrameLayout(this);root.setBackgroundColor(0xFF050407);root.setFocusable(true);root.setFocusableInTouchMode(true);root.requestFocus();

        LinearLayout info=new LinearLayout(this);info.setOrientation(LinearLayout.VERTICAL);info.setGravity(Gravity.LEFT|Gravity.BOTTOM);info.setPadding(dp(38),dp(26),dp(38),dp(28));
        GradientDrawable fade=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{0xE8050407,0xA80A0710,0x00100A18});info.setBackground(fade);
        FrameLayout.LayoutParams ip=new FrameLayout.LayoutParams(-1,dp(160),Gravity.BOTTOM);root.addView(info,ip);

        channelNumber=text("01",16,true);channelNumber.setTextColor(0xFFCFA9E8);info.addView(channelNumber);
        channelName=text("TRT 1",30,true);channelName.setPadding(0,dp(3),0,0);info.addView(channelName);
        hint=text("◀ ▶ Kanal değiştir   •   OK Kanal listesi",13,false);hint.setTextColor(0xFFB9A7C2);hint.setPadding(0,dp(7),0,0);info.addView(hint);

        TextView brand=text("ÖZDEMİR CANLI TV",14,true);brand.setTextColor(0xFFE8D5F5);brand.setPadding(dp(22),dp(12),dp(22),dp(12));brand.setBackground(pill(false));FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(-2,-2,Gravity.TOP|Gravity.LEFT);bp.setMargins(dp(30),dp(24),0,0);root.addView(brand,bp);

        listWrap=new ScrollView(this);listWrap.setFillViewport(true);listWrap.setVisibility(View.GONE);listWrap.setBackgroundColor(0xF20D0912);channelList=new LinearLayout(this);channelList.setOrientation(LinearLayout.VERTICAL);channelList.setPadding(dp(18),dp(18),dp(18),dp(18));listWrap.addView(channelList,new ScrollView.LayoutParams(-1,-2));
        for(int i=0;i<channels.length;i++){final int pos=i;TextView row=text(String.format("%02d   %s",i+1,channels[i]),18,true);row.setPadding(dp(18),dp(14),dp(18),dp(14));row.setFocusable(true);row.setClickable(true);row.setBackground(rowBg(false));row.setOnFocusChangeListener((v,f)->v.setBackground(rowBg(f)));row.setOnClickListener(v->{showChannel(pos,true);closeList();});LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,dp(58));rp.setMargins(0,dp(4),0,dp(4));channelList.addView(row,rp);}
        FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(dp(380),-1,Gravity.RIGHT);root.addView(listWrap,lp);
        return root;
    }

    @Override public boolean dispatchKeyEvent(KeyEvent e){
        if(e.getAction()!=KeyEvent.ACTION_DOWN)return super.dispatchKeyEvent(e);
        int k=e.getKeyCode();
        if(k==KeyEvent.KEYCODE_DPAD_CENTER||k==KeyEvent.KEYCODE_ENTER){if(listOpen){View f=getCurrentFocus();if(f!=null&&f.getParent()==channelList)f.performClick();else closeList();}else openList();return true;}
        if(!listOpen&&(k==KeyEvent.KEYCODE_DPAD_RIGHT||k==KeyEvent.KEYCODE_CHANNEL_UP||k==KeyEvent.KEYCODE_MEDIA_NEXT)){changeChannel(1);return true;}
        if(!listOpen&&(k==KeyEvent.KEYCODE_DPAD_LEFT||k==KeyEvent.KEYCODE_CHANNEL_DOWN||k==KeyEvent.KEYCODE_MEDIA_PREVIOUS)){changeChannel(-1);return true;}
        if(k==KeyEvent.KEYCODE_BACK&&listOpen){closeList();return true;}
        return super.dispatchKeyEvent(e);
    }

    private void changeChannel(int d){int n=(channelIndex+d+channels.length)%channels.length;showChannel(n,true);}
    private void showChannel(int pos,boolean notify){channelIndex=pos;channelNumber.setText(String.format("%02d",pos+1));channelName.setText(channels[pos]);if(notify)Toast.makeText(this,channels[pos],Toast.LENGTH_SHORT).show();}
    private void openList(){listOpen=true;listWrap.setVisibility(View.VISIBLE);View row=channelList.getChildAt(channelIndex);if(row!=null){row.requestFocus();listWrap.post(()->listWrap.smoothScrollTo(0,row.getTop()-dp(90)));}}
    private void closeList(){listOpen=false;listWrap.setVisibility(View.GONE);listWrap.clearFocus();getWindow().getDecorView().requestFocus();}

    private TextView text(String s,int sp,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(sp);t.setTextColor(Color.WHITE);t.setGravity(Gravity.CENTER_VERTICAL);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}
    private GradientDrawable rowBg(boolean f){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,f?new int[]{0xFF6D2E9A,0xFF42195F}:new int[]{0xFF21142B,0xFF160E1E});d.setCornerRadius(dp(13));d.setStroke(dp(f?2:1),f?0xFFF0D9FF:0x554D3658);return d;}
    private GradientDrawable pill(boolean f){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xDD38174D,0xDD1B1025});d.setCornerRadius(dp(14));d.setStroke(dp(1),0x777E5B8E);return d;}
    private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
