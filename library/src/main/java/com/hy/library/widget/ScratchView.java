package com.hy.library.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.hy.library.R;
import com.hy.library.util.SizeUtils;

/**
 * Created by huangyong on 2019/4/17
 * 仿刮刮卡效果view
 */
public class ScratchView extends View {
    private static final String TAG = "ScratchView";

    private Paint mPaint;
    //背景
    private Bitmap mBitmapBg;
    //图片
    private Bitmap mBitmap;
    private Canvas mBgCanvas;

    private Path mPath;
    private float mStartX = 0;
    private float mStartY = 0;

    public ScratchView(Context context) {
        this(context, null);
    }

    public ScratchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScratchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mBgCanvas = new Canvas();
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.crop_pic);

        Log.i(TAG, "mBitmap: " + mBitmap);

        mPath = new Path();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(event.getX(), event.getY());
                mStartX = event.getX();
                mStartY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                mPath.quadTo(mStartX, mStartY, event.getX(), event.getY());
                mStartX = event.getX();
                mStartY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                mStartX = event.getX();
                mStartY = event.getY();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        postInvalidate();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //将背景绘制到目标图层上，这是自己创建的图层，不是系统view自带的图层
        if (mBitmapBg == null) {
            //这是一个透明图层
            mBitmapBg = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);

            mBgCanvas.setBitmap(mBitmapBg);
            mBgCanvas.drawBitmap(mBitmap, null, new Rect(0, 0, getWidth(), getHeight()), mPaint);
        }

        //将目标图层绘制打画布上（该图层上面已经绘制了其他东西，因此上面绘制的内容也会连带绘制到画布上）
        if (!mPath.isEmpty()) {
            //这里是通过shader来实现的，实际上并不是真正的刮刮卡效果，只是通过shader将目标图层绘制到画布上，
            //但是此时shader还不能显示，我们必须通过canvas绘制区域来显示，这里的path就是绘制的区域
            mPaint.setShader(new BitmapShader(mBitmapBg, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(SizeUtils.dp2px(16));
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            //绘制路径
            canvas.drawPath(mPath, mPaint);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
}
