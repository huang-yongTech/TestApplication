package com.hy.presentation.activity;

import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;
import android.widget.Toast;

import com.hy.base.BaseActivity;
import com.hy.presentation.R;
import com.hy.presentation.fragment.HomeFragment;
import com.hy.base.util.FixMemLeak;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentTestActivity extends BaseActivity {
    @BindView(R.id.fragment_test_toolbar)
    Toolbar mToolbar;
    private boolean mIsExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_test);
        ButterKnife.bind(this);

        mToolbar.setTitle("Fragment回退栈测试");
        addFragment(R.id.fragment_test_frame_layout, new HomeFragment());
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
//            fragmentManager.popBackStack();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FixMemLeak.fixLeak(this);
    }
}
