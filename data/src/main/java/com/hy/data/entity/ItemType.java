package com.hy.data.entity;

/**
 * Created by huangyong on 2019/4/10
 * main界面列表数据实体类
 */
public class ItemType {
    private String item;
    private String type;

    public ItemType() {
    }

    public ItemType(String item, String type) {
        this.item = item;
        this.type = type;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
