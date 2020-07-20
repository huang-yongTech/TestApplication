package com.hy.library.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.hy.library.util.SizeUtils;

/**
 * Created by huangyong on 2019/4/11
 * 多边形统计数据表格
 */
public class SpiderView extends View {
    private static final String TAG = "SpiderView";

    private Paint mRadarPaint, mValuePaint;
    //网状图中心点
    private int mCenterX, mCenterY;
    //网状图半径
    private float mRadio;
    //网格有多少个间隔
    private int mCount;
    //夹角
    private double mAngle;
    //成绩统计
    private double[] mData = new double[]{2, 5, 1, 4, 3, 6};
    //网格的最大值，即满分成绩
    private int mMaxValue;

    public SpiderView(Context context) {
        this(context, null);
    }

    public SpiderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpiderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRadarPaint = new Paint();
        mRadarPaint.setAntiAlias(true);
        mRadarPaint.setColor(Color.parseColor("#770000FF"));
        mRadarPaint.setStyle(Paint.Style.FILL);

        mValuePaint = new Paint();
        mValuePaint.setAntiAlias(true);
        mValuePaint.setColor(Color.GREEN);
        mValuePaint.setStyle(Paint.Style.STROKE);
        mValuePaint.setStrokeWidth(SizeUtils.dp2px(1));

        mCount = 6;
        mAngle = Math.toRadians(360d / mCount);
        mMaxValue = mCount;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mRadio = Math.min(w, h) / 2f * 0.9f;

        mCenterX = w / 2;
        mCenterY = h / 2;

        Log.i(TAG, "mCenterX = " + mCenterX);
        Log.i(TAG, "mCenterY = " + mCenterY);

        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawPolygon(canvas);
        drawLines(canvas);
        drawRegion(canvas);
    }

    /**
     * 绘制多边形
     */
    private void drawPolygon(Canvas canvas) {
        Path path = new Path();
        //每一环网格之间的间距
        float r = mRadio / mCount;

        //绘制网状图，i从1开始表示中心点不用绘制
        for (int i = 1; i <= mCount; i++) {
            float currR = r * i;

            //绘制网状图的每一环
            path.reset();
            for (int j = 0; j < mCount; j++) {
                if (j == 0) {
                    path.moveTo(mCenterX + currR, mCenterY);
                } else {
                    float currX = (float) (mCenterX + currR * Math.cos(mAngle * j));
                    float currY = (float) (mCenterY + currR * Math.sin(mAngle * j));

                    path.lineTo(currX, currY);
                }
            }
            path.close();
            canvas.drawPath(path, mValuePaint);
        }
    }

    /**
     * 绘制网格中线
     */
    private void drawLines(Canvas canvas) {
        for (int i = 0; i < mCount; i++) {
            float currX = (float) (mCenterX + mRadio * Math.cos(mAngle * i));
            float currY = (float) (mCenterY + mRadio * Math.sin(mAngle * i));
            canvas.drawLine(mCenterX, mCenterY, currX, currY, mValuePaint);
        }
    }

    /**
     * 绘制数据图
     */
    private void drawRegion(Canvas canvas) {
        Path path = new Path();
        float r = mRadio / mCount;

        path.reset();
        for (int i = 0; i < mCount; i++) {
            double percent = mData[i] / mMaxValue;

            float currX = (float) (mCenterX + mRadio * Math.cos(mAngle * i) * percent);
            float currY = (float) (mCenterY + mRadio * Math.sin(mAngle * i) * percent);

            if (i == 0) {
                path.moveTo(currX, mCenterY);
            } else {
                path.lineTo(currX, currY);
            }

            canvas.drawCircle(currX, currY, r / 4, mRadarPaint);
        }
        path.close();
        mRadarPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, mRadarPaint);
    }
}
