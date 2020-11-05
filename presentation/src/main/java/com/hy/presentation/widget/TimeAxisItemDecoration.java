package com.hy.presentation.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.View;

import com.hy.library.util.SizeUtils;
import com.hy.presentation.R;

/**
 * Created by huangyong on 2019/4/1
 * 时光轴分割线
 */
public class TimeAxisItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "TimeAxisItemDecoration";

    private Paint mPaint;
    private Rect mTextRect;

    private int mDividerColor;
    private int mDividerHeight;
    private int mDividerMargin;
    private int mTextSize;

    private int mOffsetLeft;

    private Bitmap mBitmapIcon;

    public TimeAxisItemDecoration(Context context) {
        mDividerColor = Color.parseColor("#EF5350");
        mDividerHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());
        mDividerMargin = SizeUtils.dp2px(8);
        mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, context.getResources().getDisplayMetrics());
        mOffsetLeft = SizeUtils.dp2px(100);

        mBitmapIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.folder_selected);

        mPaint = new Paint();
        mTextRect = new Rect();
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        drawVertical(c, parent);
    }

    /**
     * 绘制水平分割线
     */
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();

        int left;
        int right;

        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft() + mDividerMargin + mOffsetLeft;
            right = parent.getWidth() - parent.getPaddingRight() - mDividerMargin;
        } else {
            left = mDividerMargin + mOffsetLeft;
            right = parent.getWidth() - mDividerMargin;
        }


        //childCount为当前屏幕可见的item的数量
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);

            drawHorizontalDivider(canvas, view, left, right);
            drawLeft(canvas, parent, view);
        }

        canvas.restore();
    }

    /**
     * 绘制水平分割线
     */
    private void drawHorizontalDivider(Canvas canvas, View view, int left, int right) {
        //绘制ItemView分割线
        int bottom = view.getTop();
        int top = bottom - mDividerHeight;
        mPaint.setColor(mDividerColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(left, top, right, bottom, mPaint);
    }

    /**
     * 绘制左侧
     */
    private void drawLeft(Canvas canvas, RecyclerView parent, View view) {
        int radius = SizeUtils.dp2px(20);

        //绘制左侧标签
        int top = view.getTop() - mDividerHeight;
        int leftDown = view.getBottom();
        int centerX = mOffsetLeft / 2;
        int centerY = (top + leftDown) / 2;

        mPaint.setStrokeWidth(SizeUtils.dp2px(2));

        //获取实际的item位置应该采用这种方法
        int itemPosition = 0;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            itemPosition = layoutManager.getPosition(view);
        }

        if (itemPosition != 0) {
            mPaint.setColor(ContextCompat.getColor(parent.getContext(), R.color.black_light));
            mPaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
            canvas.drawLine(centerX, top, centerX, centerY - radius, mPaint);
        }

        //获取RecyclerView所有子元素的数量应该采用layoutManager.getItemCount()方法
        if (itemPosition != layoutManager.getItemCount() - 1) {
            mPaint.setColor(ContextCompat.getColor(parent.getContext(), R.color.black_light));
            mPaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
            canvas.drawLine(centerX, centerY + radius, centerX, view.getBottom(), mPaint);
        }

        if (itemPosition == 0) {
            mPaint.setColor(ContextCompat.getColor(parent.getContext(), R.color.red));
        } else {
            mPaint.setColor(ContextCompat.getColor(parent.getContext(), R.color.black_light));
        }

        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(centerX, centerY, radius, mPaint);

        String text = String.valueOf(itemPosition);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(mTextSize);

        mPaint.getTextBounds(text, 0, text.length(), mTextRect);
        canvas.drawText(text, centerX - ((mTextRect.right + mTextRect.left) >> 1),
                centerY - ((mTextRect.bottom + mTextRect.top) >> 1), mPaint);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int itemPosition;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        int childCount = parent.getChildCount();
        if (childCount == 0) {
            return;
        }

        itemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        View child = parent.findViewHolderForAdapterPosition(itemPosition).itemView;

//        for (int i = 0; i < childCount; i++) {
//            View view = parent.getChildAt(i);
//            itemPosition = layoutManager.getPosition(view);
//
//            int index = parent.getChildAdapterPosition(view);
//            if (index < 3) {
//                //在item上面绘制装饰，因为view.getTop()随着RecyclerView的滑动是实时变化的，并且每个view.getTop()的值都不一样
////                c.drawBitmap(mBitmapIcon, view.getPaddingLeft() + mOffsetLeft, view.getTop(), mPaint);
//                //item顶部悬浮绘制，因为所有view.getPaddingTop()的值是一样的，这样就会一直绘制在顶部，这也就是顶部悬浮效果
//                c.drawBitmap(mBitmapIcon, view.getPaddingLeft() + mOffsetLeft, view.getPaddingTop(), mPaint);
//            }
//        }

        c.drawText("这是 " + itemPosition + " 个节点", child.getPaddingLeft() + mOffsetLeft,
                child.getPaddingTop() + (float) child.getHeight() / 5, mPaint);
    }

    /**
     * 在itemview有背景的情况下，如何让最后一行的item没有偏移量
     * 记录：由于childCount获取的是当前在界面上显示的item数量，并不是RecyclerView所包含的所有item的数量，
     * 因此在绘制水平分割线时，最好设置top偏移量，然后判断itemPosition==0是否成立从而让第一个item没有top偏移量以达到不绘制的效果。
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int itemPosition = 0;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            itemPosition = layoutManager.getPosition(view);
        }

        int top = mDividerHeight;

        if (itemPosition == 0) {
            top = 0;
        }

        outRect.set(mOffsetLeft, top, 0, 0);
    }

    public int getDividerColor() {
        return mDividerColor;
    }

    public void setDividerColor(int dividerColor) {
        this.mDividerColor = dividerColor;
    }

    public int getDividerHeight() {
        return mDividerHeight;
    }

    public void setDividerHeight(int dividerHeight) {
        this.mDividerHeight = dividerHeight;
    }

    public int getDividerMargin() {
        return mDividerMargin;
    }

    public void setDividerMargin(int dividerMargin) {
        this.mDividerMargin = dividerMargin;
    }

    public int getOffsetLeft() {
        return mOffsetLeft;
    }

    public void setmOffsetLeft(int offsetLeft) {
        this.mOffsetLeft = offsetLeft;
    }
}
