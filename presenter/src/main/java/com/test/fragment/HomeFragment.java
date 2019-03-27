package com.test.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.test.R;
import com.test.base.BaseFragment;
import com.test.base.rxbus.RxBus;
import com.test.base.rxbus.Subscribe;
import com.test.base.Constant;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomeFragment extends BaseFragment {
    private static final String TAG = "HomeFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Unbinder unbinder;
    @BindView(R.id.home_left_btn)
    RadioButton mLeftBtn;
    @BindView(R.id.home_right_btn)
    RadioButton mRightBtn;

    private boolean mLeftSelected;
    private boolean mRightSelected;

    private String mParam1;
    private String mParam2;

    public HomeFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getView()).setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //拦截到的返回事件 
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    FragmentManager fragmentManager = getChildFragmentManager();
                    Log.i(TAG, "onKey: 回退栈大小=" + fragmentManager.getBackStackEntryCount());
                    if (fragmentManager.getBackStackEntryCount() > 0) {
                        fragmentManager.popBackStack();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void init() {
        RxBus.getDefault().register(this);

        addFragment(R.id.home_fragment_container, new LeftFragment());
        mLeftSelected = true;
        mLeftBtn.setChecked(true);
    }

    @OnClick({R.id.home_left_btn, R.id.home_right_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.home_left_btn:
                if (!mLeftSelected) {
                    mLeftSelected = true;
                    mRightSelected = false;
                    replaceFragment(R.id.home_fragment_container, new LeftFragment());
                }
                break;
            case R.id.home_right_btn:
                if (!mRightSelected) {
                    mRightSelected = true;
                    mLeftSelected = false;
                    FragmentManager fragmentManager = getChildFragmentManager();
                    int count = fragmentManager.getBackStackEntryCount();
                    for (int i = 0; i < count; i++) {
                        fragmentManager.popBackStack();
                    }
                    replaceFragment(R.id.home_fragment_container, new RightFragment());
                }
                break;
        }
    }

    @Subscribe(code = Constant.REPLACE_FRAGMENT)
    public void replaceFragment(String item) {
        int level = 0;
        level++;
        replaceFragmentToBackStackInAnimation(R.id.home_fragment_container, ChildFragment.newInstance("child级数" + level));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        RxBus.getDefault().unRegister(this);
    }
}
