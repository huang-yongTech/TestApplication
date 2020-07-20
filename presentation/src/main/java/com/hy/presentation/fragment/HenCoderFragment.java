package com.hy.presentation.fragment;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hy.library.widget.BooHeRulerView;
import com.hy.library.widget.FlipBoardPageView;
import com.hy.presentation.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * henCoder实践界面
 */
@Route(path = "/presentation/henCoderFragment")
public class HenCoderFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.public_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.hen_coder_boo_weight_tv)
    AppCompatTextView mBooWeightTv;
    @BindView(R.id.hen_coder_boo_he_view)
    BooHeRulerView mBooHeView;
    @BindView(R.id.hen_coder_flip_board_view)
    FlipBoardPageView mFlipBoardView;

    Unbinder unbinder;

    private String mParam1;
    private String mParam2;

    public HenCoderFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static HenCoderFragment newInstance(String param1, String param2) {
        HenCoderFragment fragment = new HenCoderFragment();
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
        View view = inflater.inflate(R.layout.fragment_hen_coder, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        mToolbar.setTitle("HenCoder实践");

        initBooHeView();
    }

    private void initBooHeView() {
        mBooWeightTv.setTextColor(Color.GREEN);
        String val = String.valueOf(mBooHeView.getCurrValue());
        SpannableString spannableString = new SpannableString(val + " kg");
        SuperscriptSpan superscriptSpan = new SuperscriptSpan();
        RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(0.5f);
        spannableString.setSpan(superscriptSpan, val.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(relativeSizeSpan, val.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mBooWeightTv.setText(spannableString);

        mBooHeView.setOnValueChangeListener(new BooHeRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                String val = String.valueOf(value);
                SpannableString spannableString = new SpannableString(val + " kg");
                SuperscriptSpan superscriptSpan = new SuperscriptSpan();
                RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(0.5f);
                spannableString.setSpan(superscriptSpan, val.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(relativeSizeSpan, val.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                mBooWeightTv.setText(spannableString);
            }
        });
    }

    @OnClick(R.id.hen_coder_flip_board_btn)
    public void onViewClicked() {
        mFlipBoardView.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
