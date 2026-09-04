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
    @Override protected void onDraw(Canvas c){super.onDraw(c);float h=getHeight();float r=h*.29f;float cx=r+10f;float cy=h*.47f;
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(r*.16f);p.setColor(0xFFE9C8FF);p.setShadowLayer(r*.42f,0,0,0xAA9B42D3);c.drawOval(new RectF(cx-r,cy-r,cx+r,cy+r),p);
        p.clearShadowLayer();p.setStrokeWidth(r*.11f);p.setColor(0xFFFFFFFF);c.drawArc(new RectF(cx-r*.55f,cy-r*1.02f,cx+r*.55f,cy-r*.42f),200,140,false,p);
        p.setStyle(Paint.Style.FILL);drawPerson(c,cx-r*.40f,cy-r*.18f,r*.16f,r*.48f);drawPerson(c,cx+r*.40f,cy-r*.18f,r*.16f,r*.48f);drawPerson(c,cx-r*.13f,cy+r*.02f,r*.11f,r*.32f);drawPerson(c,cx+r*.15f,cy+r*.04f,r*.10f,r*.29f);
        float tx=cx+r+18f;p.setColor(Color.WHITE);p.setTextSize(h*.29f);p.setFakeBoldText(true);c.drawText("ÖZDEMİR",tx,h*.47f,p);p.setTextSize(h*.135f);p.setColor(0xFFD6B8E4);p.setLetterSpacing(.13f);c.drawText("TV OS",tx+2f,h*.69f,p);p.setLetterSpacing(0);
    }
    private void drawPerson(Canvas c,float x,float y,float head,float body){p.setColor(0xFFFFFFFF);c.drawCircle(x,y,head,p);RectF b=new RectF(x-head*1.05f,y+head*.9f,x+head*1.05f,y+head*.9f+body);c.drawRoundRect(b,head,head,p);}
}
