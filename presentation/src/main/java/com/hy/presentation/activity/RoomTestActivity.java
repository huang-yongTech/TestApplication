package com.hy.presentation.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.hy.data.cache.AppDatabase;
import com.hy.data.cache.PeopleDataSource;
import com.hy.data.entity.People;
import com.hy.base.util.FixMemLeak;
import com.hy.presentation.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class RoomTestActivity extends AppCompatActivity {
    private static final String TAG = "RoomTestActivity";
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
        setContentView(R.layout.activity_room_test);

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

    @OnClick(R.id.room_test_submit_btn)
    public void onViewClicked() {
        String peopleName = mPeopleEt.getText().toString().trim();

        mCompositeDisposable.add(mPeopleDataSource.insert(new People(peopleName))
                .subscribe(new Action() {
                    @Override
                    public void run() {
                        Toast.makeText(RoomTestActivity.this, "插入成功", Toast.LENGTH_SHORT).show();
                        mPeopleEt.setText("");
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
        FixMemLeak.fixLeak(this);
    }
}
