package com.test.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.test.library.util.SizeUtils;

/**
 * Created by huangyong on 2019/4/1
 * 时光轴分割线
 */
public class TimeAxisItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "TimeAxisItemDecoration";

    private Context mContext;
    private Paint mPaint;
    private int mDividerColor;
    private int mDividerHeight;
    private int mDividerMargin;

    public TimeAxisItemDecoration(Context context) {
        this.mContext = context;

        mDividerColor = Color.parseColor("#EF5350");
        mDividerHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, mContext.getResources().getDisplayMetrics());
        mDividerMargin = SizeUtils.dp2px(8);

        mPaint = new Paint();
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

        mPaint.setColor(mDividerColor);

        int left;
        int right;

        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft() + mDividerMargin;
            right = parent.getWidth() - parent.getPaddingRight() - mDividerMargin;
        } else {
            left = mDividerMargin;
            right = parent.getWidth() - mDividerMargin;
        }

        //childCount为当前屏幕可见的item的数量
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);

            int bottom = view.getTop();
            int top = bottom - mDividerHeight;

            canvas.drawRect(left, top, right, bottom, mPaint);
        }

        canvas.restore();
    }

    /**
     * 在itemview有背景的情况下，如何让最后一行的item没有偏移量
     * 记录：由于childCount获取的是当前在界面上显示的item数量，并不是RecyclerView所包含的所有item的数量，
     * 因此在绘制水平分割线时，最好设置top偏移量，然后判断itemPosition是否是第一个从而让第一个item没有top偏移量以达到不绘制的效果。
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int childCount = 0;
        int itemPosition = 0;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            childCount = layoutManager.getChildCount();
            itemPosition = layoutManager.getPosition(view);
        }

        int top = mDividerHeight;

        if (itemPosition == 0) {
            top = 0;
        }

        Log.i(TAG, "childCount: " + childCount);
        Log.i(TAG, "itemPosition: " + itemPosition);

        outRect.set(0, top, 0, 0);
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
}
