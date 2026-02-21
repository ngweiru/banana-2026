package com.example.ewasteapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class OutlinedTextView extends AppCompatTextView {

    private Paint strokePaint;
    private Paint textPaint;
    private float strokeWidth = 8f;

    public OutlinedTextView(Context context) {
        super(context);
        init();
    }

    public OutlinedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OutlinedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(strokeWidth);
        strokePaint.setColor(0xFFFCFFE9);
        strokePaint.setAntiAlias(true);
        strokePaint.setTextAlign(Paint.Align.CENTER);

        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        String text = getText().toString();

        int textColor = getCurrentTextColor();

        if (textColor == Color.WHITE || textColor == 0xFFFFFFFF) {
            strokePaint.setColor(0xFF000000); // Black stroke for white text
        } else {
            strokePaint.setColor(0xFFFCFFE9); // Cream stroke for black text
        }

        strokePaint.setTextSize(getTextSize());
        strokePaint.setTypeface(getTypeface());

        textPaint.setTextSize(getTextSize());
        textPaint.setTypeface(getTypeface());
        textPaint.setColor(getCurrentTextColor());

        // Text position
        float x = getWidth() / 2f;
        float y = (getHeight() / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f);

        canvas.drawText(text, x, y, strokePaint);

        canvas.drawText(text, x, y, textPaint);
    }
}