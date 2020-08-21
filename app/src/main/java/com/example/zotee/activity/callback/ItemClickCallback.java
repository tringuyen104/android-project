package com.example.zotee.activity.callback;

import android.content.Context;

import com.example.zotee.storage.model.Item;

/**
 * @author thinh.nguyen
 */
public interface ItemClickCallback<T extends Item> {

    void onClick(T t);
}
