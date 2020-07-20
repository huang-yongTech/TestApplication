package com.hy.library.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hy.library.R;
import com.hy.library.util.SizeUtils;

/**
 * Created by huangyong on 2019/4/17
 * 橡皮擦和刮刮卡效果view
 */
public class EraseAndScratchView extends View {
    private static final String TAG = "EraseAndScratchView";

    private Paint mPaint;
    private Bitmap mDstBitmap, mSrcBitmap, mLayerBitmap;

    private Path mPath;
    private float mStartX;
    private float mStartY;

    //记录目标图像的canvas
    private Canvas mDstCanvas;
    private PorterDuffXfermode mXfermode;

    private Rect mSrcRect;

    public EraseAndScratchView(Context context) {
        this(context, null);
    }

    public EraseAndScratchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EraseAndScratchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(SizeUtils.dp2px(16));

        mLayerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.crop_pic);
        //创建一个透明的源图层
        mSrcBitmap = Bitmap.createBitmap(mLayerBitmap.getWidth(), mLayerBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        //用指定颜色填充该透明源图层
        mSrcBitmap.eraseColor(Color.GREEN);
        //创建一个透明的目标图层
        mDstBitmap = Bitmap.createBitmap(mLayerBitmap.getWidth(), mLayerBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        mPath = new Path();
        mDstCanvas = new Canvas();
        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);

        mSrcRect = new Rect();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartY = event.getY();
                mPath.moveTo(mStartX, mStartY);
                postInvalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                mPath.quadTo(mStartX, mStartY, event.getX(), event.getY());
                mStartX = event.getX();
                mStartY = event.getY();
                break;
        }
        postInvalidate();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //将底层图层绘制在画布上，即刮卡后显示的图层
        canvas.drawBitmap(mLayerBitmap, 0, 0, mPaint);

        int layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), mPaint);
        //将手势绘制到目标图像上
        mDstCanvas.setBitmap(mDstBitmap);
        mDstCanvas.drawPath(mPath, mPaint);

        //将目标图像绘制到画布上
        canvas.drawBitmap(mDstBitmap, 0, 0, mPaint);
        //绘制源图像
        mPaint.setXfermode(mXfermode);
        mSrcRect.set(0, 0, getWidth(), getHeight());
        canvas.drawBitmap(mSrcBitmap, null, mSrcRect, mPaint);
        mPaint.setXfermode(null);

        canvas.restoreToCount(layerId);
    }
}
