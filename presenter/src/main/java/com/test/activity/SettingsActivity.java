package com.test.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.test.R;

@Route(path = "/presenter/settings")
public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent intent = getIntent();
        Log.i(TAG, "onCreate: ");
        Log.i(TAG, "url = " + intent.getStringExtra("url"));
        Log.i(TAG, "title = " + intent.getStringExtra("title"));
    }
}
