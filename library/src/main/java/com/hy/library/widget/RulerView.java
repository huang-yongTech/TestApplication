package com.hy.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.hy.library.R;
import com.hy.library.util.ScreenUtils;
import com.hy.library.util.SizeUtils;

/**
 * Created by huangyong on 2019/5/10
 * 仿薄荷健康滑动卷尺效果
 */
public class RulerView extends View {
    private static final String TAG = "RulerView";

    private Paint mCalibrationPaint;

    //卷尺宽度
    private int mWidth;
    //屏幕宽度
    private int mScreenSize;

    //卷尺的当前刻度
    private float mMiddle;
    //卷尺的最小刻度
    private float mMinValue = 0;
    //卷尺的最大刻度
    private float mMaxValue = 100;
    //卷尺当前值
    private float mCurrValue = 0;
    //辅助数值
    private float mAssistValue = 10.0f;
    //卷尺每一格代表的数值
    private int mPerCalibrationValue;
    //卷尺刻度之间的像素距离
    private float mCalibrationGap;
    //总刻度数量
    private int mTotalCalibrationNum;

    //刻度画线的宽度
    private int mCalibrationWidth;
    //大刻度高度
    private int mMaxCalibrationHeight;
    //小刻度高度
    private int mMinCalibrationHeight;
    //中刻度高度
    private int mMidCalibrationHeight;
    //文字与刻度之间的间距
    private int mTextGap;

    //卷尺左侧的偏移量
    private float mOffsetX;
    //卷尺左侧的最大偏移量
    private float mMaxOffsetX;

    //中间指示线的颜色
    private int mIndicatorColor = Color.GREEN;
    //刻度的颜色
    private int mCalibrationColor = Color.GRAY;
    //文字的颜色
    private int mTextColor = Color.DKGRAY;
    //卷尺背景颜色
    private int mBgColor = Color.YELLOW;
    //文字的大小
    private float mTextSize;
    private Rect mTextRect;

    //滑动速率追踪
    private VelocityTracker mVelocityTracker;
    private Scroller mScroller;
    //滑动的最小速率
    private float mMinFlingVelocity;
    //滑动的最大速率
    private float mMaxFlingVelocity;

    private float mLastX;
    private float mDx;

