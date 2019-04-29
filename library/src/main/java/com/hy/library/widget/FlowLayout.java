package com.hy.library.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by huangyong on 2019/4/22
 * 流式布局实现
 */
public class FlowLayout extends ViewGroup {
    private static final String TAG = "FlowLayout";

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //父容器的测量宽度
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        //父容器的宽高
        int parentWidth = 0;
        int parentHeight = 0;

        //每一行的宽度和高度
        int lineWidth = 0;
        int lineHeight = 0;

        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = layoutParams.getMarginStart() + layoutParams.getMarginEnd() + child.getMeasuredWidth();
            int childHeight = layoutParams.topMargin + layoutParams.bottomMargin + child.getMeasuredHeight();

            //如果超过了一行（这里父容器的宽度需要考虑paddingStart和paddingEnd，如果设置了这两个属性的话）
            if (lineWidth + childWidth > widthSize - getPaddingStart() - getPaddingEnd()) {
                //父容器宽度
                parentWidth = Math.max(parentWidth, lineWidth);
                parentHeight += lineHeight;

                //重新设置换行后的新行宽高
                lineWidth = childWidth;
                lineHeight = childHeight;
            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }

            //处理最后一行
            if (i == childCount - 1) {
                //最后一行的宽度肯定小于最大宽度，因此注释掉的代码可有可无
//                parentWidth = Math.max(parentWidth, lineWidth);
                parentHeight += lineHeight;
            }
        }

        int width = getDefaultSize(widthMeasureSpec, parentWidth + getPaddingStart() + getPaddingEnd(),
                ViewCompat.getMinimumHeight(this));
        int height = getDefaultSize(heightMeasureSpec, parentHeight + getPaddingTop() + getPaddingBottom(),
                ViewCompat.getMinimumHeight(this));

        setMeasuredDimension(width, height);
    }

    private int getDefaultSize(int spec, int desireSize, int min) {
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
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //布局的位置
        int left = getPaddingStart();
        int top = getPaddingTop();

        //每一行的宽度，控制换行
        int lineWidth = 0;
        int lineHeight = 0;

        //父容器的最大宽度（需要考虑paddingStart和paddingEnd情况，如果设置了这两个属性的话）
        int parentWidth = getMeasuredWidth() - getPaddingStart() - getPaddingEnd();

        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + layoutParams.getMarginStart() + layoutParams.getMarginEnd();
            int childHeight = child.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;

            if (lineWidth + childWidth > parentWidth) {
                left = getPaddingStart();
                top += lineHeight;

                lineWidth = childWidth;
                lineHeight = childHeight;
            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }

            int childLeft = left + layoutParams.getMarginStart();
            int childTop = top + layoutParams.topMargin;
            int childRight = childLeft + child.getMeasuredWidth();
            int childBottom = childTop + child.getMeasuredHeight();

            child.layout(childLeft, childTop, childRight, childBottom);

            //每布局完一个子view，将left加上子view的宽度
            left += childWidth;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
