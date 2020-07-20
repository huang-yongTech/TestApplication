package com.hy.library.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.hy.library.R;

/**
 * Created by huangyong on 2019/4/18
 * 文字内显示波浪view
 */
public class TextWaveView extends View {
    private Paint mPaint;
    private Path mWavePath;
    private Bitmap mSrcBitmap;
    private Bitmap mDstBitmap;
    private Canvas mDstCanvas;

    private float mWaveLength;
    private float mWaveHeight;
    private float mWaveDx;

    private PorterDuffXfermode mFermode;

    private ValueAnimator mAnimator;

    public TextWaveView(Context context) {
        this(context, null);
    }

    public TextWaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.GREEN);

        mWavePath = new Path();

        mSrcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.text_shade);
        mDstBitmap = Bitmap.createBitmap(mSrcBitmap.getWidth(), mSrcBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mDstCanvas = new Canvas();

        mFermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

        mWaveLength = mSrcBitmap.getWidth() / 2.0f;
        mWaveHeight = mSrcBitmap.getHeight() / 2.f;

        createAnim();
    }

    private void createAnim() {
        mAnimator = ValueAnimator.ofFloat(0, mWaveLength);
        mAnimator.setDuration(2000);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mWaveDx = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desireWidth = 0, desireHeight = 0;
        if (mSrcBitmap != null) {
            desireWidth = mSrcBitmap.getWidth();
            desireHeight = mSrcBitmap.getHeight();
        }

        int width = getDefaultSize(widthMeasureSpec, getPaddingStart() + getPaddingEnd() + desireWidth,
                ViewCompat.getMinimumWidth(this));
        int height = getDefaultSize(heightMeasureSpec, getPaddingTop() + getPaddingBottom() + desireHeight,
                ViewCompat.getMinimumHeight(this));

        setMeasuredDimension(width, height);
    }

    private int getDefaultSize(int spec, int desireSize, int min) {
        int size = MeasureSpec.getSize(spec);
        int mode = MeasureSpec.getMode(spec);

        switch (mode) {
            case MeasureSpec.EXACTLY:
                return size;
            case MeasureSpec.AT_MOST:
                return Math.min(size, Math.max(desireSize, min));
            case MeasureSpec.UNSPECIFIED:
            default:
                return Math.max(desireSize, min);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //波浪是需要绘制的，因此波浪的代码需要放到这里
        createWave(getWidth());

        //将波纹绘制到目标图层
        mDstCanvas.setBitmap(mDstBitmap);
        //清除源图像，这里就是文字图像
        mDstCanvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
        mDstCanvas.drawPath(mWavePath, mPaint);

        //先绘制文字，因为文字要全部显示
        canvas.drawBitmap(mSrcBitmap, 0, 0, mPaint);

        int layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), mPaint);
        canvas.drawBitmap(mDstBitmap, 0, 0, mPaint);
        mPaint.setXfermode(mFermode);
        canvas.drawBitmap(mSrcBitmap, 0, 0, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(layerId);
    }

    private void createWave(int width) {
        mWavePath.reset();

        mWavePath.moveTo(-mWaveLength + mWaveDx, mWaveHeight);
        float halfWaveLen = mWaveLength;
        float halfWaveHeight = mWaveHeight / 2;

        for (float i = -mWaveLength; i <= width + mWaveLength; i += mWaveLength) {
            mWavePath.rQuadTo(halfWaveLen / 2, -halfWaveHeight, halfWaveLen, 0);
            mWavePath.rQuadTo(halfWaveLen / 2, halfWaveHeight, halfWaveLen, 0);
        }

        //这里注意path的lineTo顺序
        mWavePath.lineTo(mSrcBitmap.getWidth(), mSrcBitmap.getHeight());
        mWavePath.lineTo(0, mSrcBitmap.getHeight());
        mWavePath.close();
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
