package com.example.zotee.activity.recycler;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zotee.R;
import com.example.zotee.activity.callback.ItemClickCallback;
import com.example.zotee.databinding.ItemDetailBinding;
import com.example.zotee.storage.model.Note;

import java.util.List;

/**
 * @author thinh.nguyen
 */
public class NoteAdapter extends  RecyclerView.Adapter<ItemViewHolder> {


    List<? extends Note> items;

    @Nullable
    private final ItemClickCallback<? extends Note> itemClickCallback;

    public NoteAdapter(@Nullable ItemClickCallback<? extends Note> clickCallback) {
        itemClickCallback = clickCallback;
        setHasStableIds(true);
    }

    public void setNoteList(final List<? extends Note> noteList) {
        if (items == null) {
            items = noteList;
            notifyItemRangeInserted(0, noteList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return items.size();
                }

                @Override
                public int getNewListSize() {
                    return noteList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return items.get(oldItemPosition).getId() ==
                            noteList.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Note newNote = noteList.get(newItemPosition);
                    Note oldNote = items.get(oldItemPosition);
                    return newNote.getId() == oldNote.getId()
                           /* && TextUtils.equals(newNote.getContent(), oldNote.getContent())
                            && TextUtils.equals(newNote.getTitle(), oldNote.getTitle())
                            && newNote.getLocationName() == oldNote.getLocationName()*/;
                }
            });
            items = noteList;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDetailBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.item_detail,
                        parent, false);
        binding.setCallback(itemClickCallback);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.getBinding().setItem(items.get(position));
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }
}
