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
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LiveTvActivity extends Activity {
    private static final String CATALOG_URL="https://raw.githubusercontent.com/asansoravcilari-pixel/TV-BOX/main/ozdemir-launcher/app/src/main/assets/channels.json";
    private final List<String> channels=new ArrayList<>();
    private final List<String> categories=new ArrayList<>();
    private final List<String> streamUrls=new ArrayList<>();
    private int channelIndex=0;
    private TextView channelName,channelNumber,hint,catalogStatus,playbackStatus;
    private LinearLayout channelList;
    private ScrollView listWrap;
    private boolean listOpen=false;
    private ExoPlayer player;
    private PlayerView playerView;

    @Override protected void onCreate(Bundle b){super.onCreate(b);requestWindowFeature(Window.FEATURE_NO_TITLE);getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);hideSystemUi();loadBundledCatalog();player=new ExoPlayer.Builder(this).build();player.addListener(new Player.Listener(){@Override public void onPlayerError(PlaybackException error){if(playbackStatus!=null)playbackStatus.setText("Yayın açılamadı • sonraki kanalı deneyin");}});setContentView(buildUi());showChannel(0,false);refreshCatalogAsync();}
    @Override protected void onStop(){super.onStop();if(player!=null)player.pause();}
    @Override protected void onStart(){super.onStart();if(player!=null&&!streamUrls.isEmpty()&&channelIndex<streamUrls.size()&&!streamUrls.get(channelIndex).isEmpty())player.play();}
    @Override protected void onDestroy(){if(player!=null){player.release();player=null;}super.onDestroy();}
    @Override public void onWindowFocusChanged(boolean f){super.onWindowFocusChanged(f);if(f)hideSystemUi();}
    private void hideSystemUi(){getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);}

    private View buildUi(){FrameLayout root=new FrameLayout(this);root.setBackgroundColor(0xFF050407);root.setFocusable(true);root.setFocusableInTouchMode(true);root.requestFocus();
        playerView=new PlayerView(this);playerView.setUseController(false);playerView.setKeepContentOnPlayerReset(true);playerView.setPlayer(player);root.addView(playerView,new FrameLayout.LayoutParams(-1,-1));
        LinearLayout info=new LinearLayout(this);info.setOrientation(LinearLayout.VERTICAL);info.setGravity(Gravity.LEFT|Gravity.BOTTOM);info.setPadding(dp(38),dp(26),dp(38),dp(28));GradientDrawable fade=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{0xE8050407,0xA80A0710,0x00100A18});info.setBackground(fade);root.addView(info,new FrameLayout.LayoutParams(-1,dp(188),Gravity.BOTTOM));
        channelNumber=text("001",16,true);channelNumber.setTextColor(0xFFCFA9E8);info.addView(channelNumber);channelName=text("TRT 1",30,true);channelName.setPadding(0,dp(3),0,0);info.addView(channelName);playbackStatus=text("Yayın hazırlanıyor…",11,true);playbackStatus.setTextColor(0xFFDCC4EA);playbackStatus.setPadding(0,dp(4),0,0);info.addView(playbackStatus);hint=text("◀ ▶ Kanal değiştir   •   CH+/CH− Kanal değiştir   •   OK Kanal listesi",13,false);hint.setTextColor(0xFFB9A7C2);hint.setPadding(0,dp(5),0,0);info.addView(hint);
        LinearLayout top=new LinearLayout(this);top.setOrientation(LinearLayout.VERTICAL);top.setPadding(dp(22),dp(11),dp(22),dp(11));top.setBackground(pill());TextView brand=text("ÖZDEMİR CANLI TV",14,true);top.addView(brand);catalogStatus=text(channels.size()+" kanal • Yerel katalog",10,false);catalogStatus.setTextColor(0xFFBDA8C8);top.addView(catalogStatus);FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(-2,-2,Gravity.TOP|Gravity.LEFT);bp.setMargins(dp(30),dp(24),0,0);root.addView(top,bp);
        listWrap=new ScrollView(this);listWrap.setFillViewport(true);listWrap.setVisibility(View.GONE);listWrap.setBackgroundColor(0xF20D0912);channelList=new LinearLayout(this);channelList.setOrientation(LinearLayout.VERTICAL);channelList.setPadding(dp(18),dp(18),dp(18),dp(18));listWrap.addView(channelList,new ScrollView.LayoutParams(-1,-2));rebuildList();FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(dp(430),-1,Gravity.RIGHT);root.addView(listWrap,lp);return root;}

    private void rebuildList(){if(channelList==null)return;channelList.removeAllViews();for(int i=0;i<channels.size();i++){final int pos=i;String cat=pos<categories.size()?categories.get(pos):"";boolean live=pos<streamUrls.size()&&!streamUrls.get(pos).isEmpty();TextView row=text(String.format("%03d   %s   · %s%s",i+1,channels.get(i),cat,live?"  • CANLI":""),16,true);row.setPadding(dp(18),dp(14),dp(18),dp(14));row.setFocusable(true);row.setClickable(true);row.setBackground(rowBg(false));row.setOnFocusChangeListener((v,f)->v.setBackground(rowBg(f)));row.setOnClickListener(v->{showChannel(pos,true);closeList();});LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,dp(58));rp.setMargins(0,dp(4),0,dp(4));channelList.addView(row,rp);}}

    @Override public boolean dispatchKeyEvent(KeyEvent e){if(e.getAction()!=KeyEvent.ACTION_DOWN)return super.dispatchKeyEvent(e);int k=e.getKeyCode();if(k==KeyEvent.KEYCODE_DPAD_CENTER||k==KeyEvent.KEYCODE_ENTER){if(listOpen){View f=getCurrentFocus();if(f!=null&&f.getParent()==channelList)f.performClick();else closeList();}else openList();return true;}if(!listOpen&&(k==KeyEvent.KEYCODE_DPAD_RIGHT||k==KeyEvent.KEYCODE_CHANNEL_UP||k==KeyEvent.KEYCODE_MEDIA_NEXT)){changeChannel(1);return true;}if(!listOpen&&(k==KeyEvent.KEYCODE_DPAD_LEFT||k==KeyEvent.KEYCODE_CHANNEL_DOWN||k==KeyEvent.KEYCODE_MEDIA_PREVIOUS)){changeChannel(-1);return true;}if(k==KeyEvent.KEYCODE_BACK&&listOpen){closeList();return true;}return super.dispatchKeyEvent(e);}
    private void changeChannel(int d){if(channels.isEmpty())return;showChannel((channelIndex+d+channels.size())%channels.size(),true);}
    private void showChannel(int pos,boolean notify){if(channels.isEmpty())return;channelIndex=Math.max(0,Math.min(pos,channels.size()-1));channelNumber.setText(String.format("%03d",channelIndex+1));channelName.setText(channels.get(channelIndex));playSelected();if(notify)Toast.makeText(this,channels.get(channelIndex),Toast.LENGTH_SHORT).show();}
    private void playSelected(){if(player==null)return;String u=channelIndex<streamUrls.size()?streamUrls.get(channelIndex):"";if(u==null||u.trim().isEmpty()){player.stop();player.clearMediaItems();playbackStatus.setText("Resmî yayın kaynağı henüz bağlanmadı");return;}playbackStatus.setText("Canlı yayın açılıyor…");MediaItem item=MediaItem.fromUri(u);player.setMediaItem(item);player.prepare();player.play();}
    private void openList(){listOpen=true;listWrap.setVisibility(View.VISIBLE);View row=channelList.getChildAt(channelIndex);if(row!=null){row.requestFocus();listWrap.post(()->listWrap.smoothScrollTo(0,Math.max(0,row.getTop()-dp(90))));}}
    private void closeList(){listOpen=false;listWrap.setVisibility(View.GONE);listWrap.clearFocus();getWindow().getDecorView().requestFocus();}

    private void loadBundledCatalog(){try{InputStream in=getAssets().open("channels.json");parseCatalog(readAll(in));}catch(Exception ignored){channels.add("TRT 1");categories.add("Ulusal");streamUrls.add("");}}
    private synchronized void parseCatalog(String json)throws Exception{JSONObject root=new JSONObject(json);JSONArray a=root.getJSONArray("channels");if(a.length()<1)return;ArrayList<String> n=new ArrayList<>(),c=new ArrayList<>(),s=new ArrayList<>();for(int i=0;i<a.length();i++){JSONObject x=a.getJSONObject(i);String name=x.optString("name","").trim();if(name.length()>0){n.add(name);c.add(x.optString("category","Diğer"));s.add(x.optString("streamUrl","").trim());}}if(n.isEmpty())return;channels.clear();channels.addAll(n);categories.clear();categories.addAll(c);streamUrls.clear();streamUrls.addAll(s);}
    private void refreshCatalogAsync(){new Thread(()->{HttpURLConnection h=null;try{h=(HttpURLConnection)new URL(CATALOG_URL).openConnection();h.setConnectTimeout(4000);h.setReadTimeout(5000);h.setUseCaches(false);h.setRequestProperty("Accept","application/json");if(h.getResponseCode()==200){String body=readAll(h.getInputStream());parseCatalog(body);runOnUiThread(()->{if(channelIndex>=channels.size())channelIndex=0;rebuildList();showChannel(channelIndex,false);int live=0;for(String s:streamUrls)if(s!=null&&!s.isEmpty())live++;catalogStatus.setText(channels.size()+" kanal • "+live+" yayın bağlı • Güncel");});}}catch(Exception e){runOnUiThread(()->catalogStatus.setText(channels.size()+" kanal • Çevrimdışı katalog"));}finally{if(h!=null)h.disconnect();}}).start();}
    private String readAll(InputStream in)throws Exception{BufferedReader r=new BufferedReader(new InputStreamReader(in,"UTF-8"));StringBuilder b=new StringBuilder();String line;while((line=r.readLine())!=null)b.append(line);r.close();return b.toString();}
    private TextView text(String s,int sp,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(sp);t.setTextColor(Color.WHITE);t.setGravity(Gravity.CENTER_VERTICAL);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}
    private GradientDrawable rowBg(boolean f){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,f?new int[]{0xFF6D2E9A,0xFF42195F}:new int[]{0xFF21142B,0xFF160E1E});d.setCornerRadius(dp(13));d.setStroke(dp(f?2:1),f?0xFFF0D9FF:0x554D3658);return d;}
    private GradientDrawable pill(){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xDD38174D,0xDD1B1025});d.setCornerRadius(dp(14));d.setStroke(dp(1),0x777E5B8E);return d;}
    private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
