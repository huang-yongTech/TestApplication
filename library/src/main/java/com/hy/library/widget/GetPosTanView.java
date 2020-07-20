package com.hy.library.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.hy.library.R;
import com.hy.library.util.SizeUtils;

/**
 * Created by huangyong on 2019/4/16
 * 带箭头的旋转路径
 */
public class GetPosTanView extends View {
    private static final String TAG = "GetPosTanView";

    private Paint mPathPaint;
    private Paint mIconPaint;
    private PathMeasure mPathMeasure;
    private ValueAnimator mAnimator;
    private float mCurrValue;
    private float mPathLength;

    private Path mDstPath;
    private Bitmap mArrowBitMap;

    private float[] mPos = new float[2];
    private float[] mTan = new float[2];

    private Matrix mMatrix;

    public GetPosTanView(Context context) {
        this(context, null);
    }

    public GetPosTanView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GetPosTanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mPathPaint = new Paint();
        mPathPaint.setAntiAlias(true);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setStrokeWidth(SizeUtils.dp2px(2));

        mIconPaint = new Paint();
        mIconPaint.setAntiAlias(true);

        Path circlePath = new Path();
        circlePath.addCircle(100, 100, 50, Path.Direction.CW);

        mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(circlePath, true);

        mPathLength = mPathMeasure.getLength();
        mDstPath = new Path();
        mArrowBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.arraw);

        mMatrix = new Matrix();

        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(2000);
        mAnimator.setRepeatCount(3);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMeasureSize(widthMeasureSpec, getPaddingStart() + getPaddingEnd(),
                ViewCompat.getMinimumWidth(this));
        int height = getMeasureSize(heightMeasureSpec, getPaddingTop() + getPaddingBottom(),
                ViewCompat.getMinimumHeight(this));
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

        //绘制路径
        mDstPath.reset();
        float start = 0;
        float end = mPathLength * mCurrValue;
        mPathMeasure.getSegment(start, end, mDstPath, true);
        canvas.drawPath(mDstPath, mPathPaint);

        //绘制箭头图片
        mMatrix.reset();
//        mPathMeasure.getPosTan(end, mPos, mTan);
//        float degrees = (float) (Math.atan2(mTan[1], mTan[0]) * 180.0 / Math.PI);
//        mMatrix.postRotate(degrees, mArrowBitMap.getWidth() / 2.0f, mArrowBitMap.getHeight() / 2.0f);
//        mMatrix.postTranslate(mPos[0] - mArrowBitMap.getWidth() / 2.0f, mPos[1] - mArrowBitMap.getHeight() / 2.0f);
        //另一种方法
        mPathMeasure.getMatrix(end, mMatrix, PathMeasure.POSITION_MATRIX_FLAG | PathMeasure.TANGENT_MATRIX_FLAG);
        mMatrix.preTranslate(-mArrowBitMap.getWidth() / 2.0f, -mArrowBitMap.getHeight() / 2.0f);
        canvas.drawBitmap(mArrowBitMap, mMatrix, mIconPaint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAnimator.start();
    }

    /**
     * 资源回收及释放
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mAnimator != null) {
            mAnimator.cancel();
        }

        if (mArrowBitMap != null) {
            mArrowBitMap.recycle();
        }
    }
}
