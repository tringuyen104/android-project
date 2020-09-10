package com.example.zotee.activity.recycler;


import androidx.recyclerview.widget.RecyclerView;

import com.example.zotee.databinding.ItemLineBinding;

/**
 * @author thinh.nguyen
 */
public class ItemViewHolder extends RecyclerView.ViewHolder {

    private final ItemLineBinding binding;

    public ItemViewHolder(ItemLineBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public ItemLineBinding getBinding() {
        return binding;
    }
}
