package com.ozdemir.tvlauncher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class OzdemirLogoView extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    public OzdemirLogoView(Context c){super(c);setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
    @Override protected void onDraw(Canvas c){super.onDraw(c);float w=getWidth(),h=getHeight(),cx=w*.28f,cy=h*.46f,r=Math.min(w,h)*.29f;
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(r*.16f);p.setColor(0xFFE8C6FF);p.setShadowLayer(r*.35f,0,0,0xAA9B42D3);c.drawOval(new RectF(cx-r,cy-r,cx+r,cy+r),p);
        p.clearShadowLayer();p.setStrokeWidth(r*.11f);p.setColor(0xFFF8ECFF);c.drawArc(new RectF(cx-r*.55f,cy-r*1.02f,cx+r*.55f,cy-r*.42f),200,140,false,p);
        p.setStyle(Paint.Style.FILL);drawPerson(c,cx-r*.40f,cy-r*.18f,r*.16f,r*.48f);drawPerson(c,cx+r*.40f,cy-r*.18f,r*.16f,r*.48f);drawPerson(c,cx-r*.13f,cy+r*.02f,r*.11f,r*.32f);drawPerson(c,cx+r*.15f,cy+r*.04f,r*.10f,r*.29f);
        p.setColor(Color.WHITE);p.setTextSize(h*.30f);p.setFakeBoldText(true);c.drawText("ÖZDEMİR",w*.56f,h*.48f,p);p.setTextSize(h*.13f);p.setColor(0xFFC9AED9);p.setLetterSpacing(.12f);c.drawText("TV OS",w*.565f,h*.69f,p);p.setLetterSpacing(0);}
    private void drawPerson(Canvas c,float x,float y,float head,float body){p.setColor(0xFFF8ECFF);c.drawCircle(x,y,head,p);RectF b=new RectF(x-head*1.05f,y+head*.9f,x+head*1.05f,y+head*.9f+body);c.drawRoundRect(b,head,head,p);}
}
