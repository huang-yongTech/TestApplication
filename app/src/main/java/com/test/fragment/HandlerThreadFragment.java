package com.test.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HandlerThreadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HandlerThreadFragment extends Fragment {
    private static final String TAG = "HandlerThreadFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.public_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.handler_thread_tv)
    AppCompatTextView mHandlerThreadTv;
    Unbinder unbinder;

    private Handler mWorkerHandler;
    private Handler mMainHandler;
    private HandlerThread mHandlerThread;

    private String mParam1;
    private String mParam2;

    public HandlerThreadFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HandlerThreadFragment.
     */
    public static HandlerThreadFragment newInstance(String param1, String param2) {
        HandlerThreadFragment fragment = new HandlerThreadFragment();
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
        View view = inflater.inflate(R.layout.fragment_handler_thread, container, false);
        unbinder = ButterKnife.bind(this, view);

        init();
        return view;
    }

    private void init() {
        mToolbar.setTitle("HandlerThread测试");

        mMainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mHandlerThreadTv.setText(String.valueOf(msg.arg1));
            }
        };

        mHandlerThread = new HandlerThread("HandlerThread");
        mHandlerThread.start();

        mWorkerHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        Log.i(TAG, "handleMessage: 线程名：" + Thread.currentThread().getName());
                        Message message = Message.obtain(msg);
                        mMainHandler.sendMessage(message);
                        break;
                }
            }
        };
    }

    @OnClick(R.id.handler_thread_btn)
    public void onViewClicked() {
        Message message = Message.obtain();
        message.what = 0;
        message.arg1 = 11111;
        mWorkerHandler.sendMessage(message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mHandlerThread.quitSafely();
    }
}
