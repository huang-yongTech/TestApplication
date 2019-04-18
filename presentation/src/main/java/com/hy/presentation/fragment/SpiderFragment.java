package com.hy.presentation.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hy.library.util.SizeUtils;
import com.hy.presentation.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 网状视图fragment
 */
@Route(path = "/presentation/spiderFragment")
public class SpiderFragment extends Fragment {
    private static final String TAG = "SpiderFragment";

    private static final int MENU_OPEN = 0x01;
    private static final int MENU_CLOSE = 0x02;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.public_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.menu_circle1_btn)
    AppCompatButton mCircle1Btn;
    @BindView(R.id.menu_circle2_btn)
    AppCompatButton mCircle2Btn;
    @BindView(R.id.menu_circle3_btn)
    AppCompatButton mCircle3Btn;
    @BindView(R.id.menu_circle4_btn)
    AppCompatButton mCircle4Btn;
    @BindView(R.id.menu_circle5_btn)
    AppCompatButton mCircle5Btn;
    @BindView(R.id.menu_btn)
    AppCompatButton mMenuBtn;

    Unbinder unbinder;

    private int mRadio = SizeUtils.dp2px(100);

    private PointF mStartPoint;

    private PointF mEndPoint1;
    private PointF mEndPoint2;
    private PointF mEndPoint3;
    private PointF mEndPoint4;
    private PointF mEndPoint5;

    private AnimatorSet mOpenAnimatorSet;
    private AnimatorSet mCloseAnimatorSet;

    private String mParam1;
    private String mParam2;

    public SpiderFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static SpiderFragment newInstance(String param1, String param2) {
        SpiderFragment fragment = new SpiderFragment();
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
        View view = inflater.inflate(R.layout.fragment_spider, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        mToolbar.setTitle("多边形网状统计数据");

        mStartPoint = new PointF(mMenuBtn.getRight(), mMenuBtn.getBottom());

        double angle = 22.5d;

        //在使用ARouter时，这些初始化不能使用非静态初始化，最好放在方法中
        mEndPoint1 = new PointF((float) (mStartPoint.x - mRadio * Math.sin(Math.toRadians(angle * 0))),
                mStartPoint.y - (float) (mRadio * Math.cos(Math.toRadians(angle * 0))));
        mEndPoint2 = new PointF((float) (mStartPoint.x - mRadio * Math.sin(Math.toRadians(angle * 1))),
                mStartPoint.y - (float) (mRadio * Math.cos(Math.toRadians(angle * 1))));
        mEndPoint3 = new PointF(mStartPoint.x - (float) (mRadio * Math.sin(Math.toRadians(angle * 2))),
                mStartPoint.y - (float) (mRadio * Math.cos(Math.toRadians(angle * 2))));
        mEndPoint4 = new PointF(mStartPoint.x - (float) (mRadio * Math.sin(Math.toRadians(angle * 3))),
                mStartPoint.y - (float) (mRadio * Math.cos(Math.toRadians(angle * 3))));
        mEndPoint5 = new PointF(mStartPoint.x - (float) (mRadio * Math.sin(Math.toRadians(angle * 4))),
                mStartPoint.y - (float) (mRadio * Math.cos(Math.toRadians(angle * 4))));

        mOpenAnimatorSet = new AnimatorSet();
        mCloseAnimatorSet = new AnimatorSet();

        mOpenAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                mMenuBtn.setTag(MENU_CLOSE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);

                mMenuBtn.setTag(MENU_CLOSE);
            }
        });

        mCloseAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);

                mMenuBtn.setTag(MENU_OPEN);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                mMenuBtn.setTag(MENU_OPEN);
            }
        });
    }

    @OnClick({R.id.menu_circle1_btn, R.id.menu_circle2_btn, R.id.menu_circle3_btn,
            R.id.menu_circle4_btn, R.id.menu_circle5_btn, R.id.menu_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu_circle1_btn:
                performMenuClose();
                showToast("点击位置1");
                break;
            case R.id.menu_circle2_btn:
                performMenuClose();
                showToast("点击位置2");
                break;
            case R.id.menu_circle3_btn:
                performMenuClose();
                showToast("点击位置3");
                break;
            case R.id.menu_circle4_btn:
                performMenuClose();
                showToast("点击位置4");
                break;
            case R.id.menu_circle5_btn:
                performMenuClose();
                showToast("点击位置5");
                break;
            case R.id.menu_btn:
                onMenuBtnClick();
                break;
        }
    }


    private void onMenuBtnClick() {
        Integer tag = (Integer) mMenuBtn.getTag();
        tag = tag == null ? MENU_OPEN : tag;

        switch (tag) {
            case MENU_OPEN:
                performMenuOpen();
                break;
            case MENU_CLOSE:
                performMenuClose();
                break;
        }
    }

    private void performMenuOpen() {
        if (mOpenAnimatorSet.isRunning()) {
            return;
        }

        onMenuOpen(mCircle1Btn, mStartPoint, mEndPoint1);
        onMenuOpen(mCircle2Btn, mStartPoint, mEndPoint2);
        onMenuOpen(mCircle3Btn, mStartPoint, mEndPoint3);
        onMenuOpen(mCircle4Btn, mStartPoint, mEndPoint4);
        onMenuOpen(mCircle5Btn, mStartPoint, mEndPoint5);
    }

    private void performMenuClose() {
        if (mCloseAnimatorSet.isRunning()) {
            return;
        }

        onMenuClose(mCircle1Btn, mEndPoint1, mStartPoint);
        onMenuClose(mCircle2Btn, mEndPoint2, mStartPoint);
        onMenuClose(mCircle3Btn, mEndPoint3, mStartPoint);
        onMenuClose(mCircle4Btn, mEndPoint4, mStartPoint);
        onMenuClose(mCircle5Btn, mEndPoint5, mStartPoint);
    }

    private void onMenuOpen(View view, PointF startPoint, PointF endPoint) {
        if (view.getVisibility() == View.GONE || view.getVisibility() == View.INVISIBLE) {
            view.setVisibility(View.VISIBLE);
        }

        mOpenAnimatorSet.playTogether(ObjectAnimator.ofFloat(view, "translationX", startPoint.x, endPoint.x),
                ObjectAnimator.ofFloat(view, "translationY", startPoint.y, endPoint.y),
                ObjectAnimator.ofFloat(view, "scaleX", 0, 1),
                ObjectAnimator.ofFloat(view, "scaleY", 0, 1),
                ObjectAnimator.ofFloat(view, "alpha", 0, 1));
        mOpenAnimatorSet.setDuration(500);
        mOpenAnimatorSet.start();
    }

    private void onMenuClose(View view, PointF startPoint, PointF endPoint) {
        mCloseAnimatorSet.playTogether(ObjectAnimator.ofFloat(view, "translationX", startPoint.x, endPoint.x),
                ObjectAnimator.ofFloat(view, "translationY", startPoint.y, endPoint.y),
                ObjectAnimator.ofFloat(view, "scaleX", 1, 0),
                ObjectAnimator.ofFloat(view, "scaleY", 1, 0),
                ObjectAnimator.ofFloat(view, "alpha", 1, 0));
        mCloseAnimatorSet.setDuration(500);
        mCloseAnimatorSet.start();
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mOpenAnimatorSet.isRunning()) {
            mOpenAnimatorSet.cancel();
        }

        if (mCloseAnimatorSet.isRunning()) {
            mCloseAnimatorSet.cancel();
        }
        unbinder.unbind();
    }
}
