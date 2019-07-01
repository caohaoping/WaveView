package com.example.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by hp on 2019/6/26.
 * TODO
 */
public class WaveXfermode extends View {

    private static final String TAG = WaveXfermode.class.getSimpleName();
    private int width = DimentionUtils.dip2px(getContext(), 150);
    private int height = DimentionUtils.dip2px(getContext(), 150);
    private PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private Xfermode textXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);
    private int color;
    private String text;
    private Paint wavePaint;
    private Paint textPaint;
    private Rect rect;
    private Paint borderPaint;
    private Path path;
    private float currentPercent;
    private Bitmap bitmap;

    public WaveXfermode(Context context) {
        this(context, null);
    }

    public WaveXfermode(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveXfermode(Context context, @Nullable AttributeSet attrs,
                        int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Wave);
        color = array.getColor(R.styleable.Wave_color, Color.BLUE);
        text = array.getString(R.styleable.Wave_text);
        array.recycle();

        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.BLUE);

        wavePaint = new Paint();
        wavePaint.setAntiAlias(true);
        wavePaint.setStyle(Paint.Style.FILL);
        wavePaint.setColor(color);
        wavePaint.setDither(true);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextAlign(Paint.Align.CENTER);

        rect = new Rect(0, 0, width, height);
        path = new Path();

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
        drawCenterText(canvas, textPaint);

        path = getWaveActionPath(currentPercent);

        int flag = canvas.saveLayer(0, 0, getWidth(), getHeight(), null,
                Canvas.ALL_SAVE_FLAG);
        canvas.drawPath(path, wavePaint);
        wavePaint.setXfermode(xfermode);
        canvas.drawBitmap(bitmap, 0, 0, wavePaint);

        textPaint.setColor(Color.WHITE);
        textPaint.setXfermode(textXfermode);
        drawCenterText(canvas, textPaint);

//        canvas.restoreToCount(flag);
        canvas.restore();
        wavePaint.setXfermode(null);
        textPaint.setXfermode(null);

    }

    private void drawCenterText(Canvas canvas, Paint textPaint) {
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        Log.i(TAG, "onDraw textPaint: " + top + ", " + bottom);
        Log.i(TAG, "onDraw textPaint: " + fontMetrics.ascent + ", " + fontMetrics.descent);
        int centerY = (int) (rect.centerY() - top / 2 - bottom / 2);
        Log.i(TAG,
                "onDraw rect: " + rect.centerX() + ", " + rect.centerY() + ", centerY: " + centerY);
        canvas.drawText(text, rect.centerX(), centerY, textPaint);
        drawBorder(canvas, centerY);
    }

    private void drawBorder(Canvas canvas, int centerY) {
        canvas.drawRect(rect, borderPaint);
        canvas.drawCircle(rect.centerX(), rect.centerY(), 5, borderPaint);
        canvas.drawCircle(rect.centerX(), centerY, 5, borderPaint);
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
        setMeasuredDimension(width, height);
        int textSize = width / 2;
        textPaint.setTextSize(textSize);
        bitmap = getCircleBitmap(width, height);
        Log.d(TAG, "onMeasure bitmap: " + bitmap + ", width: " + width + ", height: " + height);
    }

    private Bitmap getCircleBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//		bitmap.eraseColor(Color.parseColor("#FF0000"));//填充颜色
        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(width / 2, height / 2, width / 2, wavePaint);
        return bitmap;
    }

    private Path getWaveActionPath(float percent) {
        Log.d(TAG, "getWaveActionPath: " + percent);
        path.reset();
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
