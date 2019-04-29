package com.hy.library.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.hy.library.R;

/**
 * Created by huangyong on 2019/4/24
 * 仿即刻点赞效果的view
 */
public class JiKePraiseView extends View {
    //view是否被选中标记位
    private boolean mIsLike;
    //bitmap图片
    private Bitmap mThumbLikeBitmap, mThumbUnLikeBitmap, mDecoBitmap;
    //view的宽高
    private int mWidth, mHeight;
    //文字的绘制位置
    private int mTextX, mTextY;

    private ObjectAnimator mAnimator;

    public JiKePraiseView(Context context) {
        super(context);
        init();
    }

    public JiKePraiseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JiKePraiseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mThumbLikeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thumbs_on);
        mThumbUnLikeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thumbs_off);
        mDecoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thumbs_decoration);

        initAnim();
    }

    private void initAnim() {

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mThumbLikeBitmap != null) {
            mThumbLikeBitmap.recycle();
        }

        if (mThumbUnLikeBitmap != null) {
            mThumbUnLikeBitmap.recycle();
        }

        if (mDecoBitmap != null) {
            mDecoBitmap.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
