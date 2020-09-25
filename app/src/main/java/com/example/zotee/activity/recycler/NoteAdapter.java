package com.example.zotee.activity.recycler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zotee.EditDetailsActivity;
import com.example.zotee.R;
import com.example.zotee.activity.callback.ItemClickCallback;
import com.example.zotee.activity.fragment.CloudItemsFragment;
import com.example.zotee.databinding.ItemLineBinding;
import com.example.zotee.storage.model.Note;

import java.util.List;

/**
 * @author thinh.nguyen
 */
public class NoteAdapter extends  RecyclerView.Adapter<ItemViewHolder> {


    List<? extends Note> items;

    public NoteAdapter() {
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
        ItemLineBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.item_line,
                        parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.itemView.setOnClickListener(view -> {
            Toast.makeText(view.getContext(), "Selected:" + items.get(position).getId(), Toast.LENGTH_LONG).show();
            AsyncTask.execute(() -> {
                Intent intent = new Intent(view.getContext(), EditDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", items.get(position).getId());
                bundle.putString("event_name", items.get(position).getTitle());
                bundle.putString("Date", items.get(position).getDateText());
                bundle.putString("Time", items.get(position).getTimeText());
                bundle.putString("DesName", items.get(position).getLocationName());
                bundle.putString("Content", items.get(position).getContent());
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);
            });
        });
        holder.getBinding().itemActionIcon.setOnClickListener(view -> {
            Toast.makeText(view.getContext(), "Share selected", Toast.LENGTH_LONG).show();
            AsyncTask.execute(() -> {
                Intent intent = new Intent(view.getContext(), EditDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", items.get(position).getId());
                bundle.putString("event_name", items.get(position).getTitle());
                bundle.putString("Date", items.get(position).getDateText());
                bundle.putString("Time", items.get(position).getTimeText());
                bundle.putString("DesName", items.get(position).getLocationName());
                bundle.putString("Content", items.get(position).getContent());
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);
            });
        });
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
