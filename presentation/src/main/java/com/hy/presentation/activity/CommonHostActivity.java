package com.hy.presentation.activity;

import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.hy.presentation.R;
import com.hy.presentation.fragment.WebViewFragment;
import com.hy.presentation.fragment.HandlerThreadFragment;
import com.hy.base.Constant;
import com.hy.base.util.FixMemLeak;

@Route(path = "/presentation/commonHost")
public class CommonHostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_host);

        init();
    }

    private void init() {
        //这里采用传统的方式获取ARouter传递过来的参数是因为我们知道ARouter传值的方式
        //当在团队采用模块化或者组件化开发时，我们往往并不知道ARouter的传值方式，
        //在这种情况下，我们应该通过注解的方式来获取传值参数
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(Constant.TYPE_COMMON_BUNDLE);

        if (bundle == null) {
            return;
        }

        String type = bundle.getString(Constant.TYPE);
        if (null == type) {
            return;
        }

        switch (type) {
            case Constant.TYPE_HANDLER_THREAD:
                changeFragment(new HandlerThreadFragment());
                break;
            case Constant.TYPE_WEB_VIEW:
                changeFragment(new WebViewFragment());
                break;
            case Constant.TYPE_RECYCLER_VIEW:
                Fragment fragment = (Fragment) ARouter.getInstance().build("/presentation/recyclerViewFragment").navigation();
                changeFragment(fragment);
                break;
            case Constant.TYPE_RECYCLER_VIEW_CACHE:
                Fragment cacheFragment = (Fragment) ARouter.getInstance().build("/presentation/recyclerViewCacheFragment").navigation();
                changeFragment(cacheFragment);
                break;
            case Constant.TYPE_HEN_CODER_PRACTICE:
                Fragment praiseFragment = (Fragment) ARouter.getInstance().build("/presentation/henCoderFragment").navigation();
                changeFragment(praiseFragment);
                break;
            case Constant.TYPE_POLYGON:
                Fragment polygonFragment = (Fragment) ARouter.getInstance().build("/presentation/spiderFragment").navigation();
                changeFragment(polygonFragment);
                break;
            case Constant.TYPE_CROP_PIC:
                Fragment cropPicFragment = (Fragment) ARouter.getInstance().build("/presentation/cropPicFragment").navigation();
                changeFragment(cropPicFragment);
                break;
            case Constant.TYPE_ADVANCE_ANIM:
                Fragment advanceAnimFragment = (Fragment) ARouter.getInstance().build("/presentation/advanceAnimFragment").navigation();
                changeFragment(advanceAnimFragment);
                break;
            case Constant.TYPE_FRAGMENT_LAZY:
                Fragment lazyFragment = (Fragment) ARouter.getInstance().build("/presentation/LazyHomeFragment").navigation();
                changeFragment(lazyFragment);
                break;
        }
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.common_host_fragment_container, fragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FixMemLeak.fixLeak(this);
    }
}
