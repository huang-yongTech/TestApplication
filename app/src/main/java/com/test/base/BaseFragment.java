package com.test.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.test.R;


/**
 * Created by huangyong on 2017/8/29.
 * Fragment基类，如果有Fragment包含子Fragment，则可以继承该基类
 */
public abstract class BaseFragment extends Fragment {

    /**
     * 添加根fragment
     */
    protected void addFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(containerViewId, fragment);
        transaction.commit();
    }

    /**
     * 在Fragment中进行子Fragment的替换
     */
    protected void replaceFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(containerViewId, fragment);
        transaction.commit();
    }

    /**
     * 切换子fragment并将其加入到回退栈中（包含fragment切换动画）
     *
     * @param containerViewId fragment容器id
     * @param fragment        子fragment
     */
    protected void replaceFragmentToBackStackInAnimation(int containerViewId, Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        //fragment切换动画
        transaction.setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.fade_out,
                R.anim.fade_in, R.anim.slide_out_to_bottom);
        transaction.replace(containerViewId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
