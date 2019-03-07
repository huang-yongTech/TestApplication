package com.test;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.test.util.CrashHandler;

public class AndroidApplication extends Application {

    private static AndroidApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

        //初始化组件路由
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }

    public static AndroidApplication getInstance() {
        return sInstance;
    }
}
