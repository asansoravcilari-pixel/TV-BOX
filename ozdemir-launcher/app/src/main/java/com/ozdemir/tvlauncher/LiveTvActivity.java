package com.ozdemir.tvlauncher;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
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
    private static final int DESIGN_W = 1920;
    private static final int DESIGN_H = 1080;
    private static final int BG = 0xFF07111F;
    private static final int SURFACE = 0xFF111D2E;
    private static final int SURFACE_2 = 0xFF17253A;
    private static final int ACCENT = 0xFF2F80FF;
    private static final int TEXT_2 = 0xFFC7CDD6;
    private static final String CATALOG_URL = "https://raw.githubusercontent.com/asansoravcilari-pixel/TV-BOX/main/ozdemir-launcher/app/src/main/assets/channels.json";

    private final List<String> channels = new ArrayList<>();
    private final List<String> categories = new ArrayList<>();
    private final List<String> streamUrls = new ArrayList<>();

    private int channelIndex = 0;
    private FrameLayout stage;
    private FrameLayout playerCard;
    private LinearLayout channelList;
    private ScrollView listWrap;
    private TextView playbackStatus;
    private TextView catalogStatus;
    private TextView epgNow;
    private TextView epgNext;
    private TextView epgLater;
    private ExoPlayer player;
    private PlayerView playerView;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideSystemUi();
        loadBundledCatalog();

        player = new ExoPlayer.Builder(this).build();
        player.addListener(new Player.Listener() {
            @Override public void onPlayerError(PlaybackException error) {
                if (playbackStatus != null) playbackStatus.setText("Stream Error   •   INFO: İçerik Merkezi");
            }
        });

        setContentView(buildUi());
        showChannel(0, false);
        refreshCatalogAsync();
    }

    @Override protected void onStart() {
        super.onStart();
        if (player != null && channelIndex < streamUrls.size() && !streamUrls.get(channelIndex).isEmpty()) player.play();
    }

    @Override protected void onStop() {
        if (player != null) player.pause();
        super.onStop();
    }

    @Override protected void onDestroy() {
        if (player != null) {
            player.release();
            player = null;
        }
        super.onDestroy();
    }

    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUi();
    }

    private void hideSystemUi() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private View buildUi() {
        FrameLayout outer = new FrameLayout(this);
        outer.setBackgroundColor(Color.BLACK);
        outer.setClipChildren(false);
        outer.setClipToPadding(false);

        stage = new FrameLayout(this);
        stage.setBackgroundColor(BG);
        stage.setClipChildren(false);
        stage.setClipToPadding(false);
        outer.addView(stage, new FrameLayout.LayoutParams(DESIGN_W, DESIGN_H));

        TextView title = text("Canlı TV", 44, true, Color.WHITE);
        place(title, 96, 64, 300, 54);

        buildPlayerCard();
        buildChannelListCard();
        buildEpgCard();
        addAmbientEdges();

        outer.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, orr, ob) -> {
            float scale = Math.min((r - l) / (float) DESIGN_W, (b - t) / (float) DESIGN_H);
            stage.setPivotX(0f);
            stage.setPivotY(0f);
            stage.setScaleX(scale);
            stage.setScaleY(scale);
            stage.setX(((r - l) - DESIGN_W * scale) / 2f);
            stage.setY(((b - t) - DESIGN_H * scale) / 2f);
        });

        stage.post(this::focusCurrentRow);
        return outer;
    }

    private void buildPlayerCard() {
        playerCard = new FrameLayout(this);
        playerCard.setFocusable(true);
        playerCard.setFocusableInTouchMode(true);
        playerCard.setClickable(true);
        playerCard.setBackground(round(SURFACE, 30, 0, 0));
        place(playerCard, 96, 139, 1260, 650);

        TextView heading = text("Canlı Yayın Önizleme", 30, true, Color.WHITE);
        placeInside(playerCard, heading, 24, 20, 600, 42);

        FrameLayout playerShell = new FrameLayout(this);
        playerShell.setFocusable(false);
        playerShell.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        playerShell.setBackground(round(SURFACE_2, 26, 1, ACCENT));
        placeInside(playerCard, playerShell, 24, 76, 1212, 480);

        playerView = new PlayerView(this);
        playerView.setUseController(false);
        playerView.setKeepContentOnPlayerReset(true);
        playerView.setFocusable(false);
        playerView.setFocusableInTouchMode(false);
        playerView.setPlayer(player);
        playerView.setBackgroundColor(0xFF0B1421);
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(-1, -1);
        p.setMargins(2, 2, 2, 2);
        playerShell.addView(playerView, p);

        playbackStatus = text("Playing   •   Paused   •   Buffering   •   No Signal   •   Stream Error   •   Reconnecting", 18, false, TEXT_2);
        placeInside(playerCard, playbackStatus, 24, 572, 1160, 30);

        catalogStatus = text(channels.size() + " kanal", 15, false, TEXT_2);
        catalogStatus.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        placeInside(playerCard, catalogStatus, 930, 22, 280, 34);

        playerCard.setOnFocusChangeListener((v, focused) -> v.setBackground(round(SURFACE, 30, focused ? 3 : 0, focused ? ACCENT : 0)));
        playerCard.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                focusCurrentRow();
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_CHANNEL_UP || keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
                changeChannel(1, false);
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN || keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
                changeChannel(-1, false);
                return true;
            }
            return false;
        });
    }

    private void buildChannelListCard() {
        FrameLayout card = new FrameLayout(this);
        card.setBackground(round(SURFACE, 30, 0, 0));
        place(card, 1380, 139, 444, 650);

        TextView heading = text("Kanal Listesi", 28, true, Color.WHITE);
        placeInside(card, heading, 20, 18, 300, 40);

        listWrap = new ScrollView(this);
        listWrap.setFillViewport(false);
        listWrap.setVerticalScrollBarEnabled(false);
        listWrap.setSmoothScrollingEnabled(true);
        listWrap.setFocusable(false);
        listWrap.setBackgroundColor(Color.TRANSPARENT);
        FrameLayout.LayoutParams sw = new FrameLayout.LayoutParams(404, 570);
        sw.leftMargin = 20;
        sw.topMargin = 64;
        card.addView(listWrap, sw);

        channelList = new LinearLayout(this);
        channelList.setOrientation(LinearLayout.VERTICAL);
        channelList.setFocusable(false);
        channelList.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        channelList.setPadding(0, 0, 0, 18);
        listWrap.addView(channelList, new ScrollView.LayoutParams(-1, -2));
        rebuildList();
    }

    private void buildEpgCard() {
        FrameLayout epg = new FrameLayout(this);
        epg.setBackground(round(SURFACE, 28, 0, 0));
        place(epg, 96, 811, 1728, 220);

        TextView heading = text("EPG / Rehber", 28, true, Color.WHITE);
        placeInside(epg, heading, 22, 18, 300, 40);

        epgNow = epgBlock(epg, "Şimdi", 22, true);
        epgNext = epgBlock(epg, "Sıradaki", 556, false);
        epgLater = epgBlock(epg, "Daha Sonra", 1090, false);
    }

    private TextView epgBlock(FrameLayout parent, String heading, int x, boolean active) {
        FrameLayout block = new FrameLayout(this);
        block.setBackground(round(active ? ACCENT : SURFACE_2, 18, 0, 0));
        placeInside(parent, block, x, 68, 520, 110);

        TextView h = text(heading, 20, true, Color.WHITE);
        placeInside(block, h, 16, 12, 220, 30);

        TextView body = text("Program bilgisi", 17, false, active ? 0xFFE8F0FF : TEXT_2);
        placeInside(block, body, 16, 44, 480, 44);
        return body;
    }

    private void addAmbientEdges() {
        View top = new View(this); top.setBackgroundColor(0x242F80FF); place(top, 0, 0, 1920, 8);
        View bottom = new View(this); bottom.setBackgroundColor(0x242F80FF); place(bottom, 0, 1072, 1920, 8);
        View left = new View(this); left.setBackgroundColor(0x242F80FF); place(left, 0, 0, 8, 1080);
        View right = new View(this); right.setBackgroundColor(0x242F80FF); place(right, 1912, 0, 8, 1080);
    }

    private void rebuildList() {
        if (channelList == null) return;
        channelList.removeAllViews();
        TextView first = null;
        TextView previous = null;

        for (int i = 0; i < channels.size(); i++) {
            final int pos = i;
            String cat = pos < categories.size() ? categories.get(pos) : "";
            TextView row = text((i + 1) + "   " + channels.get(i) + (cat.isEmpty() ? "" : "   ·   " + cat), 20, i == channelIndex, Color.WHITE);
            row.setId(View.generateViewId());
            row.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            row.setPadding(14, 0, 14, 0);
            row.setFocusable(true);
            row.setFocusableInTouchMode(true);
            row.setClickable(true);
            row.setBackground(rowBg(i == channelIndex, false));

            row.setOnClickListener(v -> showChannel(pos, true));
            row.setOnFocusChangeListener((v, focused) -> {
                boolean selected = pos == channelIndex;
                v.setBackground(rowBg(selected, focused));
                ((TextView) v).setTypeface(Typeface.DEFAULT, (focused || selected) ? Typeface.BOLD : Typeface.NORMAL);
                if (focused && listWrap != null) listWrap.post(() -> listWrap.smoothScrollTo(0, Math.max(0, v.getTop() - 120)));
            });
            row.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    focusChannelRow(pos - 1);
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    focusChannelRow(pos + 1);
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    if (playerCard != null) playerCard.requestFocus();
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    showChannel(pos, true);
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_CHANNEL_UP || keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
                    changeChannel(1, true);
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN || keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
                    changeChannel(-1, true);
                    return true;
                }
                return false;
            });

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, 54);
            lp.setMargins(0, 3, 0, 3);
            channelList.addView(row, lp);

            if (first == null) first = row;
            if (previous != null) {
                previous.setNextFocusDownId(row.getId());
                row.setNextFocusUpId(previous.getId());
            }
            previous = row;
        }

        if (first != null && previous != null) {
            first.setNextFocusUpId(previous.getId());
            previous.setNextFocusDownId(first.getId());
        }
    }

    @Override public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getAction() != KeyEvent.ACTION_DOWN) return super.dispatchKeyEvent(e);
        int k = e.getKeyCode();

        if (k == KeyEvent.KEYCODE_MENU || k == KeyEvent.KEYCODE_GUIDE) {
            focusCurrentRow();
            return true;
        }
        if (k == KeyEvent.KEYCODE_INFO) {
            ContentProviderBridge.launchOrExplain(this);
            return true;
        }

        View focus = getCurrentFocus();
        int focusedIndex = indexOfFocusedChannel(focus);

        if (focusedIndex >= 0) {
            if (k == KeyEvent.KEYCODE_DPAD_UP) {
                focusChannelRow(focusedIndex - 1);
                return true;
            }
            if (k == KeyEvent.KEYCODE_DPAD_DOWN) {
                focusChannelRow(focusedIndex + 1);
                return true;
            }
            if (k == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (playerCard != null) playerCard.requestFocus();
                return true;
            }
            if (k == KeyEvent.KEYCODE_DPAD_CENTER || k == KeyEvent.KEYCODE_ENTER || k == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                showChannel(focusedIndex, true);
                return true;
            }
        }

        if (focus == playerCard) {
            if (k == KeyEvent.KEYCODE_DPAD_RIGHT || k == KeyEvent.KEYCODE_DPAD_CENTER || k == KeyEvent.KEYCODE_ENTER) {
                focusCurrentRow();
                return true;
            }
        }

        if (k == KeyEvent.KEYCODE_CHANNEL_UP || k == KeyEvent.KEYCODE_MEDIA_NEXT) {
            changeChannel(1, true);
            return true;
        }
        if (k == KeyEvent.KEYCODE_CHANNEL_DOWN || k == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
            changeChannel(-1, true);
            return true;
        }

        return super.dispatchKeyEvent(e);
    }

    private int indexOfFocusedChannel(View focus) {
        if (focus == null || channelList == null) return -1;
        for (int i = 0; i < channelList.getChildCount(); i++) {
            if (channelList.getChildAt(i) == focus) return i;
        }
        return -1;
    }

    private void focusChannelRow(int index) {
        if (channelList == null || channelList.getChildCount() == 0) return;
        int count = channelList.getChildCount();
        int idx = ((index % count) + count) % count;
        View row = channelList.getChildAt(idx);
        if (row != null) {
            row.requestFocus();
            if (listWrap != null) listWrap.post(() -> listWrap.smoothScrollTo(0, Math.max(0, row.getTop() - 120)));
        }
    }

    private void focusCurrentRow() {
        if (channelList == null || channelList.getChildCount() == 0) {
            if (playerCard != null) playerCard.requestFocus();
            return;
        }
        focusChannelRow(channelIndex);
    }

    private void changeChannel(int delta, boolean focusRow) {
        if (channels.isEmpty()) return;
        int next = (channelIndex + delta + channels.size()) % channels.size();
        showChannel(next, true);
        if (focusRow) focusCurrentRow();
    }

    private void showChannel(int pos, boolean notify) {
        if (channels.isEmpty()) return;
        channelIndex = Math.max(0, Math.min(pos, channels.size() - 1));
        playSelected();
        updateEpg();
        rebuildList();
        if (notify) Toast.makeText(this, channels.get(channelIndex), Toast.LENGTH_SHORT).show();
        focusCurrentRow();
    }

    private void updateEpg() {
        if (channels.isEmpty()) return;
        String name = channels.get(channelIndex);
        if (epgNow != null) epgNow.setText(name + " • Şimdi oynatılıyor");
        if (epgNext != null) epgNext.setText("Sıradaki program bilgisi");
        if (epgLater != null) epgLater.setText("Daha sonraki program bilgisi");
    }

    private void playSelected() {
        if (player == null || playbackStatus == null) return;
        String u = channelIndex < streamUrls.size() ? streamUrls.get(channelIndex) : "";
        if (u == null || u.trim().isEmpty()) {
            player.stop();
            player.clearMediaItems();
            playbackStatus.setText("No Signal   •   Resmî yayın kaynağı bağlı değil");
            return;
        }
        playbackStatus.setText("Buffering   •   " + channels.get(channelIndex));
        MediaItem item = MediaItem.fromUri(u);
        player.setMediaItem(item);
        player.prepare();
        player.play();
    }

    private void loadBundledCatalog() {
        try {
            InputStream in = getAssets().open("channels.json");
            parseCatalog(readAll(in));
        } catch (Exception ignored) {
            channels.add("TRT 1");
            categories.add("Ulusal");
            streamUrls.add("");
        }
    }

    private synchronized void parseCatalog(String json) throws Exception {
        JSONObject root = new JSONObject(json);
        JSONArray a = root.getJSONArray("channels");
        if (a.length() < 1) return;
        ArrayList<String> n = new ArrayList<>(), c = new ArrayList<>(), s = new ArrayList<>();
        for (int i = 0; i < a.length(); i++) {
            JSONObject x = a.getJSONObject(i);
            String name = x.optString("name", "").trim();
            if (!name.isEmpty()) {
                n.add(name);
                c.add(x.optString("category", "Diğer"));
                s.add(x.optString("streamUrl", "").trim());
            }
        }
        if (n.isEmpty()) return;
        channels.clear(); channels.addAll(n);
        categories.clear(); categories.addAll(c);
        streamUrls.clear(); streamUrls.addAll(s);
    }

    private void refreshCatalogAsync() {
        new Thread(() -> {
            HttpURLConnection h = null;
            try {
                h = (HttpURLConnection) new URL(CATALOG_URL).openConnection();
                h.setConnectTimeout(4000);
                h.setReadTimeout(5000);
                h.setUseCaches(false);
                h.setRequestProperty("Accept", "application/json");
                if (h.getResponseCode() == 200) {
                    String body = readAll(h.getInputStream());
                    parseCatalog(body);
                    runOnUiThread(() -> {
                        if (channelIndex >= channels.size()) channelIndex = 0;
                        rebuildList();
                        showChannel(channelIndex, false);
                        int live = 0;
                        for (String s : streamUrls) if (s != null && !s.isEmpty()) live++;
                        if (catalogStatus != null) catalogStatus.setText(channels.size() + " kanal   •   " + live + " yayın bağlı");
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (catalogStatus != null) catalogStatus.setText(channels.size() + " kanal   •   Çevrimdışı katalog");
                });
            } finally {
                if (h != null) h.disconnect();
            }
        }).start();
    }

    private String readAll(InputStream in) throws Exception {
        BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder b = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) b.append(line);
        r.close();
        return b.toString();
    }

    private TextView text(String s, int sp, boolean bold, int color) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(sp);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private GradientDrawable rowBg(boolean selected, boolean focused) {
        int fill = focused ? ACCENT : (selected ? 0xFF1D4F9D : SURFACE);
        int stroke = focused ? ACCENT : (selected ? 0xFF3E8DFF : 0x00000000);
        return round(fill, 18, focused || selected ? 2 : 0, stroke);
    }

    private GradientDrawable round(int color, int radius, int strokeWidth, int strokeColor) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        if (strokeWidth > 0) d.setStroke(strokeWidth, strokeColor);
        return d;
    }

    private void place(View v, int x, int y, int w, int h) {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
        lp.leftMargin = x;
        lp.topMargin = y;
        stage.addView(v, lp);
    }

    private void placeInside(FrameLayout parent, View v, int x, int y, int w, int h) {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
        lp.leftMargin = x;
        lp.topMargin = y;
        parent.addView(v, lp);
    }
}
