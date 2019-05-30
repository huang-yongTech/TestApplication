package com.hy.library.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.hy.library.R;
import com.hy.library.util.SizeUtils;

/**
 * Created by huangyong on 2019/5/24
 * 仿小米运动首页自定义view
 */
public class MiSportsView extends View {
    //背景paint
    private Paint mBgPaint;
    //起始进度圆环paint
    private Paint mProgressCirclePaint;
    //外部大圈paint
    private Paint mOuterCirclePaint;
    //内部小圈paint
    private Paint mInnerCirclePaint;
    //文字绘制paint
    private Paint mTextPaint;
    //装饰圆弧paint
    private Paint mOvalDecoPaint;
    //背景图片
    private Bitmap mBgBitmap;
    private Bitmap mWatchBitmap;

    private int mCenterX;
    private int mCenterY;
    //外部大圈半径
    private float mOuterRadius;
    //内部小圈半径
    private float mInnerRadius;

    //步数文字矩形框
    private Rect mStepTextRect;
    //对步数描述的文字矩形框
    private Rect mInfoTextRect;

    //动画集合
    private AnimatorSet mAnimatorSet;
    //动画状态
    private AnimationState mAnimationState = AnimationState.LOADING;

    public MiSportsView(Context context) {
        this(context, null);
    }

