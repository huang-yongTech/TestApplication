package com.hy.presentation.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hy.data.entity.ItemType;
import com.hy.presentation.R;

/**
 * Created by huangyong on 2019/4/10
 * 主界面adapter
 */
public class ItemTypeAdapter extends BaseQuickAdapter<ItemType, BaseViewHolder> {
    public ItemTypeAdapter(int layoutResId) {
        super(layoutResId);
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseViewHolder helper, ItemType item) {
        helper.setText(R.id.item_main_text_tv, item.getItem());
    }
}
