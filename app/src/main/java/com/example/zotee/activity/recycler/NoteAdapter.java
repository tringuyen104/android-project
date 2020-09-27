package com.example.zotee.activity.recycler;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zotee.EditDetailsActivity;
import com.example.zotee.R;
import com.example.zotee.activity.HomeActivity;
import com.example.zotee.activity.message.MessageGenerator;
import com.example.zotee.databinding.ItemLineBinding;
import com.example.zotee.storage.DataRepository;
import com.example.zotee.storage.entity.InvitationEntity;
import com.example.zotee.storage.entity.NoteEntity;
import com.example.zotee.storage.model.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author thinh.nguyen
 */
public class NoteAdapter extends  RecyclerView.Adapter<ItemViewHolder> {


    List<? extends Note> items;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private final DataRepository dataRepository;

    public NoteAdapter(DataRepository dataRepository) {
        setHasStableIds(true);
        this.dataRepository = dataRepository;
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
            Toast.makeText(view.getContext(), "Đang xem: " + items.get(position).getTitle(), Toast.LENGTH_LONG).show();
            AsyncTask.execute(() -> {
                Intent intent = new Intent(view.getContext(), EditDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", items.get(position).getId());
                bundle.putString("event_name", items.get(position).getTitle());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy    HH:mm");
                String d = simpleDateFormat.format(items.get(position).getDate());
                bundle.putString("Date", d);
                bundle.putString("DesName", items.get(position).getLocationName());
                bundle.putString("Content", items.get(position).getContent());
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);
            });
        });

        holder.getBinding().itemDeleteIcon.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Xác nhận xóa");
            builder.setMessage("Bạn có muốn xóa lịch hẹn " + items.get(position).getTitle() + " này?");
            builder.setIcon(R.drawable.ic_baseline_remove_circle_24);
            builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();

                    AsyncTask.execute(() -> {
                        NoteEntity entity = new NoteEntity();
                        entity.setId(items.get(position).getId());
                        entity.setTitle(items.get(position).getTitle());

                        try {
                            entity.setDate(items.get(position).getDate());
                        }
                        catch (Exception e)
                        {
                            e.getMessage();
                        }
                        entity.setLocationName(items.get(position).getLocationName());
                        entity.setContent(items.get(position).getContent());
                        dataRepository.delete(entity);
                    });
                    Intent intent= new Intent(view.getContext(), HomeActivity.class);
                    view.getContext().startActivity(intent);
                }

            });
            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        });

        holder.getBinding().itemActionIcon.setOnClickListener(view -> {
            if( auth.getCurrentUser() != null) {
                NoteEntity note = (NoteEntity) items.get(position);
                if(note.getInvitationId() == null) {
                    dataRepository.queryNoteCount().addListenerForSingleValueEvent(new ValueEventListener() {
                                int update = 1;
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(update > 0) {
                                        update--;
                                        Integer count = snapshot.getValue(Integer.class);
                                        String noteCloudId = dataRepository.createCloudNote(auth.getCurrentUser().getUid(), null, note, count);
                                        InvitationEntity invitationEntity = new InvitationEntity();
                                        invitationEntity.setNoteId(noteCloudId);
                                        invitationEntity.setOwnerId(auth.getCurrentUser().getUid());
                                        invitationEntity = dataRepository.createCloudInvitation(invitationEntity);
                                        note.setInvitationId(invitationEntity.getId());
                                        new Thread(() -> dataRepository.update(note)).start();
                                        openShareDialog(view, invitationEntity);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                } else {
                    Query query = dataRepository.queryCloudInvitation(note.getInvitationId());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            InvitationEntity invitationEntity = snapshot.getValue(InvitationEntity.class);
                            if(invitationEntity == null) {
                                note.setInvitationId(null);
                                new Thread(() -> dataRepository.update(note)).start();
                                Toast.makeText(view.getContext(), "Lỗi dữ liệu với Server. Vui lòng thử lại!", Toast.LENGTH_LONG).show();
                                return;
                            }
                            dataRepository.queryCloudNote(auth.getCurrentUser().getUid(), invitationEntity.getNoteId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    NoteEntity noteEntity = snapshot.getValue(NoteEntity.class);
                                    if(noteEntity != null) {
                                        openShareDialog(view, invitationEntity);
                                    } else {
                                        dataRepository.queryNoteCount().addValueEventListener(new ValueEventListener() {
                                            int update = 1;
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(update > 0) {
                                                    Integer count = snapshot.getValue(Integer.class);
                                                    dataRepository.createCloudNote(auth.getCurrentUser().getUid(), null, note, count);
                                                    update--;
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            } else {
                Toast.makeText(view.getContext(), "Please sign-in to use this feature", Toast.LENGTH_LONG).show();
            }
        });
        holder.getBinding().setItem(items.get(position));
        holder.getBinding().executePendingBindings();
    }

    protected void openShareDialog(View view, InvitationEntity invitationEntity){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, MessageGenerator.genInviteMessage(invitationEntity));
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        view.getContext().startActivity(shareIntent);
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
