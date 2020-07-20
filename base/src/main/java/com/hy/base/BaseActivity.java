package com.hy.base;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;

/**
 * Created by huangyong on 2017/8/21.
 * activity基类
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //把Activity添加到集合里面
        //使用5.0的activity切换动画（暂时不用）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
    }

    /**
     * 添加根fragment
     */
    protected void addFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(containerViewId, fragment);
        transaction.commit();
    }

    /**
     * 在Activity中对子Fragment进行替换
     */
    protected void replaceFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(containerViewId, fragment);
        transaction.commit();
    }

    /**
     * 切换子fragment并将其加入到回退栈中
     *
     * @param containerViewId fragment容器id
     * @param fragment        子fragment
     */
    protected void replaceFragmentToBackStackInAnimation(int containerViewId, BaseFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //fragment切换动画
        transaction.setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.fade_out,
                R.anim.fade_in, R.anim.slide_out_to_bottom);
        transaction.replace(containerViewId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
