package com.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.test.R;
import com.test.base.Constant;
import com.test.base.util.FixMemLeak;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mToolbar.setTitle("Room测试");
        setSupportActionBar(mToolbar);

    }

    @OnClick({R.id.main_fragment_test_btn, R.id.main_realm_test_btn, R.id.main_handler_thread_btn,
            R.id.main_web_view_btn, R.id.main_other_module_btn,R.id.main_recycler_view_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_fragment_test_btn:
                startActivity(new Intent(this, FragmentTestActivity.class));
                break;
            case R.id.main_realm_test_btn:
                 startActivity(new Intent(this, RealmTestActivity.class));
                break;
            case R.id.main_handler_thread_btn:
                Bundle commonBundle = new Bundle();
                commonBundle.putString(Constant.TYPE, Constant.TYPE_HANDLER_THREAD);
                ARouter.getInstance()
                        .build("/presenter/commonHost")
                        .withBundle(Constant.TYPE_COMMON_BUNDLE, commonBundle)
                        .navigation();
                break;
            case R.id.main_web_view_btn:
                Bundle handlerBundle = new Bundle();
                handlerBundle.putString(Constant.TYPE, Constant.TYPE_HANDLER_REMOVE);
                ARouter.getInstance()
                        .build("/presenter/commonHost")
                        .withBundle(Constant.TYPE_COMMON_BUNDLE, handlerBundle)
                        .navigation();
                break;
            case R.id.main_other_module_btn:
                Bundle otherBundle = new Bundle();
                otherBundle.putString("url", "这是主界面传递过来的Url");
                otherBundle.putString("title", "这是主界面传递过来的Title");
                ARouter.getInstance()
                        .build("/library/otherActivity")
                        .with(otherBundle)
                        .navigation();
                break;
            case R.id.main_recycler_view_btn:
                Bundle recyclerBundle = new Bundle();
                recyclerBundle.putString(Constant.TYPE, Constant.TYPE_RECYCLER_VIEW);
                ARouter.getInstance()
                        .build("/presenter/commonHost")
                        .withBundle(Constant.TYPE_COMMON_BUNDLE, recyclerBundle)
                        .navigation();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FixMemLeak.fixLeak(this);
    }
}
