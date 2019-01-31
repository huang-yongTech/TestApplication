package com.test.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.test.R;

import java.util.List;

public class FragmentListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public FragmentListAdapter(int layoutResId) {
        super(layoutResId);
    }

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     */
    public FragmentListAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     */
    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.folder_list_item, item);
    }
}