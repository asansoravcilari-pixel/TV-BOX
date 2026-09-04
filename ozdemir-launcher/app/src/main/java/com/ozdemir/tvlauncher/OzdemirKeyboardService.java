package com.ozdemir.tvlauncher;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.inputmethodservice.InputMethodService;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OzdemirKeyboardService extends InputMethodService {
    private boolean caps=false;
    @Override public View onCreateInputView(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(16),dp(12),dp(16),dp(12));root.setBackground(bg(0xFF14091B,0xFF2F1140,0));
        TextView brand=key("ÖZDEMİR TV KLAVYE",14,false);brand.setFocusable(false);brand.setTextColor(0xFFDDB8F0);root.addView(brand,new LinearLayout.LayoutParams(-1,dp(36)));
        String[][] rows={{"1","2","3","4","5","6","7","8","9","0"},{"q","w","e","r","t","y","u","ı","o","p","ğ","ü"},{"a","s","d","f","g","h","j","k","l","ş","i"},{"⇧","z","x","c","v","b","n","m","ö","ç","⌫"}};
        for(String[] r:rows){LinearLayout line=row();for(String k:r)line.addView(key(k,18,true),new LinearLayout.LayoutParams(0,dp(50),1f));root.addView(line);}
        LinearLayout last=row();last.addView(key("123",14,true),new LinearLayout.LayoutParams(0,dp(54),1f));last.addView(key("BOŞLUK",14,true),new LinearLayout.LayoutParams(0,dp(54),4f));last.addView(key("ENTER",14,true),new LinearLayout.LayoutParams(0,dp(54),1.5f));last.addView(key("KAPAT",13,true),new LinearLayout.LayoutParams(0,dp(54),1.5f));root.addView(last);return root;
    }
    private LinearLayout row(){LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.HORIZONTAL);return r;}
    private TextView key(String label,int size,boolean action){TextView t=new TextView(this);t.setText(label);t.setTextColor(Color.WHITE);t.setTextSize(size);t.setGravity(Gravity.CENTER);t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);t.setFocusable(true);t.setClickable(true);t.setPadding(dp(3),dp(3),dp(3),dp(3));t.setBackground(bg(0xD92B1937,0xD9181020,1));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-2,-1);p.setMargins(dp(3),dp(3),dp(3),dp(3));t.setLayoutParams(p);if(action){t.setOnFocusChangeListener((v,f)->{v.setBackground(bg(f?0xFF7D2EB0:0xD92B1937,f?0xFF4A1768:0xD9181020,f?2:1));v.animate().scaleX(f?1.05f:1f).scaleY(f?1.05f:1f).setDuration(90).start();});t.setOnClickListener(v->press(((TextView)v).getText().toString()));}return t;}
    private void press(String k){InputConnection ic=getCurrentInputConnection();if(ic==null)return;if("⌫".equals(k)){ic.deleteSurroundingText(1,0);return;}if("ENTER".equals(k)){ic.commitText("\n",1);return;}if("BOŞLUK".equals(k)){ic.commitText(" ",1);return;}if("KAPAT".equals(k)){requestHideSelf(0);return;}if("⇧".equals(k)){caps=!caps;return;}if("123".equals(k))return;String out=caps?k.toUpperCase(new java.util.Locale("tr","TR")):k;ic.commitText(out,1);}
    private GradientDrawable bg(int a,int b,int stroke){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{a,b});d.setCornerRadius(dp(13));if(stroke>0)d.setStroke(dp(stroke),stroke>1?0xFFF0D7FF:0x66533E60);return d;}
    private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
