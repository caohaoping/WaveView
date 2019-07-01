package com.example.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by hp on 2019/6/26.
 * TODO
 */
public class WaveQuad extends View {

    private static final String TAG = WaveQuad.class.getSimpleName();
    private Paint pathPaint;
    private int width = DimentionUtils.dip2px(getContext(), 150);
    private int height = DimentionUtils.dip2px(getContext(), 150);
    private Rect rect;
    private Paint shapePaint;
    private Path path;

    public WaveQuad(Context context) {
        this(context, null);
    }

    public WaveQuad(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveQuad(Context context, @Nullable AttributeSet attrs,
                    int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Wave);
        int color = array.getColor(R.styleable.Wave_color, Color.BLUE);
        array.recycle();

        shapePaint = new Paint();
        shapePaint.setAntiAlias(true);
        shapePaint.setStyle(Paint.Style.STROKE);
        shapePaint.setColor(Color.BLUE);

        pathPaint = new Paint();
        pathPaint.setAntiAlias(true);
        pathPaint.setStyle(Paint.Style.FILL);
        pathPaint.setColor(color);

        rect = new Rect(0, 0, width, height);

        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw: " + width + ", " + height);
        canvas.drawRect(rect, shapePaint);
        canvas.drawCircle(rect.centerX(), rect.centerY(), 5, shapePaint);
        path.moveTo(0, height / 2);
        path.quadTo(50, -50, 100, 0);
        path.quadTo(50, 50, 100, 0);
        path.quadTo(50, -50, 100, 0);
        path.quadTo(50, 50, 100, 0);
        path.quadTo(50, -50, 100, 0);
        canvas.drawPath(path, pathPaint);
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
    }

}
