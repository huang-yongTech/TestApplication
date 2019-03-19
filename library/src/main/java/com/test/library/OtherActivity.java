package com.test.library;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class OtherActivity extends AppCompatActivity {
    private static final String TAG = "OtherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        Intent intent = getIntent();
        Log.i(TAG, "onCreate: ");
        Log.i(TAG, "url = " + intent.getStringExtra("url"));
        Log.i(TAG, "title = " + intent.getStringExtra("title"));
    }
}
