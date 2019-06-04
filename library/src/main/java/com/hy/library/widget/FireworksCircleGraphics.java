package com.hy.library.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;

import com.hy.library.util.SizeUtils;

import java.security.SecureRandom;
import java.util.Random;

/**
 * 烟花圆环控制类
 * <p>
 * 速度相关参数对应时间单位为每帧（1/60秒）
 * <p>
 * 日志耗性能所以就直接注释了
 * <p>
 * Created by chenhao on 2017/10/15.
 */
class FireworksCircleGraphics {
    private static final String TAG = "FireworksCircleGraphics";

    /**
     * 圆环旋转速度 degree
     **/
    private static final int ROTATE_RATE = 3;
    /**
     * 圆环半径与 Canvas 宽度之比
     **/
    private static final float RADIUS_SCALE = 0.32f;

    /**
     * 线条数目
     **/
    private static final int LINE_AMOUNT = 15;
    /**
     * 线条弧长 0-360 degree
     **/
    private static final int LINE_DEGREE = 345;
    /**
     * 线条大小 dp
     **/
    private static final float LINE_SIZE = 0.5f;
    /**
     * 线条半径偏离范围最大值 dp（越大显得越宽）
     **/
    private static final float LINE_MAX_DR = 4f;
    /**
     * 线条圆心偏离范围最大值 dp （越大显得越宽）
     **/
    private static final float LINE_MAX_DC = 4f;
    /**
     * 线条弧长变化速率范围（绝对值）dp
     **/
    private static final float LINE_MAX_CHANGE_RATE = 0.015f;
    /**
     * 线条弧长变化速率衰减速率 dp
     **/
    private static final float LINE_DECAY_RATE = LINE_MAX_CHANGE_RATE / 180;
    /**
     * 线条边界反向力比率
     **/
    private static final float LINE_SIDE_RATIO = LINE_DECAY_RATE * 10;
    /**
     * 线条随机摆动触发间隔 frame
     **/
    private static final int LINE_RANDOM_AFTER_FRAMES = 60;

    /**
     * 星星数目
     **/
    private static final int STAR_AMOUNT = 30;
    /**
     * 星星大小 dp
     **/
    private static final float STAR_SIZE = 8f;
    /**
     * 星星逃离 X 轴最大速度 dp
     **/
    private static final float STAR_MAX_VX = 2.5f;
    /**
     * 星星逃离 Y 轴最大速度 dp
     **/
    private static final float STAR_MAX_VY = 2.5f;
    /**
     * 星星速度衰减速率 dp
     **/
    private static final float STAR_DECAY_RATE = 0.003f;
    /**
     * 星星速度衰减常量 dp
     **/
    private static final float STAR_DECAY_RATE_CONST = 0.001f;
    /**
     * 星星消失临界距离 dp
     **/
    private static final float STAR_DISAPPEAR_DISTANCE = 60f;
    /**
     * 星星消失临界亮度 dp
     **/
    private static final float STAR_DISAPPEAR_ALPHA = 0.05f;


    /**
     * 线条半径偏离范围最大值 px（约为圆环宽度 / 2）
     **/
    private float mLineMaxDxy;
    /**
     * 线条圆心偏离范围最大值 px
     **/
    private float mLineMaxDr;
    /**
     * 线条弧长变化速率范围（绝对值）px
     **/
    private float mLineMaxChangeRate;
    /**
     * 线条弧长变化速率衰减速率 px
     **/
    private float mLineDecayRate;

    /**
     * 星星逃离 X 轴最大速度 px
     **/
    private float mStarMaxVx;
    /**
     * 星星逃离 Y 轴最大速度 px
     **/
    private float mStarMaxVy;
    /**
     * 星星速度衰减速率 px
     **/
    private float mStarDecayRate;
    /**
     * 星星速度衰减常量 px
     **/
    private float mStarDecayRateConst;
    /**
     * 星星消失临界距离 px
     **/
    private float mStarDisappearDistance;

    private int mEndColor;
    private int mStartColor;

    private Paint mLinePaint;
    private Paint mStarPaint;
    private Random mRandom;

