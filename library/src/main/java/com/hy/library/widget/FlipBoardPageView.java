package com.hy.library.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.hy.library.R;

/**
 * Created by huangyong on 2018/12/4
 * 谷歌图片三维动画效果（通过翻转camera以及移动画布来实现）
 * <p>
 * 思路：将图片分为左右两个部分，左边为不翻转，右边为翻转
 * （所有操作均在画布中心点平移到原点后进行的操作，因此操作完后需要将画布平移回来；
 * 如果画布还有别的改变状态的操作，如旋转等，则需要在结束后将画布旋转回来）
 * 1、首先将右半部分向上翻转，左半部分不翻转
 * 2、将右半部分旋转-270°（绕Z轴原点，即中心点），左半部分做相应的旋转（即将画布旋转270°）
 * 3、旋转270度后，此时左半部分在上面，将左半部分向上翻转，右半部分不动
 * <p>
 * 备注：旋转其实是画布在旋转，而图片并没有旋转，通过画布的旋转来达到分别绘制图片不同部分的动画效果
 */
public class FlipBoardPageView extends View {
    private static final String TAG = "FlipBoardPageView";

    private Paint mPaint;
    private Bitmap mBitmap;
    private Camera mCamera;
    private Rect mRectNoRotate;
    private Rect mRectRotate;

    private AnimatorSet mAnimatorSet;

    //camera翻转的角度（Y轴方向翻转）
    private int degreeY;
    //canvas旋转的角度（Z轴方向旋转）
    private int degreeZ;
    //最后图片上半部分翻转角度（X轴方向翻转）
    private int degreeX;

    public FlipBoardPageView(Context context) {
        this(context, null);
    }

    public FlipBoardPageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlipBoardPageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.google_map);
        mRectNoRotate = new Rect();
        mRectRotate = new Rect();

        mCamera = new Camera();

        //起始右侧翻转角度45°
        ObjectAnimator animator1 = ObjectAnimator.ofInt(this, "degreeY", 0, -45);
        //右侧旋转270°，左侧也配合旋转
        ObjectAnimator animator2 = ObjectAnimator.ofInt(this, "degreeZ", 0, -270);
        //图片上半部分翻转45°
        ObjectAnimator animator3 = ObjectAnimator.ofInt(this, "degreeX", 0, -30);
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setDuration(1200);
        mAnimatorSet.playSequentially(animator1, animator2, animator3);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMeasureSize(widthMeasureSpec, mBitmap.getWidth() + getPaddingStart() + getPaddingEnd(),
                ViewCompat.getMinimumWidth(this));
        int height = getMeasureSize(heightMeasureSpec, mBitmap.getHeight() + getPaddingTop() + getPaddingBottom(),
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

    public void start() {
        mAnimatorSet.start();
    }

    public void end() {
        mAnimatorSet.end();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        end();

        if (mBitmap != null) {
            mBitmap.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        //中心点
        int centerX = width / 2;
        int centerY = height / 2;

        //绘制的起始坐标
        int x = width / 2 - mBitmap.getWidth() / 2;
        int y = height / 2 - mBitmap.getHeight() / 2;

        //备注：这里的绘制一定要严格注意画布的平移以及画布的旋转的先后顺序
        //之所以需要对camera和canvas进行restore操作是因为后续还有相应的绘制，所以要恢复原状态

        //绘制不翻转部分
        canvas.save();
        mCamera.save();
        //将画布中心平移到原点
        canvas.translate(centerX, centerY);
        //旋转画布
        canvas.rotate(degreeZ);
        //备注：由于画布旋转了-270度，此时Y轴的正方向为水平正方向，因此最后图片上半部分翻转45°应该将相机沿Y轴旋转
        //此时Y轴的正向旋转方向与X轴的正向旋转方向相反，需要对旋转的度数取反
        mCamera.rotateY(-degreeX);
        mRectNoRotate.set(-centerX, -centerY, 0, centerY);
        //裁切不旋转部分
        canvas.clipRect(mRectNoRotate);
        mCamera.applyToCanvas(canvas);
        //将画布旋转回来（该操作代码必须要在将画布移回原位代码之前，若放在后面，则旋转时需要调用带旋转中心点参数的方法rotate(degree,x,y)）
        canvas.rotate(-degreeZ);
        //将画布移到原位置
        canvas.translate(-centerX, -centerY);
        mCamera.restore();
        canvas.drawBitmap(mBitmap, x, y, mPaint);
        canvas.restore();

        //绘制翻转部分
        canvas.save();
        mCamera.save();
        //将画布中心移到原点
        canvas.translate(centerX, centerY);
        //旋转画布
        canvas.rotate(degreeZ);
        mCamera.rotateY(degreeY);
        mRectRotate.set(0, -centerY, centerX, centerY);
        //裁切旋转部分
        canvas.clipRect(mRectRotate);
        mCamera.applyToCanvas(canvas);
        //将画布旋转回来
        canvas.rotate(-degreeZ);
        //将画布移回原位置
        canvas.translate(-centerX, -centerY);
        mCamera.restore();
        canvas.drawBitmap(mBitmap, x, y, mPaint);
        canvas.restore();
    }

    public int getDegreeY() {
        return degreeY;
    }

    public void setDegreeY(int degreeY) {
        this.degreeY = degreeY;
        invalidate();
    }

    public int getDegreeZ() {
        return degreeZ;
    }

    public void setDegreeZ(int degreeZ) {
        this.degreeZ = degreeZ;
        invalidate();
    }

    public int getDegreeX() {
        return degreeX;
    }

    public void setDegreeX(int degreeX) {
        this.degreeX = degreeX;
        invalidate();
    }
}
