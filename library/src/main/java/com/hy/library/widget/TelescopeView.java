package com.hy.library.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hy.library.R;

/**
 * Created by huangyong on 2019/4/17
 * 望远镜效果view
 */
public class TelescopeView extends View {
    private Paint mPaint;
    //背景
    private Bitmap mBitmapBg;
    //图片
    private Bitmap mBitmap;
    private Canvas mBgCanvas;

    private float mCurrX = -1;
    private float mCurrY = -1;

    public TelescopeView(Context context) {
        this(context, null);
    }

    public TelescopeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TelescopeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mBgCanvas = new Canvas();
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.crop_pic);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCurrX = event.getX();
                mCurrY = event.getY();
                postInvalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                mCurrX = event.getX();
                mCurrY = event.getY();
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

        if (mBitmapBg == null) {
            mBitmapBg = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);

            mBgCanvas.setBitmap(mBitmapBg);
            mBgCanvas.drawBitmap(mBitmap, null, new Rect(0, 0, getWidth(), getHeight()), mPaint);
        }

        if (mCurrX != -1 && mCurrY != -1) {
            mPaint.setShader(new BitmapShader(mBitmapBg, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
            canvas.drawCircle(mCurrX, mCurrY, 150, mPaint);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
}
