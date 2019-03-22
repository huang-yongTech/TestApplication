package com.test.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.test.R;
import com.test.library.util.FixMemLeak;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Handler remove相关方法测试
 */
public class HandlerRemoveFragment extends Fragment {
    private static final String TAG = "HandlerRemoveFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.public_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.handler_remove_tv)
    AppCompatTextView mHandlerRemoveTv;

    Unbinder unbinder;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //内部类持有外部类的引用，当内部类的生命周期大于外部类的生命周期时，就会出现内存泄漏
                    //这种情况下可以通过HandlerThread来解决，参考HandlerThreadFragment
                    mHandlerRemoveTv.setText("接收到消息，测试HandlerRemoveCallback");
                    break;
            }
        }
    };

    private Runnable mTimeRunnable;

    private String mParam1;
    private String mParam2;

    public HandlerRemoveFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static HandlerRemoveFragment newInstance(String param1, String param2) {
        HandlerRemoveFragment fragment = new HandlerRemoveFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_handler_remove, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        mToolbar.setTitle("HandlerRemoveCallback");

        mTimeRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(TAG, "run: 开始执行Runnable中方法");
                    TimeUnit.SECONDS.sleep(20);
                    Message message = Message.obtain(mHandler);
                    message.what = 0;
                    mHandler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(mTimeRunnable).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
//        mHandler.removeCallbacks(mTimeRunnable);
        mHandler.removeCallbacksAndMessages(null);
    }

    @OnClick(R.id.handler_remove_btn)
    public void onViewClicked() {
        Toast.makeText(getContext(), "按钮点击", Toast.LENGTH_SHORT).show();

    }
}
