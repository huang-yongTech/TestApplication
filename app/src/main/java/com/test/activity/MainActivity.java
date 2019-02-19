package com.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.test.R;
import com.test.util.Constant;

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

    @OnClick({R.id.main_fragment_test_btn, R.id.main_realm_test_btn, R.id.main_handler_thread_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_fragment_test_btn:
                startActivity(new Intent(this, FragmentTestActivity.class));
                break;
            case R.id.main_realm_test_btn:
                startActivity(new Intent(this, RealmTestActivity.class));
                break;
            case R.id.main_handler_thread_btn:
                Bundle bundle = new Bundle();
                bundle.putString(Constant.TYPE, Constant.TYPE_HANDLER_THREAD);
                Intent intent = new Intent(this, CommonHostActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }
}
