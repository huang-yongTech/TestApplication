package com.hy.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
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
 * Created by huangyong on 2019/5/22
 * 仿薄荷健康滑动卷尺
 */
public class BooHeRulerView extends View {
    private static final String TAG = "BooHeRulerView";

    private Paint mCalibrationPaint;

    //卷尺宽度
    private int mWidth;
    //屏幕宽度
    private int mScreenWidth;

    //卷尺的当前刻度
    private float mMiddle;
    //卷尺的最小刻度
    private float mMinValue = 0;
    //卷尺的最大刻度
    private float mMaxValue = 100;
    //卷尺当前值
    private float mCurrValue = 0;
    //卷尺初始值
    private float mOriginValue = 0;
    //辅助数值
    private int mAssistValue = 10;
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
    private float mOriginOffsetX;

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

    //滑动左边界
    private float mLeftBorder;
    //滑动右边界
    private float mRightBorder;

    private float mLastX;
    private float mLastY;
    //滑动的最小速率
    private float mMinFlingVelocity;
    //滑动的最大速率
    private float mMaxFlingVelocity;

    private BooHeRulerView.OnValueChangeListener mValueListener;

    public BooHeRulerView(Context context) {
        this(context, null);
    }

    public BooHeRulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        initAttrs(context, attrs);
        init();
    }

    public BooHeRulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs) {
        mScroller = new Scroller(context);
        mMinFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BooHeRulerView);
        mIndicatorColor = array.getColor(R.styleable.BooHeRulerView_indicatorColor, mIndicatorColor);
        mCalibrationColor = array.getColor(R.styleable.BooHeRulerView_calibrationColor, mCalibrationColor);
        mTextColor = array.getColor(R.styleable.BooHeRulerView_textColor, mTextColor);
        mBgColor = array.getColor(R.styleable.BooHeRulerView_bgColor, mBgColor);

        mMinValue = array.getFloat(R.styleable.BooHeRulerView_minValue, mMinValue);
        mMaxValue = array.getFloat(R.styleable.BooHeRulerView_maxValue, mMaxValue);
        mCurrValue = array.getFloat(R.styleable.BooHeRulerView_currValue, mCurrValue);
        mOriginValue = mCurrValue;
        array.recycle();
    }

    private void init() {
        mCalibrationPaint = new Paint();
        mCalibrationPaint.setAntiAlias(true);

        mScreenWidth = ScreenUtils.getScreenWidth(getContext());

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
        //初始化时中间数值相对于最小数值的偏移量（初始显示数值是显示在正中间的）
        mOriginOffsetX = (mOriginValue - mMinValue) * mAssistValue / mPerCalibrationValue * mCalibrationGap;
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
        int desireWidth = (int) (mTotalCalibrationNum * mCalibrationGap) + getPaddingStart() + getPaddingEnd();
        int desireHeight = mMaxCalibrationHeight + SizeUtils.sp2px(16) + mTextGap * 2 + getPaddingTop() + getPaddingBottom();
        mWidth = desireWidth;

        int width = getMeasureSize(widthMeasureSpec, desireWidth, ViewCompat.getMinimumWidth(this));
        int height = getMeasureSize(heightMeasureSpec, desireHeight, ViewCompat.getMinimumHeight(this));
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

        mLeftBorder = getLeft();
        mRightBorder = mWidth;
        mMiddle = mScreenWidth / 2.0f;

        drawBg(canvas);
        drawCalibration(canvas);
        drawIndicator(canvas);
    }

    /**
     * 绘制背景
     */
    private void drawBg(Canvas canvas) {
        canvas.drawColor(Color.YELLOW);
    }

    /**
     * 绘制刻度
     */
    private void drawCalibration(Canvas canvas) {
        //当前刻度的宽度
        int calibrationHeight;
        //当前刻度的位置
        float calibrationPos;

        //每两个大刻度之间的小刻度数量
        int perCount = 10;
        int left = 0;
        while (left <= mTotalCalibrationNum) {
            //calibrationPos是从0开始的，为了能让中间指示器指示的初始数值对应正确的刻度，需要加上mMiddle
            calibrationPos = left * mCalibrationGap + mMiddle - mOriginOffsetX;

            if (left % perCount == 0) {
                //绘制大刻度
                mCalibrationPaint.setStrokeWidth(mCalibrationWidth * 2);
                calibrationHeight = mMaxCalibrationHeight;

                //绘制大刻度下的文字
                String value = String.valueOf(mMinValue + (float) left * mPerCalibrationValue / mAssistValue);
                if (value.endsWith(".0")) {
                    value = value.substring(0, value.length() - 2);
                }
                mCalibrationPaint.setTextSize(mTextSize);
                mCalibrationPaint.setColor(mTextColor);
                mCalibrationPaint.getTextBounds(value, 0, value.length(), mTextRect);
                canvas.drawText(value, calibrationPos - (mTextRect.left + mTextRect.right) / 2.f,
                        mMaxCalibrationHeight + mTextGap * 2, mCalibrationPaint);
            } else if (left % 10 != 0 && left % 5 == 0) {
                //绘制中刻度
                mCalibrationPaint.setStrokeWidth(mCalibrationWidth);
                calibrationHeight = mMidCalibrationHeight;
            } else {
                //绘制小刻度
                mCalibrationPaint.setStrokeWidth(mCalibrationWidth);
                calibrationHeight = mMinCalibrationHeight;
            }

            mCalibrationPaint.setColor(mCalibrationColor);
            canvas.drawLine(calibrationPos, 0, calibrationPos, calibrationHeight, mCalibrationPaint);

            left++;
        }
    }

    /**
     * 绘制指示线
     */
    private void drawIndicator(Canvas canvas) {
        mCalibrationPaint.setColor(Color.GREEN);
        //这里加上getScrollX()是让指示线不随内容的滑动而滑动，一直保持在中间位置
        int centerX = (int) (mMiddle + getScrollX());
        canvas.drawRect(centerX - mCalibrationWidth, 0,
                centerX + mCalibrationWidth, mMaxCalibrationHeight, mCalibrationPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        acquireVelocityTracker(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastX = event.getX();
                mLastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = mLastX - event.getX();
                float deltaY = mLastY - event.getY();

                //这里不比较deltaX和ViewConfiguration.get(context).getScaledTouchSlop()之间的大小，这个容易导致滑动出现卡顿
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    getParent().requestDisallowInterceptTouchEvent(true);

                    scrollBy((int) deltaX, 0);
                    validateBorder();
                    validateCurrentValue();
                }

                mLastX = event.getX();
                mLastY = event.getY();
                break;
            case MotionEvent.ACTION_UP:

                smoothMoveToRecently();

                mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                float velocityX = mVelocityTracker.getXVelocity();
                if (Math.abs(velocityX) >= mMinFlingVelocity) {
                    getParent().requestDisallowInterceptTouchEvent(true);

                    mScroller.fling(getScrollX(), 0, (int) (-velocityX), 0,
                            Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
                }
                releaseVelocityTracker();
                break;
        }
        return true;
    }

    /**
     * 如果指针处于两个刻度之间，滑动到指向最近的刻度
     */
    private void smoothMoveToRecently() {
        //这里使用了求余操作来计算指针和最近刻度的距离
        float distanceToCalibration = (mMiddle + getScrollX() - mLeftBorder) % mCalibrationGap;

        //这里要区分下distanceToCalibration的正负值问题（即卷尺是往初始中间刻度值右侧滑动时取正值，往初始中间刻度值左侧滑动时取负值）
        if (distanceToCalibration >= 0) {
            if (distanceToCalibration >= mCalibrationGap / 2) {
                mScroller.startScroll(getScrollX(), 0, (int) (mCalibrationGap - distanceToCalibration), 0, 500);
            } else {
                mScroller.startScroll(getScrollX(), 0, (int) -distanceToCalibration, 0, 500);
            }
        } else {
            if (distanceToCalibration <= -mCalibrationGap / 2) {
                mScroller.startScroll(getScrollX(), 0, (int) -(mCalibrationGap + distanceToCalibration), 0, 500);
            } else {
                mScroller.startScroll(getScrollX(), 0, (int) -distanceToCalibration, 0, 500);
            }
        }

        invalidate();
    }

    /**
     * 滑动时纠正当前value
     */
    private void validateCurrentValue() {
        float roundValue = Math.round((getScrollX() - mLeftBorder + mOriginOffsetX) / mCalibrationGap);
        mCurrValue = mMinValue + roundValue * mPerCalibrationValue / mAssistValue;
        if (mValueListener != null) {
            mValueListener.onValueChange(mCurrValue);
        }
    }

    /**
     * 边界滑动纠正
     */
    private void validateBorder() {
        if (getScrollX() < -mOriginOffsetX) {
            scrollTo((int) (-mOriginOffsetX), 0);
        } else if (getScrollX() > mRightBorder - mOriginOffsetX) {
            scrollTo((int) (mRightBorder - mOriginOffsetX), 0);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            validateBorder();
            validateCurrentValue();

            //当fling动作结束时，若指针指在两个刻度之间，纠正指针指向
            if (!mScroller.computeScrollOffset()) {
                smoothMoveToRecently();
            }

            postInvalidate();
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

    public void setOnValueChangeListener(BooHeRulerView.OnValueChangeListener valueListener) {
        this.mValueListener = valueListener;
    }
}
