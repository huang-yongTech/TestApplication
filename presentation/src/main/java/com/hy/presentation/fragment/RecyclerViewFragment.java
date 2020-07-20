package com.hy.presentation.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.hy.presentation.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.hy.presentation.adapter.RecyclerTestAdapter;
import com.hy.presentation.widget.TimeAxisItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * RecyclerView测试
 */
@Route(path = "/presentation/recyclerViewFragment")
public class RecyclerViewFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.public_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.fragment_refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    Unbinder unbinder;

    private RecyclerTestAdapter mAdapter;
    private List<String> mList;

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
        mList = new ArrayList<>();

        initRecyclerView();

        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 5; i++) {
                            mList.add("测试数据：" + i);
                        }
                        mAdapter.notifyDataSetChanged();
                        mRefreshLayout.finishLoadMore(1200);
                    }
                }, 1500);

            }
        });
    }

    private void initRecyclerView() {
        Context context = Objects.requireNonNull(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(layoutManager);
        TimeAxisItemDecoration itemDecoration = new TimeAxisItemDecoration(context);
        mRecyclerView.addItemDecoration(itemDecoration);
        mAdapter = new RecyclerTestAdapter(R.layout.item_string_layout);
        mRecyclerView.setAdapter(mAdapter);

        for (int i = 0; i < 20; i++) {
            mList.add("测试数据 " + i);
        }
        mAdapter.setNewData(mList);

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                adapter.remove(position);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
