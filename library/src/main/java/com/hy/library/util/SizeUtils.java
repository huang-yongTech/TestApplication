package com.hy.library.util;

import android.content.res.Resources;
import android.widget.Spinner;

/**
 * Created by huangyong on 2017/8/23.
 * 尺寸转换工具类
 */
public final class SizeUtils {
    private SizeUtils() {
    }

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Value of sp to value of px.
     *
     * @param spValue The value of sp.
     * @return value of px
     */
    public static int sp2px(final float spValue) {
        final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取spinner高度并设置弹出框竖直偏移量
     *
     * @param spinner 传入的spinner组件
     */
    public static void setSpinnerDropDown(final Spinner spinner) {
        spinner.post(new Runnable() {
            @Override
            public void run() {
                int dropDownOffset = spinner.getHeight();
                spinner.setDropDownVerticalOffset(dropDownOffset);
            }
        });
    }
}
