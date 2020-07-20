package com.hy.presentation.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hy.presentation.R;

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

        //匿名内部类会隐式持有外部类的引用，因此只要这里被执行了，就会出现内存泄漏错误
        //内部类隐式持有外部类的引用，当内部类的生命周期大于外部类的生命周期时，就会出现内存泄漏
        //这种情况下可以通过HandlerThread来解决
        Handler.Callback mainCallback = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        Log.i(TAG, "handleMessage: callback代码执行");
                        mHandlerThreadTv.setText(String.valueOf(msg.obj));
                        Log.i(TAG, "handleMessage: 打印消息：" + msg.obj);
                        break;
                }
                return true;
            }
        };
        mMainHandler = new Handler(Looper.getMainLooper(), mainCallback);

        mHandlerThread = new HandlerThread("HandlerThread");
        mHandlerThread.start();

        Handler.Callback workCallBack = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        //这样的代码是错误的，由于内部类隐式持有外部类的引用，当内部类的生命周期超过外部类的生命周期时，
                        //就会造成外部类对象始终无法被GC回收，因此出现内存泄漏，
                        //在开发中绝对不要出现这样的代码
                        //如果内部类中有线程，那么在外部类的结束生命周期中一定要把这个线程终结掉，否则就会出现内存泄漏，原因如上
//                        try {
//                            TimeUnit.SECONDS.sleep(20);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                        Log.i(TAG, "handleMessage: 线程名：" + Thread.currentThread().getName());

                        Message message = Message.obtain(msg);
                        mMainHandler.sendMessage(message);
                        break;
                }
                return true;
            }
        };
        mWorkerHandler = new Handler(mHandlerThread.getLooper(), workCallBack);
    }

    @OnClick(R.id.handler_thread_btn)
    public void onViewClicked() {
        Message message = Message.obtain();
        message.what = 0;
        message.obj = "这是主线程按钮的点击事件";
        mWorkerHandler.sendMessage(message);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
//        mHandlerThread.quitSafely();
        mHandlerThread.quit();
        //这种方法只能移除当前已经在消息队列中的消息，不能移除界面结束后还在运行的线程发送的消息
        mWorkerHandler.removeCallbacksAndMessages(null);
        mMainHandler.removeCallbacksAndMessages(null);
    }
}
