package com.test.base.util;

import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by huangyong on 2018/10/25
 * WebView相关设置工具类
 */
public class WebViewUtils {
    /**
     * 设置WebView的相关属性
     *
     * @param webView WebView
     */
    public static void initWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        //显示字体大小，默认为16
        settings.setDefaultFontSize(16);
        //是否阻塞显示网页上的图片（默认为false）
        settings.setBlockNetworkImage(false);
        //是否自动加载网页上的图片（默认为true）
        settings.setLoadsImagesAutomatically(true);
        settings.setDefaultTextEncodingName("utf-8");//默认为utf-8
    }

    /**
     * 释放WebView资源，防止内存泄漏
     */
    public static void removeWebView(WebView webView) {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "UTF-8", null);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
    }
}