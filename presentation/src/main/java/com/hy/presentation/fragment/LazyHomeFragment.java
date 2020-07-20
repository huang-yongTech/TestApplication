package com.hy.presentation.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hy.presentation.R;
import com.hy.presentation.databinding.FragmentLazyHomeBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * 懒加载主界面Fragment
 */
@Route(path = "/presentation/LazyHomeFragment")
public class LazyHomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FragmentLazyHomeBinding mBinding;

    public LazyHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static LazyHomeFragment newInstance(String param1, String param2) {
        LazyHomeFragment fragment = new LazyHomeFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lazy_home, container, false);
        mBinding = FragmentLazyHomeBinding.bind(view);

        init();
        return view;
    }

    private void init() {
        mBinding.lazyToolbar.publicBindingToolbar.setTitle("Fragment懒加载");
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new LazyFirstFragment());
        fragmentList.add(new LazySecondFragment());
        fragmentList.add(new LazyThirdFragment());

        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        };

        mBinding.lazyViewPager.setAdapter(pagerAdapter);
    }
}