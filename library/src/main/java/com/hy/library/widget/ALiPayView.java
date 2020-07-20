package com.hy.library.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.hy.library.util.SizeUtils;

/**
 * Created by huangyong on 2019/4/16
 * 仿阿里支付成功动画
 */
public class ALiPayView extends View {
    private Paint mPathPaint;
    private PathMeasure mPathMeasure;
    private Path mDstPath;

    private ValueAnimator mAnimator;
    private float mCurrValue;
    //标记为，用于确保第一条path曲线在第二条path曲线绘制前能绘制完成
    private boolean mNext;

    public ALiPayView(Context context) {
        this(context, null);
    }

    public ALiPayView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ALiPayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPathPaint = new Paint();
        mPathPaint.setAntiAlias(true);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setStrokeWidth(SizeUtils.dp2px(2));

        Path circlePath = new Path();
        circlePath.addCircle(100, 100, 50, Path.Direction.CW);

        circlePath.moveTo(100 - 50 / 2f, 100);
        circlePath.lineTo(100, 100 + 50 / 2f);
        circlePath.lineTo(100 + 50 / 4f * 3, 100 - 50 / 2f);

        mPathMeasure = new PathMeasure(circlePath, false);
        mDstPath = new Path();

        mAnimator = ValueAnimator.ofFloat(0, 2);
        mAnimator.setDuration(2000);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mCurrValue < 1) {
            //绘制第一条曲线
            float end = mPathMeasure.getLength() * mCurrValue;
            mPathMeasure.getSegment(0, end, mDstPath, true);
        } else {
            //确保第一条曲线绘制完毕
            if (!mNext) {
                mNext = true;
                mPathMeasure.getSegment(0, mPathMeasure.getLength(), mDstPath, true);

                mPathMeasure.nextContour();
            }

            //绘制第二条曲线
            float end = mPathMeasure.getLength() * (mCurrValue - 1);
            mPathMeasure.getSegment(0, end, mDstPath, true);
        }

        canvas.drawPath(mDstPath, mPathPaint);
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
