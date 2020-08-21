package com.example.zotee.activity.recycler;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zotee.databinding.ItemDetailBinding;

/**
 * @author thinh.nguyen
 */
public class ItemViewHolder extends RecyclerView.ViewHolder {

    private final ItemDetailBinding binding;

    public ItemViewHolder(ItemDetailBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public ItemDetailBinding getBinding() {
        return binding;
    }
}