    public MiSportsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiSportsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
        initAnim();
    }

    private void init() {
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mProgressCirclePaint = new Paint();
        mProgressCirclePaint.setAntiAlias(true);
        mProgressCirclePaint.setStyle(Paint.Style.STROKE);
        mProgressCirclePaint.setStrokeWidth(SizeUtils.dp2px(1));

        mOuterCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterCirclePaint.setStyle(Paint.Style.STROKE);
        mOuterCirclePaint.setColor(Color.WHITE);
        mOuterCirclePaint.setStrokeWidth(SizeUtils.dp2px(12));

        mInnerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerCirclePaint.setColor(Color.WHITE);
        mInnerCirclePaint.setStrokeWidth(SizeUtils.dp2px(2));

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);

        mOvalDecoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOvalDecoPaint.setStyle(Paint.Style.STROKE);
        mOvalDecoPaint.setAlpha(ovalDecoAlpha);
        mOvalDecoPaint.setStrokeWidth(SizeUtils.dp2px(12));

        mStepTextRect = new Rect();
        mInfoTextRect = new Rect();

        mBgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mi_sports_bg);
        mWatchBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mi_sports_watch);
    }

    private void initAnim() {
        //连接时外部大圈动画
        ObjectAnimator progressAnimator = ObjectAnimator.ofFloat(this, "progressRotateDegree", 0, 540);
        progressAnimator.setDuration(4000);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progressRotateDegree = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        progressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                outerCircleAlpha = 160;
                ovalDecoAlpha = 120;

                mAnimationState = AnimationState.UP1;
            }
        });

        PropertyValuesHolder scaleHolder = PropertyValuesHolder.ofFloat("outerCircleScale", 1f, 1.3f, 1f);
        PropertyValuesHolder translationHolder = PropertyValuesHolder.ofInt("canvasTranslateY1",
                0, -SizeUtils.dp2px(40), 0);
        ObjectAnimator upDownAnimator = ObjectAnimator.ofPropertyValuesHolder(this, scaleHolder, translationHolder);
        upDownAnimator.setDuration(1000);
        upDownAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                innerCircleAlpha = 255;

                mAnimationState = AnimationState.STOP;
            }
        });

        ObjectAnimator upAnimator = ObjectAnimator.ofInt(this, "canvasTranslateY2", 0, -SizeUtils.dp2px(40));
        upAnimator.setDuration(500);
        upAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationState = AnimationState.FINISH;
            }
        });

        ObjectAnimator ovalDecoAnimator = ObjectAnimator.ofFloat(this, "ovalDecoDegree", 0, 360);
        ovalDecoAnimator.setRepeatCount(ValueAnimator.INFINITE);
        ovalDecoAnimator.setRepeatMode(ValueAnimator.RESTART);
        ovalDecoAnimator.setDuration(15000);
        ovalDecoAnimator.setInterpolator(new LinearInterpolator());

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playSequentially(progressAnimator, upDownAnimator, upAnimator, ovalDecoAnimator);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMeasureSize(widthMeasureSpec, mBgBitmap.getWidth() + getPaddingStart() + getPaddingEnd(),
                ViewCompat.getMinimumWidth(this));
        int height = getMeasureSize(heightMeasureSpec, mBgBitmap.getHeight() - SizeUtils.dp2px(40) + getPaddingStart() + getPaddingEnd(),
                ViewCompat.getMinimumHeight(this));

        mCenterX = width / 2;
        mCenterY = height / 2;
        mOuterRadius = Math.min(width, height) * 2.0f / 6;
        mInnerRadius = mOuterRadius * 4 / 5;

        setMeasuredDimension(width, height);
    }

    private int getMeasureSize(int spec, int desireSize, int min) {
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

        canvas.save();
        canvas.translate(0, canvasTranslateY1);
        canvas.translate(0, canvasTranslateY2);

        canvas.drawBitmap(mBgBitmap, 0, 0, mBgPaint);

        switch (mAnimationState) {
            case LOADING:
                drawProgressCircle(canvas);
                drawInnerText(canvas);
                break;
            case UP1:
            case DOWN:
                drawInnerText(canvas);
                drawOuterCircle(canvas);
                drawArcDecorations(canvas);
                break;
            case STOP:
            case UP2:
                drawInnerText(canvas);
                drawOuterCircle(canvas);
                drawArcDecorations(canvas);
                drawInnerCircle(canvas);
                break;
            case FINISH:
                drawInnerText(canvas);
                drawOuterCircle(canvas);
                drawArcDecorations(canvas);
                drawInnerCircle(canvas);
                break;
        }

        canvas.restore();
    }

    /**
     * 绘制圈内的文字
     */
    private void drawInnerText(Canvas canvas) {
        canvas.save();
        //绘制文字
        String steps = "2274";
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 50, getResources().getDisplayMetrics()));
        mTextPaint.getTextBounds(steps, 0, steps.length(), mStepTextRect);
        canvas.drawText(steps, mCenterX - (mStepTextRect.left + mStepTextRect.right) / 2.0f,
                mCenterY - (mStepTextRect.top + mStepTextRect.bottom) / 2.0f, mTextPaint);

        String distance = "1.5公里";
        String calorie = "34千卡";

        float infoTextY = mCenterY - (mStepTextRect.top + mStepTextRect.bottom) / 2.0f + SizeUtils.dp2px(10);
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));

        mTextPaint.getTextBounds(distance, 0, distance.length(), mInfoTextRect);
        canvas.drawText(distance, mCenterX - (mInfoTextRect.left + mInfoTextRect.right) - SizeUtils.dp2px(8),
                infoTextY - (mInfoTextRect.top + mInfoTextRect.bottom), mTextPaint);

        mTextPaint.getTextBounds(calorie, 0, calorie.length(), mInfoTextRect);
        canvas.drawText(calorie, mCenterX + SizeUtils.dp2px(8),
                infoTextY - (mInfoTextRect.top + mInfoTextRect.bottom), mTextPaint);

        //绘制竖线
        mTextPaint.setStrokeWidth(SizeUtils.dp2px(1));
        canvas.drawLine(mCenterX, infoTextY, mCenterX, infoTextY - (mInfoTextRect.top + mInfoTextRect.bottom), mTextPaint);

        canvas.drawBitmap(mWatchBitmap, mCenterX - mWatchBitmap.getWidth() / 2.0f,
                infoTextY - (mInfoTextRect.top + mInfoTextRect.bottom) + SizeUtils.dp2px(10), mTextPaint);
    }

    /**
     * 绘制连接时候外面的圆环动画
     */
    private void drawProgressCircle(Canvas canvas) {
        mProgressCirclePaint.setAlpha(progressCircleAlpha);
        SweepGradient sweepGradient = new SweepGradient(mCenterX, mCenterY, Color.TRANSPARENT, Color.WHITE);
        mProgressCirclePaint.setShader(sweepGradient);
        int space = SizeUtils.dp2px(1.2f);

        canvas.save();
        canvas.rotate(180 + progressRotateDegree + 14, mCenterX, mCenterY);
        canvas.drawCircle(mCenterX - space * 3, mCenterY + space * 2, mOuterRadius, mProgressCirclePaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(180 + progressRotateDegree + 12, mCenterX, mCenterY);
        canvas.drawCircle(mCenterX - space * 2, mCenterY - space, mOuterRadius, mProgressCirclePaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(180 + progressRotateDegree + 10, mCenterX, mCenterY);
        canvas.drawCircle(mCenterX - space, mCenterY - space * 3, mOuterRadius, mProgressCirclePaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(180 + progressRotateDegree + 8, mCenterX, mCenterY);
        canvas.drawCircle(mCenterX, mCenterY + space * 2, mOuterRadius, mProgressCirclePaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(180 + progressRotateDegree + 6, mCenterX, mCenterY);
        canvas.drawCircle(mCenterX + space, mCenterY + space, mOuterRadius, mProgressCirclePaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(180 + progressRotateDegree + 4, mCenterX, mCenterY);
        canvas.drawCircle(mCenterX + space * 2, mCenterY + space * 3, mOuterRadius, mProgressCirclePaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(180 + progressRotateDegree + 2, mCenterX, mCenterY);
        canvas.drawCircle(mCenterX + space * 3, mCenterY + space * 2, mOuterRadius, mProgressCirclePaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(180 + progressRotateDegree, mCenterX, mCenterY);
        canvas.drawCircle(mCenterX * 4, mCenterY - space, mOuterRadius, mProgressCirclePaint);
        canvas.restore();
    }

    /**
     * 绘制大圈
     */
    private void drawOuterCircle(Canvas canvas) {
        mOuterCirclePaint.setAlpha(outerCircleAlpha);

        canvas.save();
        canvas.scale(outerCircleScale, outerCircleScale, mCenterX, mCenterY);
        canvas.drawCircle(mCenterX, mCenterY, mOuterRadius, mOuterCirclePaint);
        canvas.restore();
    }

    /**
     * 绘制内圈
     */
    private void drawInnerCircle(Canvas canvas) {
        mInnerCirclePaint.setAlpha(innerCircleAlpha);
        mInnerCirclePaint.setStyle(Paint.Style.STROKE);

        canvas.drawArc(mCenterX - mInnerRadius, mCenterY - mInnerRadius, mCenterX + mInnerRadius, mCenterY + mInnerRadius,
                -90, 270, false, mInnerCirclePaint);
        mInnerCirclePaint.setPathEffect(new DashPathEffect(new float[]{2, 4}, 4));
        canvas.drawArc(mCenterX - mInnerRadius, mCenterY - mInnerRadius, mCenterX + mInnerRadius, mCenterY + mInnerRadius,
                180, 90, false, mInnerCirclePaint);
        mInnerCirclePaint.setStyle(Paint.Style.FILL);
        mInnerCirclePaint.setPathEffect(null);
        canvas.drawCircle(mCenterX - mInnerRadius, mCenterY, SizeUtils.dp2px(4), mInnerCirclePaint);
    }

    /**
     * 绘制圆弧装饰
     */
    private void drawArcDecorations(Canvas canvas) {
        SweepGradient sweepGradient = new SweepGradient(mCenterX, mCenterY, new int[]{Color.TRANSPARENT, Color.parseColor("#77FFFFFF"), Color.TRANSPARENT},
                new float[]{0, 0.25f, 0.5f});
        mOvalDecoPaint.setShader(sweepGradient);
        mOvalDecoPaint.setMaskFilter(new BlurMaskFilter(mOuterRadius + SizeUtils.dp2px(4), BlurMaskFilter.Blur.NORMAL));

        canvas.save();
        canvas.rotate(ovalDecoDegree, mCenterX, mCenterY);
        canvas.scale(outerCircleScale, outerCircleScale, mCenterX, mCenterY);
        if (ovalDecoAlpha > 0) {
            mOvalDecoPaint.setAlpha(200);
        }
        canvas.drawArc(mCenterX - mOuterRadius, mCenterY - mOuterRadius,
                mCenterX + mOuterRadius, mCenterY + mOuterRadius,
                0, 180, false, mOvalDecoPaint);
        if (ovalDecoAlpha > 0) {
            mOvalDecoPaint.setAlpha(120);
        }
        canvas.drawArc(mCenterX - mOuterRadius, mCenterY - mOuterRadius - SizeUtils.dp2px(4),
                mCenterX + mOuterRadius, mCenterY + mOuterRadius + SizeUtils.dp2px(4),
                0, 180, false, mOvalDecoPaint);
        if (ovalDecoAlpha > 0) {
            mOvalDecoPaint.setAlpha(80);
        }
        canvas.drawArc(mCenterX - mOuterRadius, mCenterY - mOuterRadius - SizeUtils.dp2px(8),
                mCenterX + mOuterRadius, mCenterY + mOuterRadius + SizeUtils.dp2px(8),
                0, 180, false, mOvalDecoPaint);
        if (ovalDecoAlpha > 0) {
            mOvalDecoPaint.setAlpha(40);
        }
        canvas.drawArc(mCenterX - mOuterRadius, mCenterY - mOuterRadius - SizeUtils.dp2px(12),
                mCenterX + mOuterRadius, mCenterY + mOuterRadius + SizeUtils.dp2px(12),
                0, 180, false, mOvalDecoPaint);
        canvas.restore();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mAnimatorSet != null) {
            mAnimatorSet.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
        }

        //回收bitmap资源
        if (mBgBitmap != null) {
            mBgBitmap.recycle();
        }

        if (mWatchBitmap != null) {
            mWatchBitmap.recycle();
        }
    }

    //起始圆环透明度
    private int progressCircleAlpha = 255;
    //连接动画中外部大圈的旋转度数
    private float progressRotateDegree;
    //外圈的透明度
    private int outerCircleAlpha;
    //外圈的缩放
    private float outerCircleScale = 1;
    //画布Y轴平移1
    private int canvasTranslateY1;
    //画布Y轴平移2
    private int canvasTranslateY2;
    //内圈的透明度
    private int innerCircleAlpha;
    //椭圆装饰沿大圈旋转度数
    private float ovalDecoDegree;
    //椭圆透明度
    private int ovalDecoAlpha;

    public int getProgressCircleAlpha() {
        return progressCircleAlpha;
    }

    public void setProgressCircleAlpha(int progressCircleAlpha) {
        this.progressCircleAlpha = progressCircleAlpha;
    }

    public float getProgressRotateDegree() {
        return progressRotateDegree;
    }

    public void setProgressRotateDegree(float progressRotateDegree) {
        this.progressRotateDegree = progressRotateDegree;
    }

    public int getOuterCircleAlpha() {
        return outerCircleAlpha;
    }

    public void setOuterCircleAlpha(int outerCircleAlpha) {
        this.outerCircleAlpha = outerCircleAlpha;
    }

    public int getInnerCircleAlpha() {
        return innerCircleAlpha;
    }

    public void setInnerCircleAlpha(int innerCircleAlpha) {
        this.innerCircleAlpha = innerCircleAlpha;
    }

    public float getOuterCircleScale() {
        return outerCircleScale;
    }

    public void setOuterCircleScale(float outerCircleScale) {
        this.outerCircleScale = outerCircleScale;
        invalidate();
    }

    public int getCanvasTranslateY1() {
        return canvasTranslateY1;
    }

    public void setCanvasTranslateY1(int canvasTranslateY1) {
        this.canvasTranslateY1 = canvasTranslateY1;
        invalidate();
    }

    public int getCanvasTranslateY2() {
        return canvasTranslateY2;
    }

    public void setCanvasTranslateY2(int canvasTranslateY2) {
        this.canvasTranslateY2 = canvasTranslateY2;
        invalidate();
    }

    public float getOvalDecoDegree() {
        return ovalDecoDegree;
    }

    public void setOvalDecoDegree(float ovalDecoDegree) {
        this.ovalDecoDegree = ovalDecoDegree;
        invalidate();
    }

    public int getOvalDecoAlpha() {
        return ovalDecoAlpha;
    }

    public void setOvalDecoAlpha(int ovalDecoAlpha) {
        this.ovalDecoAlpha = ovalDecoAlpha;
    }

    private enum AnimationState {
        LOADING,
        UP1,
        DOWN,
        STOP,
        UP2,
        FINISH
    }
}