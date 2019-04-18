package com.hy.library.widget;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.hy.library.R;
import com.hy.library.util.SizeUtils;

/**
 * Created by huangyong on 2019/4/12
 * 裁剪图片动画自定义view
 */
public class CropPicView extends View {
    private static final String TAG = "CropPicView";

    //显示的背景图
    private Bitmap mBitmap;

    //辅助裁剪
    private Path mPath;
    private Paint mPaint;

    //背景图宽高
    private int mWidth;
    private int mHeight;

    //裁剪的宽度
    private int mClipWidth = 0;
    //裁剪的高度
    private int mClipHeight = SizeUtils.dp2px(16);

    //裁剪动画
    private ValueAnimator mAnimator;

    public CropPicView(Context context) {
        this(context, null);
    }

    public CropPicView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropPicView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mPath = new Path();
        mPaint = new Paint();

//        mBitmap = BitmapUtils.decodeSampleBitmapFromResource(getResources(), R.drawable.crop_pic, mWidth, mHeight);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.crop_pic);
        mWidth = mBitmap.getWidth();
        mHeight = mBitmap.getHeight();

        mAnimator = ValueAnimator.ofInt(0, mWidth);
        mAnimator.setDuration(3000);
        mAnimator.setEvaluator(new TypeEvaluator<Integer>() {
            @Override
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                int start = startValue;

                return (int) (start + fraction * (endValue - start));
            }
        });
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mClipWidth = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPath.reset();

        int i = 0;
        //如果裁剪的高度不高于当前高度
        while (i * mClipHeight <= mHeight) {
            if (i % 2 == 0) {
                //从左往右裁剪
                mPath.addRect(new RectF(0, i * mClipHeight, mClipWidth,
                        (i + 1) * mClipHeight), Path.Direction.CCW);
            } else {
                //从右往左裁剪
                mPath.addRect(new RectF(mWidth - mClipWidth, i * mClipHeight, mWidth,
                        (i + 1) * mClipHeight), Path.Direction.CCW);
            }

            i++;
        }

        //先裁剪，裁剪后绘制的图片只显示在裁剪区域内
        canvas.clipPath(mPath);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mAnimator.cancel();
        mBitmap.recycle();
    }
}
