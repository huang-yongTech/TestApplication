package com.hy.library.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hy.library.R;

/**
 * Created by huangyong on 2019/4/17
 * 放大镜效果view
 */
public class MagnifyView extends View {
    //图片
    private Bitmap mBitmap;

    //放大镜的半径
    private int mRadius = 160;
    //放大倍数
    private int mFactor = 3;
    //矩阵
    private Matrix mMatrix;

    private ShapeDrawable mShapeDrawable;

    private int mCurrX;
    private int mCurrY;

    public MagnifyView(Context context) {
        this(context, null);
    }

    public MagnifyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MagnifyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mMatrix = new Matrix();
        mShapeDrawable = new ShapeDrawable(new OvalShape());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCurrX = (int) event.getX();
                mCurrY = (int) event.getY();

                //设置shader显示区域跟随手指移动
                mMatrix.setTranslate(mRadius - mCurrX * mFactor, mRadius - mCurrY * mFactor);
                mShapeDrawable.getPaint().getShader().setLocalMatrix(mMatrix);
                mShapeDrawable.setBounds(mCurrX - mRadius, mCurrY - mRadius, mCurrX + mRadius, mCurrY + mRadius);
                postInvalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                mCurrX = (int) event.getX();
                mCurrY = (int) event.getY();

                //设置shader显示区域跟随手指移动
                mMatrix.setTranslate(mRadius - mCurrX * mFactor, mRadius - mCurrY * mFactor);
                mShapeDrawable.getPaint().getShader().setLocalMatrix(mMatrix);
                mShapeDrawable.setBounds(mCurrX - mRadius, mCurrY - mRadius, mCurrX + mRadius, mCurrY + mRadius);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mCurrX = -1;
                mCurrY = -1;
                break;
        }
        postInvalidate();

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmap == null) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.crop_pic);

            if (mCurrX != -1 && mCurrY != -1) {
                //创建放大的shader图像
                @SuppressLint("DrawAllocation")
                BitmapShader bitmapShader = new BitmapShader(
                        Bitmap.createScaledBitmap(mBitmap, getWidth() * mFactor, getHeight() * mFactor, false)
                        , Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

                //绘制圆形区域
                mShapeDrawable.getPaint().setShader(bitmapShader);
            }
        }

        //绘制原图
        canvas.drawBitmap(mBitmap, 0, 0, null);
        //绘制放大图
        mShapeDrawable.draw(canvas);
    }
}
