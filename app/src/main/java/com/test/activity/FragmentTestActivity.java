package com.test.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.test.R;
import com.test.base.BaseActivity;
import com.test.fragment.ChildFragment;
import com.test.fragment.HomeFragment;
import com.test.rxbus.RxBus;
import com.test.rxbus.Subscribe;
import com.test.util.Constant;

import me.yokeyword.fragmentation.SupportActivity;

public class FragmentTestActivity extends BaseActivity {
    private boolean mIsExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_test);

        RxBus.getDefault().register(this);

        addFragment(R.id.fragment_test_frame_layout, new HomeFragment());
    }

    @Subscribe(code = Constant.REPLACE_FRAGMENT)
    public void replaceFragment(String item) {
        int level = 0;
        level++;
        replaceFragmentToBackStackInAnimation(R.id.fragment_test_frame_layout, ChildFragment.newInstance("child级数" + level));
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int count = fragmentManager.getBackStackEntryCount();
        if (count == 0) {
            if (mIsExit) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, "再按一次退出当前activity", Toast.LENGTH_SHORT).show();
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
        } else {
            fragmentManager.popBackStack();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unRegister(this);
    }
}
