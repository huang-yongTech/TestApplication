package com.test;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.squareup.leakcanary.LeakCanary;
import com.test.library.util.CrashHandler;

public class AndroidApplication extends Application {

    private static AndroidApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
        setupCrashHandler();

        //初始化组件路由
        setupARouter();

        //内存泄漏
        setupLeakCanary();
    }

    /**
     * APP异常崩溃处理
     */
    private void setupCrashHandler() {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

    /**
     * 组件路由
     */
    private void setupARouter() {
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }

    /**
     * 内存泄漏
     */
    private void setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }

    public static AndroidApplication getInstance() {
        return sInstance;
    }
}