    private OnValueChangeListener mValueListener;

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init();
    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs) {
        mScroller = new Scroller(context);
        mMinFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RulerView);
        mIndicatorColor = array.getColor(R.styleable.RulerView_indicatorColor, mIndicatorColor);
        mCalibrationColor = array.getColor(R.styleable.RulerView_calibrationColor, mCalibrationColor);
        mTextColor = array.getColor(R.styleable.RulerView_textColor, mTextColor);
        mBgColor = array.getColor(R.styleable.RulerView_bgColor, mBgColor);

        mMinValue = array.getFloat(R.styleable.RulerView_minValue, mMinValue);
        mMaxValue = array.getFloat(R.styleable.RulerView_maxValue, mMaxValue);
        mCurrValue = array.getFloat(R.styleable.RulerView_currValue, mCurrValue);
        array.recycle();
    }

    private void init() {
        mCalibrationPaint = new Paint();
        mCalibrationPaint.setAntiAlias(true);

        mScreenSize = ScreenUtils.getScreenWidth(getContext());

        mCalibrationGap = SizeUtils.dp2px(10);
        mCalibrationWidth = SizeUtils.dp2px(1);
        mMaxCalibrationHeight = SizeUtils.dp2px(50);
        mMinCalibrationHeight = SizeUtils.dp2px(25);
        mMidCalibrationHeight = SizeUtils.dp2px(35);
        mTextGap = SizeUtils.dp2px(12);
        mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics());
        mTextRect = new Rect();

        mPerCalibrationValue = 1;

        verifyValues(mMinValue, mMaxValue);
        mOffsetX = (mCurrValue - mMinValue) * mAssistValue / mPerCalibrationValue * mCalibrationGap;
        mMaxOffsetX = (mMaxValue - mMinValue) * mAssistValue / mPerCalibrationValue * mCalibrationGap;
        mTotalCalibrationNum = (int) ((mMaxValue - mMinValue) * mAssistValue / mPerCalibrationValue);
    }

    /**
     * 数值边界校验
     */
    private void verifyValues(float minValue, float maxValue) {
        if (mMinValue > mMaxValue) {
            mMinValue = maxValue;
        }

        if (mCurrValue < minValue) {
            mCurrValue = minValue;
        }

        if (mCurrValue > maxValue) {
            mCurrValue = maxValue;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = getMeasureSize(widthMeasureSpec, mScreenSize, ViewCompat.getMinimumHeight(this));
        //卷尺高度
        int height = getMeasureSize(heightMeasureSpec, mMaxCalibrationHeight + mTextGap * 2 + SizeUtils.sp2px(16),
                ViewCompat.getMinimumHeight(this));

        //设置默认的刻度为中间（即宽度的一半）
        mMiddle = mWidth / 2.f;

        setMeasuredDimension(mWidth, height);
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
        //绘制卷尺背景
        canvas.drawColor(mBgColor);
        //绘制刻度
        drawCalibration(canvas);
        drawIndicatorLine(canvas);
    }

    private void drawCalibration(Canvas canvas) {
        //当前绘制的刻度的位置
        float currCalibrationPos;
        //当前画的刻度的高度
        int currCalibrationHeight;
        //大刻度数值
        String calibrationValue;

        float distanceX = mMiddle - mOffsetX;

        //计算出左边第一个刻度（即绘制刻度的初始位置，超出屏幕左边的不绘制），跳过前面不需要绘制的刻度
        int left = 0;
        if (distanceX < 0) {
            left = (int) (-distanceX / mCalibrationGap);
        }

        currCalibrationPos = mMiddle - mOffsetX + left * mCalibrationGap;

        //这里设置绘制的刻度位置不大于屏幕的最大宽度（超出屏幕右边的不绘制）
        while (currCalibrationPos < mWidth && left <= mTotalCalibrationNum) {
            //省略掉第一个刻度（第一个刻度不绘制）
            if (currCalibrationPos == 0) {
                left++;
                currCalibrationPos = mMiddle - mOffsetX + left * mCalibrationGap;
                continue;
            }

            //两个大刻度之间的小刻度数量
            int perCount = 10;
            if (left % perCount == 0) {
                //绘制大刻度及大刻度下方文字
                //大刻度线的宽度为一般刻度线宽度的两倍
                mCalibrationPaint.setStrokeWidth(mCalibrationWidth * 2);
                currCalibrationHeight = mMaxCalibrationHeight;
                calibrationValue = String.valueOf(mMinValue + left * mPerCalibrationValue / mAssistValue);
                mCalibrationPaint.setColor(mTextColor);
                mCalibrationPaint.setTextSize(mTextSize);

                //去除小数点后在绘制大刻度数值
                if (calibrationValue.endsWith(".0")) {
                    calibrationValue = calibrationValue.substring(0, calibrationValue.length() - 2);
                }

                mCalibrationPaint.getTextBounds(calibrationValue, 0, calibrationValue.length(), mTextRect);
                canvas.drawText(calibrationValue, currCalibrationPos - (mTextRect.left + mTextRect.right) / 2.f,
                        currCalibrationHeight + mTextGap * 2, mCalibrationPaint);
            } else if (left % perCount != 0 && left % 5 == 0) {
                //绘制中刻度
                mCalibrationPaint.setStrokeWidth(mCalibrationWidth);
                currCalibrationHeight = mMidCalibrationHeight;
            } else {
                //绘制小刻度
                mCalibrationPaint.setStrokeWidth(mCalibrationWidth);
                currCalibrationHeight = mMinCalibrationHeight;
            }

            mCalibrationPaint.setColor(mCalibrationColor);
            canvas.drawLine(currCalibrationPos, 0, currCalibrationPos, currCalibrationHeight, mCalibrationPaint);

            left++;
            currCalibrationPos = mMiddle - mOffsetX + left * mCalibrationGap;

//            Log.i(TAG, "currCalibrationPos: " + currCalibrationPos);
        }
    }

    /**
     * 绘制指示线
     */
    private void drawIndicatorLine(Canvas canvas) {
        int drawX = getMeasuredWidth() / 2;
        mCalibrationPaint.setStyle(Paint.Style.FILL);
        mCalibrationPaint.setColor(mIndicatorColor);
        canvas.drawRect(drawX - SizeUtils.dp2px(1), 0,
                drawX + SizeUtils.dp2px(1), mMaxCalibrationHeight, mCalibrationPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        acquireVelocityTracker(event);

        float x = event.getX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastX = x;
                mDx = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                mDx = mLastX - x;
                validateOffset();

                mLastX = x;
                break;
            case MotionEvent.ACTION_UP:
                smoothMoveToCalibration();
                calculateVelocity();

                releaseVelocityTracker();
                break;
        }
        return true;
    }

    /**
     * 卷尺滑动时重新设置mOffsetX数值
     */
    private void validateOffset() {
        mOffsetX += mDx;

        if (mOffsetX < 0) {
            mOffsetX = 0;
            mDx = 0;
        } else if (mOffsetX > mMaxOffsetX) {
            mOffsetX = mMaxOffsetX;
            mDx = 0;
        }

        mCurrValue = mMinValue + Math.round(Math.abs(mOffsetX) / mCalibrationGap) * mPerCalibrationValue / mAssistValue;
        if (mValueListener != null) {
            mValueListener.onValueChange(mCurrValue);
        }

        postInvalidate();
    }

    /**
     * 滑动结束时，若中间指针指在两条刻度之间，需要让指针指在最近的刻度
     */
    private void smoothMoveToCalibration() {
        mOffsetX += mDx;

        if (mOffsetX < 0) {
            mOffsetX = 0;
        } else if (mOffsetX > mMaxOffsetX) {
            mOffsetX = mMaxOffsetX;
        }
        mLastX = 0;
        mDx = 0;

        //设置指示器指向的值为刻度值
        mCurrValue = mMinValue + Math.round(Math.abs(mOffsetX) / mCalibrationGap) * mPerCalibrationValue / mAssistValue;
        //重新纠正offset
        mOffsetX = (mCurrValue - mMinValue) * mAssistValue / mPerCalibrationValue * mCalibrationGap;

        if (mValueListener != null) {
            mValueListener.onValueChange(mCurrValue);
        }

        postInvalidate();
    }

    /**
     * 计算滑动速率
     */
    private void calculateVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
        float velocityX = mVelocityTracker.getXVelocity();

        if (Math.abs(velocityX) > mMinFlingVelocity) {
            //fling实际上只是将滑动相关的值储存起来，需要配合computeScroll()方法才能完成真正的滑动
            mScroller.fling(0, 0, (int) velocityX, 0,
                    Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mScroller.computeScrollOffset()) {
            if (mScroller.getCurrX() == mScroller.getFinalX()) {
                //fling动作已经结束，纠正指示线指向的刻度
                smoothMoveToCalibration();
            } else {
                int x = mScroller.getCurrX();
                mDx = mLastX - x;
                //在滑动过程中重新设置mOffsetX数值
                validateOffset();
                mLastX = x;
            }
        }
    }

    /**
     * @param event 向VelocityTracker添加Event
     * @see VelocityTracker#obtain()
     * @see VelocityTracker#addMovement(MotionEvent)
     */
    private void acquireVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        mVelocityTracker.addMovement(event);
    }

    /**
     * 释放VelocityTracker
     */
    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public float getCurrValue() {
        return mCurrValue;
    }

    //暴露给外部调用的接口
    public interface OnValueChangeListener {
        void onValueChange(float value);
    }

    public void setOnValueChangeListener(OnValueChangeListener valueListener) {
        this.mValueListener = valueListener;
    }
}
