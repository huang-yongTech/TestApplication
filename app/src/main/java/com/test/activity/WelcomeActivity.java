package com.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.test.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class WelcomeActivity extends AppCompatActivity {
    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        init();
    }

    private void init() {
        mDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .take(3)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {

                    }
                }, new Action() {
                    @Override
                    public void run() {
                        start();
                    }
                });
    }

    private void start() {
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        WelcomeActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }
}