    /**
     * 旋转度数 setter and getter
     */
    private float rotateDegree;

    /**
     * 对象复用，减少 GC
     **/
    private int mWidth = 0;
    private int mHeight = 0;
    private int mCircleX = 0;
    private int mCircleY = 0;
    private RectF mLineRectF;
    private boolean mNeedRefresh = false;

    //调试用，用于追踪某个点
//    private static int traceCount = 0;

    /**
     * 线条参数列表
     **/
    private ArcLineArgument[] mArcLineArgumentList;
    /**
     * 星星参数列表
     **/
    private StarArgument[] mStarArgumentList;

    FireworksCircleGraphics() {
        //线条初始化
        mLineMaxDxy = SizeUtils.dp2px(LINE_MAX_DR);
        mLineMaxDr = SizeUtils.dp2px(LINE_MAX_DC);
        mLineMaxChangeRate = SizeUtils.dp2px(LINE_MAX_CHANGE_RATE);
        mLineDecayRate = SizeUtils.dp2px(LINE_DECAY_RATE);
        mLineRectF = new RectF(0, 0, 0, 0);

        //星星初始化
        float starSize = SizeUtils.dp2px(STAR_SIZE);
        mStarMaxVx = SizeUtils.dp2px(STAR_MAX_VX);
        mStarMaxVy = SizeUtils.dp2px(STAR_MAX_VY);
        mStarDecayRate = SizeUtils.dp2px(STAR_DECAY_RATE);
        mStarDecayRateConst = SizeUtils.dp2px(STAR_DECAY_RATE_CONST);
        mStarDisappearDistance = SizeUtils.dp2px(STAR_DISAPPEAR_DISTANCE);

        mStartColor = Color.WHITE;
        mEndColor = Color.TRANSPARENT;

        //随机数引擎
        mRandom = new Random(new SecureRandom().nextInt());

        //存储将要绘制的线条集合
        mArcLineArgumentList = new ArcLineArgument[LINE_AMOUNT];
        for (int i = 0; i < mArcLineArgumentList.length; i++) {
            mArcLineArgumentList[i] = new ArcLineArgument();
        }

        //存储将要绘制的星星集合
        mStarArgumentList = new StarArgument[STAR_AMOUNT];
        for (int i = 0; i < mStarArgumentList.length; i++) {
            mStarArgumentList[i] = new StarArgument();
        }

        //线条画笔
        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setStrokeWidth(SizeUtils.dp2px(LINE_SIZE));
        mLinePaint.setAntiAlias(true);

        //星星画笔
        mStarPaint = new Paint();
        mStarPaint.setStrokeCap(Paint.Cap.ROUND);
        mStarPaint.setStrokeWidth(starSize);
    }

    void draw(Canvas canvas) {
        if (canvas.getHeight() != mHeight || canvas.getWidth() != mWidth) {
            mNeedRefresh = true;
            mHeight = canvas.getHeight();
            mWidth = canvas.getWidth();
        }

        if (mNeedRefresh) {
            mCircleX = (int) (mWidth * 0.5f);
            mCircleY = (int) (mHeight * 0.5f);
            SweepGradient lineSweepGradient = new SweepGradient(mCircleX, mCircleY, mEndColor, mStartColor);
            mLinePaint.setShader(lineSweepGradient);
            mNeedRefresh = false;

            //初始化星星集合，使得每一次轻微的旋转星星都能从初始位置向旋转的反方向逃逸
            for (StarArgument argument : mStarArgumentList) {
                argument.reset();
            }
        }

        //设置圆弧半径
        int radius = (int) (canvas.getWidth() * RADIUS_SCALE);

        //开始绘制
        canvas.save();

        canvas.rotate(180 + rotateDegree, mCircleX, mCircleY);

        //绘制旋转的圆弧
        for (ArcLineArgument argument : mArcLineArgumentList) {

            float dx = argument.dx;
            float dy = argument.dy;
            float dr = argument.dr;
            mLineRectF.set(
                    mCircleX - radius - dr - dx,
                    mCircleY - radius - dr - dy,
                    mCircleX + radius + dr + dx,
                    mCircleY + radius + dr + dy);

            // 模拟倾斜效果，使得绘制的各个圆弧起始弧度向后有一定的偏差，最高偏差不超过15度
            float dAngle = (-dx) < -(360 - LINE_DEGREE) ? (360 - LINE_DEGREE) : (-dx);
            canvas.drawArc(
                    mLineRectF,
                    360 - LINE_DEGREE + dAngle,
                    LINE_DEGREE,
                    false, mLinePaint);
        }

        //绘制星星
        for (StarArgument argument : mStarArgumentList) {
            float dx = argument.dx;
            float dy = argument.dy;

            //设置将要绘制的星星的透明度
            int alphaMask = ((int) (argument.alpha * 0xff)) << 24;
            int transparentColor = (mStartColor & 0x00ffffff) + alphaMask;
            mStarPaint.setColor(transparentColor);
            canvas.drawPoint(mCircleX + radius + dx, mCircleY + dy, mStarPaint);
        }
        //绘制起始星星的位置（不透明）
        mStarPaint.setColor(mStartColor);
        canvas.drawPoint(mCircleX + radius, mCircleY, mStarPaint);

        canvas.restore();
    }

