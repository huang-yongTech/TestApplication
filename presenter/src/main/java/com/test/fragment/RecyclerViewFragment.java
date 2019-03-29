package com.test.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.test.R;
import com.test.adapter.RecyclerTestAdapter;
import com.test.widget.GridDividerItemDecoration;
import com.test.widget.GridItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * RecyclerView测试
 */
@Route(path = "/presenter/recyclerViewFragment")
public class RecyclerViewFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.public_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_recycler_view)
    RecyclerView mRecyclerView;
    Unbinder unbinder;

    private String mParam1;
    private String mParam2;

    public RecyclerViewFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static RecyclerViewFragment newInstance(String param1, String param2) {
        RecyclerViewFragment fragment = new RecyclerViewFragment();
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
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        mToolbar.setTitle("RecyclerView测试");

        initRecyclerView();
    }

    private void initRecyclerView() {
        Context context = Objects.requireNonNull(getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        GridDividerItemDecoration itemDecoration = new GridDividerItemDecoration(context);
        itemDecoration.setDrawable(Objects.requireNonNull(context.getDrawable(R.drawable.divider_space)));
        mRecyclerView.addItemDecoration(itemDecoration);
        RecyclerTestAdapter adapter = new RecyclerTestAdapter(R.layout.item_string_layout);
        mRecyclerView.setAdapter(adapter);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 29; i++) {
            list.add("测试数据 " + i);
        }
        adapter.setNewData(list);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
