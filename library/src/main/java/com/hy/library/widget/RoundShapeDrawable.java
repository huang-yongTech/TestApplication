package com.hy.library.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by huangyong on 2019/4/19
 * 自定义drawable实现圆角图片
 */
public class RoundShapeDrawable extends Drawable {
    private Bitmap mBitmap;
    private Paint mPaint;
    private RectF mRectBounds;

    public RoundShapeDrawable(Bitmap bitmap) {
        this.mBitmap = bitmap;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mRectBounds = new RectF();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawRoundRect(mRectBounds, 20, 20, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);

        BitmapShader shader = new BitmapShader(Bitmap.createScaledBitmap(mBitmap, right - left, bottom - top, false),
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint.setShader(shader);
        mRectBounds.set(left, top, right, bottom);
    }

    /**
     * 设置drawable的默认宽度
     */
    @Override
    public int getIntrinsicWidth() {
        return mBitmap.getWidth();
    }

    /**
     * 设置drawable的默认高度
     */
    @Override
    public int getIntrinsicHeight() {
        return mBitmap.getHeight();
    }
}
