package com.hy.library.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hy.library.R;

/**
 * Created by huangyong on 2019/4/22
 * 自定义SurfaceView背景动画
 */
public class SurfaceAnimView extends SurfaceView {
    private Bitmap mBitmapBg;
    private SurfaceHolder mHolder;
    private Canvas mCanvas;

    private int mSurfaceWidth;
    private int mSurfaceHeight;

    private State mState = State.LEFT;
    //绘制的起始X坐标
    private int mPosX;
    //每次绘制的移动距离
    private static final int mBitMapStep = 1;

    //绘制控制标识
    private boolean mFlag;

    public SurfaceAnimView(Context context) {
        this(context, null);
    }

    public SurfaceAnimView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SurfaceAnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mFlag = true;
                startAnim();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mFlag = false;
            }
        });
    }

    private void startAnim() {
        mSurfaceWidth = getWidth();
        mSurfaceHeight = getHeight();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scenery);
        mBitmapBg = Bitmap.createScaledBitmap(bitmap, mSurfaceWidth * 3 / 2, mSurfaceHeight, true);

        //在子线程中开始绘制
        new Thread(new Runnable() {
            @Override
            public void run() {
                //开始循环
                while (mFlag) {
                    drawBg();
                }
            }
        }).start();
    }

    private void drawBg() {
        mCanvas = mHolder.lockCanvas();

        //这里需要做一下判空处理，防止界面退出后获取的canvas为空导致出现空指针异常
        //这里是直接返回，什么也不做
        if (mCanvas == null) {
            return;
        }

        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mCanvas.drawBitmap(mBitmapBg, mPosX, 0, null);

        switch (mState) {
            case LEFT:
                //向左移动
                mPosX -= mBitMapStep;
                break;
            case RIGHT:
                //向右移动
                mPosX += mBitMapStep;
                break;
            default:
                break;
        }

        if (mPosX <= -mSurfaceWidth / 2) {
            mState = State.RIGHT;
        }

        if (mPosX >= 0) {
            mState = State.LEFT;
        }

        mHolder.unlockCanvasAndPost(mCanvas);
    }

    private enum State {
        LEFT, RIGHT
    }
}