    /**
     * 计算下一帧参数
     */
    void next() {
//        rotateDegree = (rotateDegree + ROTATE_RATE) % 360;
        for (ArcLineArgument argument : mArcLineArgumentList) {
            argument.next();
        }
        for (StarArgument argument : mStarArgumentList) {
            argument.next();
        }
    }

    /**
     * 圆弧线条位置数据，有惯性和随机属性。半径随机效果一般，最后没用到
     * 加速度由 衰减值，随机值，边界反向力三个力合成，并影响偏移值
     */
    private class ArcLineArgument {
        /**
         * 圆心 X 轴偏移值
         **/
        float dx;
        /**
         * 圆心 Y 轴偏移值
         **/
        float dy;
        /**
         * 圆半径 r 偏移值
         **/
        float dr;
        /**
         * 圆心 X 轴偏移速度
         **/
        float vx;
        /**
         * 圆心 Y 轴偏移速度
         **/
        float vy;
        /**
         * 圆半径 r 轴偏移速度
         **/
        float vr;
        /**
         * 圆心 X 轴偏移加速度
         **/
        float ax;
        /**
         * 圆心 Y 轴偏移加速度
         **/
        float ay;
        /**
         * 圆半径 r 轴偏移加速度
         **/
        float ar;

        /**
         * 帧数计算，用于施加随机力
         **/
        int frameCount;

        ArcLineArgument() {
            dx = nextSignedFloat() * mLineMaxDr;
            dy = nextSignedFloat() * mLineMaxDr;
            dr = nextSignedFloat() * mLineMaxDxy;

            vx = 0;
            vy = 0;
            vr = 0;

            ax = 0;
            ay = 0;
            ar = 0;

            frameCount = mRandom.nextInt() % LINE_RANDOM_AFTER_FRAMES;
        }

