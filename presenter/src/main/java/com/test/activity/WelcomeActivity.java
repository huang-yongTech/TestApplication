package com.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;

import com.test.R;
import com.test.base.util.FixMemLeak;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class WelcomeActivity extends AppCompatActivity {
    @BindView(R.id.welcome_tv)
    AppCompatTextView mWelcomeTv;

    private Disposable mDisposable;
    private long mTime = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        mDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .take(5)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) {
                        return mTime - aLong;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()
                )
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        if (mWelcomeTv != null) {
                            mWelcomeTv.setText(aLong == -1 ? "" : String.valueOf(aLong));
                        }
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
        FixMemLeak.fixLeak(this);
    }
}
