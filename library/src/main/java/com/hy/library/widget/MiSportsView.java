package com.hy.library.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
    //外部大圈paint
    private Paint mOuterCirclePaint;
    //内部小圈paint
    private Paint mInnerCirclePaint;
    //文字绘制paint
    private Paint mTextPaint;
    //装饰圆弧paint
    private Paint mArcDecoPaint;
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
    //水平方向文字间隔
    private int mTextGapX;
    //竖直方向文字间隔
    private int mTextGapY;
    //大文字size
    private int mLargeTextSize;
    //小文字size
    private int mSmallTextSize;

    //初始圆弧透明度
    private int mArcOriginAlpha;
    //圆弧递增透明度
    private int mArcDAlpha;
    //初始圆弧半径大小
    private int mArcOriginWidth = SizeUtils.dp2px(14);
    //圆弧递增半径大小
    private int mArcDWidth;

    //画布在Y轴上的平移距离
    private int mCanvasTranslateY = SizeUtils.dp2px(40);

    private FireworksCircleGraphics mCircleGraphics;

    //动画持续时间
    private final long DURATION = 1000;
    //起始圆环进度动画
    private ObjectAnimator mProgressAnimator;
    //画布上移下移动画
    private ObjectAnimator mUpDownAnimator;
    //画布再次上移动画
    private ObjectAnimator mUpAnimator;
    //圆弧装饰动画
    private ObjectAnimator mArcDecoAnimator;
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

        mCircleGraphics = new FireworksCircleGraphics();

        init();
        initAnim();
    }

    private void init() {
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mOuterCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterCirclePaint.setStyle(Paint.Style.STROKE);
        mOuterCirclePaint.setColor(Color.WHITE);
        mOuterCirclePaint.setAlpha(160);
        mOuterCirclePaint.setStrokeWidth(mArcOriginWidth);

        mInnerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerCirclePaint.setColor(Color.WHITE);
        mInnerCirclePaint.setStrokeWidth(SizeUtils.dp2px(2));

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);

        mArcDecoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcDecoPaint.setStyle(Paint.Style.STROKE);
        mArcOriginAlpha = 200;
        mArcDAlpha = -50;
        mArcDWidth = SizeUtils.dp2px(3);

        mStepTextRect = new Rect();
        mInfoTextRect = new Rect();
        mTextGapX = SizeUtils.dp2px(8);
        mTextGapY = SizeUtils.dp2px(10);
        mLargeTextSize = 50;
        mSmallTextSize = 14;

        mBgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mi_sports_bg);
        mWatchBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mi_sports_watch);
    }

    private void initAnim() {
        //连接时动画
        mProgressAnimator = ObjectAnimator.ofFloat(this, "progressRotateDegree", 0, 540);
        mProgressAnimator.setDuration(DURATION * 3);
        mProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progressRotateDegree = (float) animation.getAnimatedValue();
                mCircleGraphics.setRotateDegree(progressRotateDegree);
                mCircleGraphics.next();
                invalidate();
            }
        });
        mProgressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationState = AnimationState.UP1;

                if (mUpDownAnimator != null) {
                    mUpDownAnimator.start();
                }
            }
        });

        //连接完成后画布缩放与上移下移动画
        PropertyValuesHolder scaleHolder = PropertyValuesHolder.ofFloat("outerCircleScale", 1f, 1.3f, 1f);
        PropertyValuesHolder translationHolder = PropertyValuesHolder.ofInt("canvasTranslateY1",
                0, -mCanvasTranslateY, 0);
        mUpDownAnimator = ObjectAnimator.ofPropertyValuesHolder(this, scaleHolder, translationHolder);
        mUpDownAnimator.setDuration(DURATION);
        mUpDownAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationState = AnimationState.STOP;

                if (mUpAnimator != null) {
                    mUpAnimator.start();
                }
            }
        });

        //画布再次上移动画
        mUpAnimator = ObjectAnimator.ofInt(this, "canvasTranslateY2", 0, -mCanvasTranslateY);
        mUpAnimator.setDuration(DURATION / 2);
        mUpAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationState = AnimationState.FINISH;

                if (mArcDecoAnimator != null) {
                    mArcDecoAnimator.start();
                }
            }
        });

        //外部圆弧装饰旋转动画
        mArcDecoAnimator = ObjectAnimator.ofFloat(this, "arcDecoDegree", 0, 360);
        mArcDecoAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mArcDecoAnimator.setRepeatMode(ValueAnimator.RESTART);
        mArcDecoAnimator.setDuration(DURATION * 10);
        mArcDecoAnimator.setInterpolator(new LinearInterpolator());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMeasureSize(widthMeasureSpec, mBgBitmap.getWidth() + getPaddingStart() + getPaddingEnd(),
                ViewCompat.getMinimumWidth(this));
        int height = getMeasureSize(heightMeasureSpec, mBgBitmap.getHeight() - SizeUtils.dp2px(40) + getPaddingStart() + getPaddingEnd(),
                ViewCompat.getMinimumHeight(this));

        mCenterX = width / 2;
        mCenterY = height / 2;
        //外部大圈半径为画布宽度的 1/3
        mOuterRadius = Math.min(width, height) * 2.0f / 6;
        //内圈半径为外圈半径的 0.8
        mInnerRadius = mOuterRadius * 0.8f;

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
                mCircleGraphics.draw(canvas);
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
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mLargeTextSize, getResources().getDisplayMetrics()));
        mTextPaint.getTextBounds(steps, 0, steps.length(), mStepTextRect);
        canvas.drawText(steps, mCenterX - (mStepTextRect.left + mStepTextRect.right) / 2.0f,
                mCenterY - (mStepTextRect.top + mStepTextRect.bottom) / 2.0f, mTextPaint);

        String distance = "1.5公里";
        String calorie = "34千卡";

        float infoTextY = mCenterY - (mStepTextRect.top + mStepTextRect.bottom) / 2.0f + mTextGapY;
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mSmallTextSize, getResources().getDisplayMetrics()));

        mTextPaint.getTextBounds(distance, 0, distance.length(), mInfoTextRect);
        canvas.drawText(distance, mCenterX - (mInfoTextRect.left + mInfoTextRect.right) - mTextGapX,
                infoTextY - (mInfoTextRect.top + mInfoTextRect.bottom), mTextPaint);

        mTextPaint.getTextBounds(calorie, 0, calorie.length(), mInfoTextRect);
        canvas.drawText(calorie, mCenterX + mTextGapX,
                infoTextY - (mInfoTextRect.top + mInfoTextRect.bottom), mTextPaint);

        //绘制竖线
        mTextPaint.setStrokeWidth(SizeUtils.dp2px(1));
        canvas.drawLine(mCenterX, infoTextY, mCenterX, infoTextY - (mInfoTextRect.top + mInfoTextRect.bottom), mTextPaint);

        canvas.drawBitmap(mWatchBitmap, mCenterX - mWatchBitmap.getWidth() / 2.0f,
                infoTextY - (mInfoTextRect.top + mInfoTextRect.bottom) + mTextGapY, mTextPaint);
    }

    /**
     * 绘制大圈
     */
    private void drawOuterCircle(Canvas canvas) {
        canvas.save();
        canvas.scale(outerCircleScale, outerCircleScale, mCenterX, mCenterY);
        canvas.drawCircle(mCenterX, mCenterY, mOuterRadius, mOuterCirclePaint);
        canvas.restore();
    }

    /**
     * 绘制内圈
     */
    private void drawInnerCircle(Canvas canvas) {
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
        SweepGradient sweepGradient = new SweepGradient(mCenterX, mCenterY,
                new int[]{Color.TRANSPARENT, Color.parseColor("#77FFFFFF"), Color.TRANSPARENT},
                new float[]{0, 0.25f, 0.5f});
        mArcDecoPaint.setShader(sweepGradient);
        mArcDecoPaint.setMaskFilter(new BlurMaskFilter(mOuterRadius + SizeUtils.dp2px(4), BlurMaskFilter.Blur.NORMAL));

        canvas.save();

        canvas.rotate(arcDecoDegree, mCenterX, mCenterY);
        canvas.scale(outerCircleScale, outerCircleScale, mCenterX, mCenterY);

        mArcDecoPaint.setAlpha(mArcOriginAlpha);
        mArcDecoPaint.setStrokeWidth(mArcOriginWidth);
        canvas.drawArc(mCenterX - mOuterRadius, mCenterY - mOuterRadius,
                mCenterX + mOuterRadius, mCenterY + mOuterRadius,
                0, 180, false, mArcDecoPaint);

        mArcDecoPaint.setAlpha(mArcOriginAlpha + mArcDAlpha);
        mArcDecoPaint.setStrokeWidth(mArcOriginWidth + mArcDWidth);
        canvas.drawArc(mCenterX - mOuterRadius, mCenterY - mOuterRadius - mArcDWidth,
                mCenterX + mOuterRadius, mCenterY + mOuterRadius + mArcDWidth,
                0, 180, false, mArcDecoPaint);

        mArcDecoPaint.setAlpha(mArcOriginAlpha + mArcDAlpha * 2);
        mArcDecoPaint.setStrokeWidth(mArcOriginWidth + mArcDWidth * 2);
        canvas.drawArc(mCenterX - mOuterRadius, mCenterY - mOuterRadius - mArcDWidth * 2,
                mCenterX + mOuterRadius, mCenterY + mOuterRadius + mArcDWidth * 2,
                0, 180, false, mArcDecoPaint);

        mArcDecoPaint.setAlpha(mArcOriginAlpha + mArcDAlpha * 3);
        mArcDecoPaint.setStrokeWidth(mArcOriginWidth + mArcDWidth * 3);
        canvas.drawArc(mCenterX - mOuterRadius, mCenterY - mOuterRadius - mArcDWidth * 3,
                mCenterX + mOuterRadius, mCenterY + mOuterRadius + mArcDWidth * 3,
                0, 180, false, mArcDecoPaint);

        canvas.restore();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mProgressAnimator != null) {
            mProgressAnimator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mProgressAnimator != null && mProgressAnimator.isRunning()) {
            mProgressAnimator.cancel();
        }

        if (mUpDownAnimator != null && mUpDownAnimator.isRunning()) {
            mUpDownAnimator.cancel();
        }

        if (mUpAnimator != null && mUpAnimator.isRunning()) {
            mUpAnimator.cancel();
        }

        if (mArcDecoAnimator != null && mArcDecoAnimator.isRunning()) {
            mArcDecoAnimator.cancel();
        }

        //回收bitmap资源
        if (mBgBitmap != null) {
            mBgBitmap.recycle();
        }

        if (mWatchBitmap != null) {
            mWatchBitmap.recycle();
        }
    }

    //连接动画中外部大圈的旋转度数
    private float progressRotateDegree;
    //外圈的缩放
    private float outerCircleScale = 1;
    //画布Y轴平移1
    private int canvasTranslateY1;
    //画布Y轴平移2
    private int canvasTranslateY2;
    //椭圆装饰沿大圈旋转度数
    private float arcDecoDegree;

    public float getProgressRotateDegree() {
        return progressRotateDegree;
    }

    public void setProgressRotateDegree(float progressRotateDegree) {
        this.progressRotateDegree = progressRotateDegree;
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

    public float getArcDecoDegree() {
        return arcDecoDegree;
    }

    public void setArcDecoDegree(float arcDecoDegree) {
        this.arcDecoDegree = arcDecoDegree;
        invalidate();
    }

    /**
     * 整个动画各个阶段的状态
     */
    private enum AnimationState {
        LOADING,
        UP1,
        DOWN,
        STOP,
        UP2,
        FINISH
    }
}