package com.test.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 网格布局分割线
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "DividerItem";
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private Drawable mDivider;
    private int mDividerWidth;
    private int mDividerHeight;

    private final Rect mBounds = new Rect();

    public GridItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        if (mDivider == null) {
            Log.w(TAG, "@android:attr/listDivider was not set in the theme used for this "
                    + "GridItemDecoration. Please set that attribute all call setDrawable()");
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

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        final int spanCount = getSpanCount(parent);
        final int childCount = parent.getChildCount();

        canvas.save();
        final int left;
        int right;
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            int bottom = mBounds.bottom + Math.round(child.getTranslationY());
            final int top = bottom - mDividerHeight;

            //不绘制最后一行
            if (isLastRow(parent, i, spanCount, childCount)) {
                bottom = top;
            }

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
        canvas.restore();
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        final int spanCount = getSpanCount(parent);
        final int childCount = parent.getChildCount();

        canvas.save();
        final int top;
        final int bottom;
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top,
                    parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getLayoutManager().getDecoratedBoundsWithMargins(child, mBounds);
            int right = mBounds.right + Math.round(child.getTranslationX());
            final int left = right - mDividerWidth;

            if (isLastColumn(parent, i, spanCount, childCount)) {
                right = left;
            }

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
        canvas.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
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

        int eachWidth = (spanCount - 1) * mDividerHeight / spanCount;
        int dl = mDividerHeight - eachWidth;

        left = itemPosition % spanCount * dl;
        right = eachWidth - left;

        if (isLastRow(parent, itemPosition, spanCount, childCount)) {
            bottom = 0;
        }

        if (isLastColumn(parent, itemPosition, spanCount, childCount)) {
            right = 0;
        }

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
