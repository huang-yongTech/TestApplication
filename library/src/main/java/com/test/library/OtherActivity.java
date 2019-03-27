package com.test.library;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.test.base.util.FixMemLeak;

@Route(path = "/library/otherActivity")
public class OtherActivity extends AppCompatActivity {
    private static final String TAG = "OtherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle == null) {
            return;
        }

        Log.i(TAG, "onCreate: ");
        Log.i(TAG, "url = " + bundle.getString("url"));
        Log.i(TAG, "title = " + bundle.getString("title"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FixMemLeak.fixLeak(this);
    }
}
