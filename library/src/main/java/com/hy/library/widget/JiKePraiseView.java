package com.hy.library.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.hy.library.R;
import com.hy.library.util.SizeUtils;

/**
 * Created by huangyong on 2019/4/24
 * 仿即刻点赞效果的view
 */
public class JiKePraiseView extends View {
    private static final String TAG = "JiKePraiseView";

    private static final long DURATION = 500;

    //view是否被选中标记位
    private boolean mIsLike;
    //是否第一次进入界面
    private boolean mIsFirst = true;

    //bitmap图片
    private Bitmap mThumbLikeBitmap, mThumbUnLikeBitmap, mDecoBitmap, mHandBitmap;
    private Paint mHandBitmapPaint, mDecoBitmapPaint, mTextPaint, mOldTextPaint;

    //点赞的数字
    private int mPraiseNumber;
    //数字的显示区域
    private Rect mTextBounds;

    //数字移动的最大距离
    private int mTextMaxMove;
    private float[] mWidths;

    private AnimatorSet mLikeAnimatorSet, mUnLikeAnimatorSet;

    public JiKePraiseView(Context context) {
        this(context, null);
    }

    public JiKePraiseView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JiKePraiseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.JiKePraiseView);
        mPraiseNumber = array.getInteger(R.styleable.JiKePraiseView_praise_number, 0);
        mIsLike = array.getBoolean(R.styleable.JiKePraiseView_is_like, false);
        array.recycle();

        init();
    }

    private void init() {
        mHandBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDecoBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
        mTextPaint.setColor(Color.GRAY);
        mOldTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOldTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
        mOldTextPaint.setColor(Color.GRAY);

        mTextBounds = new Rect();
        mWidths = new float[8];
        mTextMaxMove = SizeUtils.dp2px(18);

        initAnim();
    }

    private void initAnim() {
        //点暂时图标动画
        PropertyValuesHolder handLikeScaleHolder = PropertyValuesHolder.ofFloat("handScale", 1f, 0.8f, 1f);
        PropertyValuesHolder decoLikeScaleHolder = PropertyValuesHolder.ofFloat("decoScale", 0f, 1f);
        PropertyValuesHolder decoLikeAlphaHolder = PropertyValuesHolder.ofFloat("decoAlpha", 0f, 1f);
        ObjectAnimator likeBitmapAnimator = ObjectAnimator.ofPropertyValuesHolder(this, handLikeScaleHolder, decoLikeScaleHolder, decoLikeAlphaHolder);

        //取消点赞时图标动画
        PropertyValuesHolder handUnLikeScaleHolder = PropertyValuesHolder.ofFloat("handScale", 1f, 0.8f, 1f);
        PropertyValuesHolder decoUnLikeAlphaHolder = PropertyValuesHolder.ofFloat("decoAlpha", 1f, 0f);
        ObjectAnimator unLikeBitmapAnimator = ObjectAnimator.ofPropertyValuesHolder(this, handUnLikeScaleHolder, decoUnLikeAlphaHolder);

        PropertyValuesHolder textLikeTransHolder = PropertyValuesHolder.ofFloat("textTranslate", mTextMaxMove, 0);
        PropertyValuesHolder textAlphaHolder = PropertyValuesHolder.ofFloat("textAlpha", 0f, 1f);
        ObjectAnimator textLikeAnimator = ObjectAnimator.ofPropertyValuesHolder(this, textLikeTransHolder, textAlphaHolder);

        PropertyValuesHolder textUnLikeTransHolder = PropertyValuesHolder.ofFloat("textTranslate", -mTextMaxMove, 0);
        ObjectAnimator textUnLikeAnimator = ObjectAnimator.ofPropertyValuesHolder(this, textUnLikeTransHolder, textAlphaHolder);

        mLikeAnimatorSet = new AnimatorSet();
        mLikeAnimatorSet.playTogether(likeBitmapAnimator, textLikeAnimator);
        mLikeAnimatorSet.setDuration(DURATION);
        mUnLikeAnimatorSet = new AnimatorSet();
        mUnLikeAnimatorSet.playTogether(unLikeBitmapAnimator, textUnLikeAnimator);
        mUnLikeAnimatorSet.setDuration(DURATION);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mThumbLikeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thumbs_on);
        mThumbUnLikeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thumbs_off);
        mDecoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thumbs_decoration);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        //图片资源回收
        if (mThumbLikeBitmap != null) {
            mThumbLikeBitmap.recycle();
        }

        if (mThumbUnLikeBitmap != null) {
            mThumbUnLikeBitmap.recycle();
        }

        if (mDecoBitmap != null) {
            mDecoBitmap.recycle();
        }

        if (mHandBitmap != null) {
            mHandBitmap.recycle();
        }

        //退出view时，结束动画
        if (mLikeAnimatorSet != null && mLikeAnimatorSet.isRunning()) {
            mLikeAnimatorSet.cancel();
        }

        if (mUnLikeAnimatorSet != null && mUnLikeAnimatorSet.isRunning()) {
            mUnLikeAnimatorSet.cancel();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        String textNum = String.valueOf(mPraiseNumber);
        float textLength = mTextPaint.measureText(textNum, 0, textNum.length());

        int contentWidth = (int) (mThumbLikeBitmap.getWidth() + textLength + SizeUtils.dp2px(8));
        int contentHeight = mThumbLikeBitmap.getHeight() + mDecoBitmap.getHeight();

        //view的宽高
        int width = getMeasureSize(widthMeasureSpec, contentWidth + getPaddingStart() + getPaddingEnd(),
                ViewCompat.getMinimumWidth(this));
        int height = getMeasureSize(heightMeasureSpec, contentHeight + getPaddingTop() + getPaddingBottom(),
                ViewCompat.getMinimumHeight(this));

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

        mHandBitmap = mIsLike ? mThumbLikeBitmap : mThumbUnLikeBitmap;

        drawHandIcon(canvas, mHandBitmap);
        drawText(canvas, mHandBitmap);
        drawDeco(canvas, mHandBitmap);
    }

    /**
     * 绘制小手图标
     */
    private void drawHandIcon(Canvas canvas, Bitmap handBitmap) {
        int handX = SizeUtils.dp2px(10);
        int handY = (getHeight() - handBitmap.getHeight()) / 2;

        canvas.save();
        canvas.scale(handScale, handScale, handX + handBitmap.getWidth() / 2.0f, getHeight() / 2.0f);
        canvas.drawBitmap(handBitmap, handX, handY, mHandBitmapPaint);
        canvas.restore();
    }

    /**
     * 绘制点赞数字
     */
    private void drawText(Canvas canvas, Bitmap handBitmap) {
        String textValue = String.valueOf(mPraiseNumber);
        String textOldValue;

        if (mIsLike) {
            //点赞（原来的数字是当前数字减一）
            if (mIsFirst) {
                textOldValue = String.valueOf(mPraiseNumber);
//                mOldTextPaint.setColor(Color.RED);
                //如果初始化为选中状态，则设置文字颜色为红色
                mTextPaint.setColor(Color.RED);
                mIsFirst = !mIsFirst;
            } else {
                textOldValue = String.valueOf(mPraiseNumber - 1);
            }
        } else {
            //取消点赞（原来的数字是当前数字加一）
            if (mIsFirst) {
                textOldValue = String.valueOf(mPraiseNumber);
                mIsFirst = !mIsFirst;
            } else {
                textOldValue = String.valueOf(mPraiseNumber + 1);
            }
        }

        int textValueLength = textValue.length();
        mTextPaint.getTextBounds(textValue, 0, textValueLength, mTextBounds);
        float textX = SizeUtils.dp2px(10) + handBitmap.getWidth() + SizeUtils.dp2px(4);
        float textY = getHeight() / 2.0f - (mTextBounds.bottom + mTextBounds.top) / 2.0f;

        //处理前后数字长度不等的情况
        if (textValueLength != textOldValue.length()) {
            if (mIsLike) {
                //点赞
                mOldTextPaint.setAlpha((int) (255 * (1 - textAlpha)));
                canvas.drawText(textOldValue, textX, textY - mTextMaxMove + textTranslate, mOldTextPaint);

                mTextPaint.setAlpha((int) (255 * textAlpha));
                mTextPaint.setColor(Color.RED);
                canvas.drawText(textValue, textX, textY + textTranslate, mTextPaint);
            } else {
                //取消点赞
                mOldTextPaint.setAlpha((int) (255 * (1 - textAlpha)));
                canvas.drawText(textOldValue, textX, textY + mTextMaxMove + textTranslate, mOldTextPaint);

                mTextPaint.setAlpha((int) (255 * textAlpha));
                mTextPaint.setColor(Color.GRAY);
                canvas.drawText(textValue, textX, textY + textTranslate, mTextPaint);
            }

            return;
        }

        //处理前后数字长度相等的情况
        mTextPaint.getTextWidths(textValue, mWidths);

        char[] textChar = textValue.toCharArray();
        char[] textOldChar = textOldValue.toCharArray();

        for (int i = 0; i < textChar.length; i++) {
            if (textChar[i] == textOldChar[i]) {
                mTextPaint.setAlpha(255);
                canvas.drawText(String.valueOf(textChar[i]), textX, textY, mTextPaint);
            } else {
                if (mIsLike) {
                    //点赞
                    mOldTextPaint.setAlpha((int) (255 * (1 - textAlpha)));
                    canvas.drawText(String.valueOf(textOldChar[i]), textX, textY - mTextMaxMove + textTranslate, mOldTextPaint);

                    mTextPaint.setAlpha((int) (255 * textAlpha));
                    mTextPaint.setColor(Color.RED);
                    canvas.drawText(String.valueOf(textChar[i]), textX, textY + textTranslate, mTextPaint);
                } else {
                    //取消点赞
                    mOldTextPaint.setAlpha((int) (255 * (1 - textAlpha)));
                    canvas.drawText(String.valueOf(textOldChar[i]), textX, textY + mTextMaxMove + textTranslate, mOldTextPaint);

                    mTextPaint.setAlpha((int) (255 * textAlpha));
                    mTextPaint.setColor(Color.GRAY);
                    canvas.drawText(String.valueOf(textChar[i]), textX, textY + textTranslate, mTextPaint);
                }
            }

            textX += mWidths[i];
        }
    }

    /**
     * 绘制小手上方的装饰
     */
    private void drawDeco(Canvas canvas, Bitmap handBitmap) {
        int decoX = SizeUtils.dp2px(12);
        int decoY = (getHeight() - handBitmap.getHeight()) / 2 - mDecoBitmap.getHeight() + SizeUtils.dp2px(6);

        mDecoBitmapPaint.setAlpha((int) (255 * decoAlpha));

        canvas.save();
        canvas.scale(decoScale, decoScale, decoX + handBitmap.getWidth() / 2.0f, decoY);
        canvas.drawBitmap(mDecoBitmap, decoX, decoY, mDecoBitmapPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onPressAction();
        }

        return super.onTouchEvent(event);
    }

    //点赞事件处理
    private void onPressAction() {
        if (mLikeAnimatorSet == null || mUnLikeAnimatorSet == null) {
            return;
        }

        if (mLikeAnimatorSet.isRunning()
                || mUnLikeAnimatorSet.isRunning()) {
            return;
        }

        mIsLike = !mIsLike;

        if (mIsLike) {
            //点赞
            ++mPraiseNumber;
            mLikeAnimatorSet.start();
        } else {
            //取消点赞
            --mPraiseNumber;
            mUnLikeAnimatorSet.start();
        }
    }

    public boolean isIsLike() {
        return mIsLike;
    }

    public void setIsLike(boolean isLike) {
        this.mIsLike = isLike;
        decoAlpha = 1;
        decoScale = 1;
        invalidate();
    }

    public int getPraiseNumber() {
        return mPraiseNumber;
    }

    public void setPraiseNumber(int praiseNumber) {
        this.mPraiseNumber = praiseNumber;
    }

    //文字平移
    private float textTranslate;
    //文字透明度（默认不透明）
    private float textAlpha = 1;
    //手指缩放（默认不缩放）
    private float handScale = 1;
    //手指上方装饰缩放
    private float decoScale;
    //手指上方装饰透明度
    private float decoAlpha;

    public float getTextTranslate() {
        return textTranslate;
    }

    public void setTextTranslate(float textTranslate) {
        this.textTranslate = textTranslate;
        invalidate();
    }

    public float getTextAlpha() {
        return textAlpha;
    }

    public void setTextAlpha(float textAlpha) {
        this.textAlpha = textAlpha;
        invalidate();
    }

    public float getHandScale() {
        return handScale;
    }

    public void setHandScale(float handScale) {
        this.handScale = handScale;
        invalidate();
    }

    public float getDecoScale() {
        return decoScale;
    }

    public void setDecoScale(float decoScale) {
        this.decoScale = decoScale;
        invalidate();
    }

    public float getDecoAlpha() {
        return decoAlpha;
    }

    public void setDecoAlpha(float decoAlpha) {
        this.decoAlpha = decoAlpha;
        invalidate();
    }
}
