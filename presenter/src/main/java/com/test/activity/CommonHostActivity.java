package com.test.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.test.R;
import com.test.fragment.HandlerRemoveFragment;
import com.test.fragment.HandlerThreadFragment;
import com.test.library.util.Constant;
import com.test.library.util.FixMemLeak;

@Route(path = "/presenter/commonHost")
public class CommonHostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_host);

        init();
    }

    private void init() {
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
            case Constant.TYPE_HANDLER_REMOVE:
                changeFragment(new HandlerRemoveFragment());
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
