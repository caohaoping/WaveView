package com.example.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by hp on 2019/6/26.
 * TODO
 */
public class Wave extends View {

    private static final String TAG = Wave.class.getSimpleName();
    private int color;
    private String text;
    private Paint wavePaint;
    private Paint textPaint;
    private int width = DimentionUtils.dip2px(getContext(), 150);
    private int height = DimentionUtils.dip2px(getContext(), 150);
    private Rect rect;
    private Paint shapePaint;
    private Path path;
    private float currentPercent;

    public Wave(Context context) {
        this(context, null);
    }

    public Wave(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Wave(Context context, @Nullable AttributeSet attrs,
                int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Wave);
        color = array.getColor(R.styleable.Wave_color, Color.BLUE);
        text = array.getString(R.styleable.Wave_text);
        array.recycle();

        shapePaint = new Paint();
        shapePaint.setAntiAlias(true);
        shapePaint.setStyle(Paint.Style.STROKE);
        shapePaint.setColor(Color.BLUE);

        wavePaint = new Paint();
        wavePaint.setAntiAlias(true);
        wavePaint.setStyle(Paint.Style.FILL);
        wavePaint.setColor(Color.BLUE);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextAlign(Paint.Align.CENTER);

        rect = new Rect(0, 0, width, height);

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentPercent = animation.getAnimatedFraction();
                Log.d(TAG, "onAnimationUpdate: " + currentPercent);
                invalidate();
            }
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw: " + width + ", " + height);
        textPaint.setColor(color);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        Log.i(TAG, "onDraw textPaint: " + top + ", " + bottom);
        Log.i(TAG, "onDraw textPaint: " + fontMetrics.ascent + ", " + fontMetrics.descent);
        int centerY = (int) (rect.centerY() - top / 2 - bottom / 2);
        Log.i(TAG, "onDraw rect: " + rect.centerX() + ", " + rect.centerY() + ", centerY: " + centerY);
        canvas.drawText(text, rect.centerX(), centerY, textPaint);

        canvas.drawRect(rect, shapePaint);
        canvas.drawCircle(rect.centerX(), rect.centerY(), 5, shapePaint);
        canvas.drawCircle(rect.centerX(), centerY, 5, shapePaint);

        path = getActionPath(currentPercent);
        canvas.clipPath(path);
        canvas.drawCircle(width / 2, height / 2, width / 2, wavePaint);

        textPaint.setColor(Color.WHITE);
        canvas.drawText(text, rect.centerX(), centerY, textPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure: " + width + ", " + height);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        }
        int textSize = width / 2;
        textPaint.setTextSize(textSize);
        setMeasuredDimension(width, height);
    }

    private Path getActionPath(float percent) {
        Log.d(TAG, "getActionPath: " + percent);
        Path path = new Path();
        int x = -width;
        x += percent * width;
        path.moveTo(x, height / 2);
        int quadWidth = width / 4;
        int quadHeight = height / 20 * 3;
        path.rQuadTo(quadWidth, quadHeight, quadWidth * 2, 0);
        path.rQuadTo(quadWidth, -quadHeight, quadWidth * 2, 0);
        path.rQuadTo(quadWidth, quadHeight, quadWidth * 2, 0);
        path.rQuadTo(quadWidth, -quadHeight, quadWidth * 2, 0);
        path.lineTo(x + width * 2, height);
        path.lineTo(x, height);
        path.close();
        return path;
    }
}
