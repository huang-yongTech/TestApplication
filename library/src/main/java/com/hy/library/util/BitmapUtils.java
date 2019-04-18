package com.hy.library.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by huangyong on 2019/4/12
 * Bitmap相关工具类
 */
public class BitmapUtils {
    private static final String TAG = "BitmapUtils";

    /**
     * 解析适合组件大小的图片
     *
     * @param resources Resources
     * @param redId     图片资源id
     * @param reqWidth  要求宽度
     * @param reqHeight 要求高度
     * @return 符合要求的图片
     */
    public static Bitmap decodeSampleBitmapFromResource(Resources resources, int redId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, redId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(resources, redId, options);
    }

    /**
     * 根据要求的宽高获取图片采样率
     *
     * @param options   BitmapFactory.Options
     * @param reqWidth  要求宽度
     * @param reqHeight 要求高度
     * @return 采样率
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;

        int inSampleSize = 1;

        if (width > reqWidth || height > reqHeight) {
            final int halfWidth = width / 2;
            final int halfHeight = height / 2;

            Log.i(TAG, "calculateInSampleSize: ");

            Log.i(TAG, "halfWidth: " + halfWidth);
            Log.i(TAG, "halfHeight: " + halfHeight);
            Log.i(TAG, "reqWidth: " + reqWidth);
            Log.i(TAG, "reqHeight: " + reqHeight);

            while ((halfWidth / inSampleSize) >= reqWidth && (halfHeight / inSampleSize) >= reqHeight) {
                Log.i(TAG, "inSampleSize: " + inSampleSize);
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
