package com.test.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.test.R;
import com.test.cache.AppDatabase;
import com.test.cache.PeopleDataSource;
import com.test.entity.People;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class RealmTestActivity extends AppCompatActivity {
    private static final String TAG = "RealmTestActivity";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.main_people_et)
    AppCompatEditText mPeopleEt;

    private Disposable disposable;
    private PeopleDataSource mPeopleDataSource;
    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realm_test);

        ButterKnife.bind(this);

        mToolbar.setTitle("Room测试");
        setSupportActionBar(mToolbar);

        init();
    }

    private void init() {
        AppDatabase appDatabase = AppDatabase.getInstance(this);
        mCompositeDisposable = new CompositeDisposable();
        mPeopleDataSource = new PeopleDataSource(appDatabase.peopleDao());

        mCompositeDisposable.add(mPeopleDataSource.queryPeopleList()
                .subscribe(new Consumer<List<People>>() {
                    @Override
                    public void accept(List<People> people) {
                        Log.i(TAG, "accept: 查询操作！");
                    }
                }));
    }

    @OnClick(R.id.main_realm_test_btn)
    public void onViewClicked() {
        String peopleName = mPeopleEt.getText().toString().trim();

        mCompositeDisposable.add(mPeopleDataSource.insert(new People(peopleName))
                .subscribe(new Action() {
                    @Override
                    public void run() {
                        Log.i(TAG, "run: 插入成功！");
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }

    //    private void init() {
//        Observer<String> observer = new Observer<String>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//                Log.i(TAG, "onSubscribe--运行线程：" + Thread.currentThread().getName());
//                disposable = d;
//            }
//
//            @Override
//            public void onNext(String integer) {
//                Log.i(TAG, "onNext: " + integer + " --运行线程：" + Thread.currentThread().getName());
//                mToolbar.setTitle(integer + "");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.i(TAG, "onError: " + e.getMessage());
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onComplete() {
//                Log.i(TAG, "onComplete--运行线程：" + Thread.currentThread().getName());
//            }
//        };
//
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> emitter) {
//                Log.i(TAG, "subscribe--运行线程：" + Thread.currentThread().getName());
//                emitter.onNext(1);
//                emitter.onNext(2);
//                emitter.onNext(3);
//                emitter.onComplete();
//            }
//        }).subscribeOn(Schedulers.io())
//                .subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
//                .flatMap(new Function<Integer, ObservableSource<String>>() {
//                    @Override
//                    public ObservableSource<String> apply(Integer integer) {
//                        if (integer == 2) {
//                            return Observable.just("忽略偶数");
//                        }
//                        return Observable.just(String.valueOf(integer));
//                    }
//                })
//                .subscribe(observer);
//    }
}
