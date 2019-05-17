package com.hy.library.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;


import com.hy.library.R;

/**
 * 比例圆角矩形view
 */
public class MyView extends View {
    private float iNum = 2378;                     //进(左)的数量
    private int iColor = Color.GREEN; //进的颜色
    private float oNum = 7896;                     //出(右)的数量
    private int oColor = Color.RED;            //出的颜色
    private int mInclination = 45;               //两柱中间的倾斜度
    private int iTextColor = Color.WHITE;        //进的百分比数字颜色
    private int oTextColor = Color.WHITE;        //出的百分比数字颜色
    private int TextSize = 12;                   //百分比字体大小

    private float iPre;
    private float oPre;

    private String txtiPre;                      //显示进的百分比
    private String txtoPre;                      //显示出的百分比

    private Paint mPaint;
    private Rect mBound;                        //包含文字的框
    private int color;
    private int arryColor;
    int width;
    int height;

    public MyView(Context context) {
        this(context, null);

    }

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(5);

        mBound = new Rect();
    }

    @SuppressLint("ResourceAsColor")
    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

        TypedArray arry = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyView, defStyleAttr, 0);
        color = arry.getColor(R.styleable.MyView_oColor, getResources().getColor(R.color.red));
        arryColor = arry.getColor(R.styleable.MyView_iColor, getResources().getColor(R.color.blue));

        iNum = arry.getFloat(R.styleable.MyView_iNum, 2378);
        iColor = arry.getColor(R.styleable.MyView_iColor, Color.GREEN);
        oNum = arry.getFloat(R.styleable.MyView_oNum, 7896);
        oColor = arry.getColor(R.styleable.MyView_oColor, Color.RED);
        mInclination = arry.getInt(R.styleable.MyView_Inclination, 45);
        iTextColor = arry.getColor(R.styleable.MyView_iTextColor, Color.WHITE);
        oTextColor = arry.getColor(R.styleable.MyView_oTextColor, Color.WHITE);
        TextSize = arry.getDimensionPixelSize(R.styleable.MyView_TextSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));

        arry.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);


        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = getPaddingLeft() + getWidth() + getPaddingRight();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getPaddingTop() + getHeight() + getPaddingBottom();
        }

        setMeasuredDimension(width, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        iPre = (iNum / (iNum + oNum)) * getWidth();
        oPre = (oNum / (iNum + oNum)) * getWidth();

        //Log.e("mPre", "iPre:" + iPre + "  oPre:" + oPre + "width" + getWidth());

        //如果进值或出值有一个为0，则另一个就会占满整个进度条，这时就不需要倾斜角度了
        if (iNum == 0 || oPre == 0) {
            mInclination = 0;
        }

        Path iPath = new Path();
        iPath.moveTo(0, 20);
        iPath.quadTo(0, 0, 20, 0);
        iPath.lineTo(iPre + mInclination, 0);
        iPath.lineTo(iPre, getHeight());
        iPath.lineTo(20, getHeight());
        iPath.quadTo(0, getHeight(), 0, getHeight() - 20);
        iPath.close();
        mPaint.setColor(iColor);
        canvas.drawPath(iPath, mPaint);


        Path oPath = new Path();
        oPath.moveTo(iPre + mInclination, 0);
        oPath.lineTo(getWidth() - 20, 0);
        oPath.quadTo(getWidth(), 0, getWidth(), 20);
        oPath.lineTo(getWidth(), getHeight() - 20);
        oPath.quadTo(getWidth(), getHeight(), getWidth() - 20, getHeight());
        oPath.lineTo(iPre - mInclination, getHeight());
        oPath.close();

        mPaint.setColor(oColor);
        canvas.drawPath(oPath, mPaint);

//        txtiPre = getProValText(iNum / (iNum + oNum) * 100);
//        txtoPre = getProValText(oNum / (iNum + oNum) * 100);
        txtoPre = 7896 + "";
        txtiPre = 2378 + "";


        mPaint.setColor(iTextColor);
        mPaint.setTextSize(TextSize);

        mPaint.getTextBounds(txtiPre, 0, txtiPre.length(), mBound);
        //判断一下，如果进值为0则不显示，如果进值不为空而出值为0，则进值的数值显示居中显示
        if (iNum != 0 && oNum != 0) {

            canvas.drawText(txtiPre, 20, getHeight() / 2 + mBound.height() / 2, mPaint);

        } else if (iNum != 0 && oNum == 0) {

            canvas.drawText(txtiPre, getWidth() / 2 - mBound.width() / 2, getHeight() / 2 + mBound.height() / 2, mPaint);

        }

        mPaint.setColor(oTextColor);
        mPaint.getTextBounds(txtoPre, 0, txtoPre.length(), mBound);
        if (oNum != 0 && iNum != 0) {

            canvas.drawText(txtoPre, getWidth() - 20 - mBound.width(), getHeight() / 2 + mBound.height() / 2, mPaint);

        } else if (oNum != 0 && iNum == 0) {
            canvas.drawText(txtoPre, getWidth() / 2 - mBound.width() / 2, getHeight() / 2 + mBound.height() / 2, mPaint);
        }


    }

    /**
     * 格式化显示的百分比
     *
     * @param proValue
     * @return
     */
//    private String getProValText(float proValue) {
//        DecimalFormat format = new DecimalFormat("#0.0");
//        return format.format(proValue) + "%";
//    }

    /**
     * 动态设置进值
     *
     * @param iNum
     */
    public void setINum(float iNum) {
        this.iNum = iNum;
        postInvalidate();
    }

    /**
     * 动态设置出值
     *
     * @param oNum
     */
    public void setONum(float oNum) {
        this.oNum = oNum;
        postInvalidate();
    }
}
