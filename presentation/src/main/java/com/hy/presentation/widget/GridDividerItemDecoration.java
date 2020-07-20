package com.hy.presentation.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

/**
 * Created by huangyong on 2019/3/28
 * 网格分割线
 */
public class GridDividerItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "DividerItem";
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private Drawable mDivider;
    private int mDividerWidth;
    private int mDividerHeight;

    /**
     * Creates a divider {@link RecyclerView.ItemDecoration} that can be used with a
     * {@link LinearLayoutManager}.
     *
     * @param context Current context, it will be used to access resources.
     */
    public GridDividerItemDecoration(@NonNull Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        if (mDivider == null) {
            Log.w(TAG, "@android:attr/listDivider was not set in the theme used for this "
                    + "GridItemDecoration. Please set that attribute all call setDrawable()");
        } else {
            mDividerWidth = mDivider.getIntrinsicWidth();
            mDividerHeight = mDivider.getIntrinsicHeight();
        }
        a.recycle();
    }

    /**
     * Sets the {@link Drawable} for this divider.
     *
     * @param drawable Drawable that should be used as a divider.
     */
    public void setDrawable(@NonNull Drawable drawable) {
        mDivider = drawable;
        mDividerWidth = mDivider.getIntrinsicWidth();
        mDividerHeight = mDivider.getIntrinsicHeight();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null || mDivider == null) {
            return;
        }

        drawVertical(c, parent);
        drawHorizontal(c, parent);
    }

    /**
     * 绘制水平分割线
     */
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        final int spanCount = getSpanCount(parent);
        final int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();

            final int top = child.getTop() - layoutParams.topMargin;
            int bottom = child.getBottom() + layoutParams.bottomMargin;
            final int left = child.getRight() + layoutParams.rightMargin;
            int right = left + mDividerWidth;

            if (isLastColumn(parent, i, spanCount, childCount)) {
                right = left;
            }

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
    }

    /**
     * 绘制垂直分割线
     */
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        final int spanCount = getSpanCount(parent);
        final int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();

            final int left = child.getLeft() - layoutParams.leftMargin;
            int right = child.getRight() + layoutParams.rightMargin;
            final int top = child.getBottom() + layoutParams.bottomMargin;
            int bottom = top + mDividerHeight;

            //不绘制最后一行
            if (isLastRow(parent, i, spanCount, childCount)) {
                bottom = top;
            }

            //如果不是最后一列
            if (!isLastColumn(parent, i, spanCount, childCount)) {
                right = child.getRight() + layoutParams.rightMargin + mDividerWidth;
            }

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (mDivider == null) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }

        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();

        int left;
        int right;
        int top = 0;
        int bottom = mDividerHeight;

        //计算每列相邻item之间divider所占用的宽度
        int eachOffset = (spanCount - 1) * mDividerWidth / spanCount;
        //计算divider实际设置的宽度和eachOffset之间的差值
        int dl = mDividerWidth - eachOffset;

        //计算每个item的左右偏移量，使得每列各个item所占的宽度能均匀分布
        left = itemPosition % spanCount * dl;
        right = eachOffset - left;

        if (isLastRow(parent, itemPosition, spanCount, childCount)) {
            bottom = 0;
        }

        //由于上面的代码已经计算了item的左右偏移量，所以这里的判断可有可无，保留主要是为了学习下如何判断是否最后一列
//        if (isLastColumn(parent, itemPosition, spanCount, childCount)) {
//            right = 0;
//        }

        outRect.set(left, top, right, bottom);
    }

    /**
     * 是否是最后一列
     */
    private boolean isLastColumn(RecyclerView parent, int itemPosition, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            //当前元素位置加1再与列数求余，值为0，则表示是最后一列，加1是因为下标是从0开始的
            return (itemPosition + 1) % spanCount == 0;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                return (itemPosition + 1) % spanCount == 0;
            } else {
                childCount = childCount - childCount % spanCount;
                return itemPosition >= childCount;
            }
        }

        return false;
    }

    /**
     * 是否是最后一行
     */
    private boolean isLastRow(RecyclerView parent, int itemPosition, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if (childCount % spanCount == 0) {
                childCount = childCount - spanCount;
            } else {
                childCount = childCount - childCount % spanCount;
            }
            //判断当前位置是否是最后一行
            return itemPosition >= childCount;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                return itemPosition >= childCount;
            } else {
                return (itemPosition + 1) % spanCount == 0;
            }
        }

        return false;
    }

    private int getSpanCount(RecyclerView parent) {
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }

        return spanCount;
    }
}
