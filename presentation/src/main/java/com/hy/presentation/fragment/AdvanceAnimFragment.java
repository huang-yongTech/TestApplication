package com.hy.presentation.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hy.library.widget.RoundShapeDrawable;
import com.hy.presentation.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 动画进阶
 */
@Route(path = "/presentation/advanceAnimFragment")
public class AdvanceAnimFragment extends Fragment {
    private static final String TAG = "AdvanceAnimFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.public_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.advance_anim_iv)
    AppCompatImageView mAdvanceAnimIv;

    Unbinder unbinder;

    private String mParam1;
    private String mParam2;

    public AdvanceAnimFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static AdvanceAnimFragment newInstance(String param1, String param2) {
        AdvanceAnimFragment fragment = new AdvanceAnimFragment();
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
        View view = inflater.inflate(R.layout.fragment_advance_anim, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        mToolbar.setTitle("动画进阶");

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avator);
        RoundShapeDrawable roundShapeDrawable = new RoundShapeDrawable(bitmap);
        mAdvanceAnimIv.setImageDrawable(roundShapeDrawable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
