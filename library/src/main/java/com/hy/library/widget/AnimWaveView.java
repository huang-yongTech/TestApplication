package com.hy.library.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by huangyong on 2019/4/16
 * 贝赛尔曲线实现的波浪
 */
public class AnimWaveView extends View {
    private Paint mWavePaint;
    private Path mWavePath;

    private int mItemWaveLength;
    private int mDx;

    private ValueAnimator mAnimator;

    public AnimWaveView(Context context) {
        this(context, null);
    }

    public AnimWaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mWavePaint = new Paint();
        mWavePaint.setAntiAlias(true);
        mWavePaint.setStyle(Paint.Style.FILL);
        mWavePaint.setColor(Color.GREEN);

        mWavePath = new Path();
        //这里有可能会导致AndroidStudio卡死，要万分小心
        mItemWaveLength = 800;

        mAnimator = ValueAnimator.ofInt(0, 800);
        mAnimator.setDuration(2000);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDx = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mItemWaveLength <= 0) {
            return;
        }

        mWavePath.reset();

        int height = 300;
        int halfWaveLen = mItemWaveLength / 2;

        //移动到初始位置
        mWavePath.moveTo(-mItemWaveLength + mDx, height);
        for (int i = -mItemWaveLength; i < getWidth() + mItemWaveLength; i += mItemWaveLength) {
            mWavePath.rQuadTo(halfWaveLen / 2f, -100, halfWaveLen, 0);
            mWavePath.rQuadTo(halfWaveLen / 2f, 100, halfWaveLen, 0);
        }

        //闭合路径
        mWavePath.lineTo(getWidth(), getHeight());
        mWavePath.lineTo(0, getHeight());
        mWavePath.close();

        canvas.drawPath(mWavePath, mWavePaint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }
}