        /**
         * 计算下一帧（每秒60帧）的各项参数值
         */
        void next() {
            // a = 衰减值 + 随机值 + 边界反向力
            float newAx = (vx > 0 ? -1 : 1) * mLineDecayRate +
                    (frameCount == 0 ? (nextSignedFloat() * mLineMaxChangeRate) : 0) +
                    (Math.abs(dx) > mLineMaxDxy ? (-LINE_SIDE_RATIO * dx / mLineMaxDxy) : 0);

            float newAy = (vy > 0 ? -1 : 1) * mLineDecayRate +
                    (frameCount == 0 ? (nextSignedFloat() * mLineMaxChangeRate) : 0) +
                    (Math.abs(dy) > mLineMaxDxy ? (-LINE_SIDE_RATIO * dy / mLineMaxDxy) : 0);

            float newAr = (vr > 0 ? -1 : 1) * mLineDecayRate +
                    (frameCount == 0 ? (nextSignedFloat() * mLineMaxChangeRate) : 0) +
                    (Math.abs(dr) > mLineMaxDr ? (-LINE_SIDE_RATIO * dr / mLineMaxDr) : 0);

            float newVx = vx + (ax + newAx) / 2;
            float newVy = vy + (ax + newAy) / 2;
            float newVr = vr + (ax + newAr) / 2;

            dx += (vx + newVx) / 2;
            dy += (vy + newVy) / 2;
            dr += (vr + newVr) / 2;

//            if (traceCount++ % LINE_AMOUNT == 0) {
//                LogUtils.d(String.format(java.util.Locale.CHINA, "ax=% 2.2f\tvx=% 2.2f\tdx=% 2.2f\tdr=% 2.2f\tfr=% 5d\tsr=% 2.2f\t",
//                        ax, vx, dx,
//                        -LINE_DECAY_RATE * vx * Math.abs(vx),
//                        frameCount,
//                        -LINE_SIDE_RATIO * dx * Math.abs(dx)));
//            }

            ax = newAx;
            ay = newAy;
            ar = newAr;
            vx = newVx;
            vy = newVy;
//            vr = newVr;
            frameCount = ++frameCount % LINE_RANDOM_AFTER_FRAMES;
        }
    }


    /**
     * 星星位置数据，有惯性和随机属性
     * 具有初始速度
     * 加速度由 衰减值（模拟空气阻力）组成
     * 星星透明度与距离相关
     * 速度过低或距离过远则消失并重新出发
     */
    private class StarArgument {
        /**
         * 距离源点 X 轴偏移
         **/
        float dx;
        /**
         * 距离源点 Y 轴偏移
         **/
        float dy;
        /**
         * 逃离源点 X 轴速度
         **/
        float vx;
        /**
         * 逃离源点 Y 轴速度
         **/
        float vy;
        /**
         * 逃离源点 X 轴加速度
         **/
        double ax;
        /**
         * 逃离源点 Y 轴加速度
         **/
        double ay;
        /**
         * 星星透明度
         **/
        float alpha;

        StarArgument() {
            reset();
        }

        private void reset() {
            dx = 0;
            dy = 0;

            vx = nextSignedFloat() * mStarMaxVx;
            // Y 轴因旋转存在初始速度
            vy = mRandom.nextFloat() * mStarMaxVy
                    - 2 * (float) Math.PI * mWidth * RADIUS_SCALE * ((float) ROTATE_RATE / 360);

            ax = 0;
            ay = 0;

            alpha = 1;
        }

        //生成下一个星星
        void next() {
            ax = -(vx * Math.abs(vx) * mStarDecayRate - mStarDecayRateConst);
            ay = -(vy * Math.abs(vy) * mStarDecayRate - mStarDecayRateConst);
            if (ax < 0) {
                ax = 0;
            }
            if (ay < 0) {
                ay = 0;
            }

            dx += vx / 2;
            vx += ax;
            dx += vx / 2;

            dy += vy / 2;
            vy += ay;
            dy += vy / 2;

            //根据当前星星的逃逸距离与临界消失距离比来计算星星透明度
            alpha = 1 - (float) Math.sqrt(dx * dx + dy * dy) / mStarDisappearDistance;

//            if (traceCount++ % STAR_AMOUNT == 0) {
//                LogUtils.i(String.format(Locale.CHINA, "ax=% 2.2f\tvx=% 2.2f\tdx=% 2.2f\tay=% 2.2f\tvy=% 2.2f\tdy=% 2.2f\talpha=%2.2f",
//                        ax, vx, dx, ay, vy, dy, alpha));
//                if (alpha < STAR_DISAPPEAR_ALPHA) {
//                    LogUtils.i("reset");
//                }
//            }

            //如果当前透明度小于星星消失临界透明度则重置
            if (alpha < STAR_DISAPPEAR_ALPHA) {
                reset();
            }
        }
    }

    private float nextSignedFloat() {
        return (mRandom.nextBoolean() ? 1 : -1) * mRandom.nextFloat();
    }

    public float getRotateDegree() {
        return rotateDegree;
    }

    public void setRotateDegree(float rotateDegree) {
        this.rotateDegree = rotateDegree;
    }
}
