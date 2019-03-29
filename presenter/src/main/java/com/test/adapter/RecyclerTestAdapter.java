package com.test.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.test.R;

/**
 * Created by huangyong on 2019/3/28
 */
public class RecyclerTestAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public RecyclerTestAdapter(int layoutResId) {
        super(layoutResId);
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.recycler_test_tv, item);
    }
}
