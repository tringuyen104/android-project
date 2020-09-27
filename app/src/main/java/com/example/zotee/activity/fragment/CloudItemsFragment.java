package com.example.zotee.activity.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.zotee.CloudEventDetailsActivity;
import com.example.zotee.EditDetailsActivity;
import com.example.zotee.R;
import com.example.zotee.activity.message.MessageGenerator;
import com.example.zotee.activity.recycler.ItemViewHolder;
import com.example.zotee.databinding.CloudItemsFragmentBinding;
import com.example.zotee.databinding.ItemLineBinding;
import com.example.zotee.storage.DataRepository;
import com.example.zotee.storage.entity.InvitationEntity;
import com.example.zotee.storage.entity.NoteEntity;
import com.example.zotee.storage.model.Note;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.util.Strings;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CloudItemsFragment extends SearchableActionBarFragment {

    @Inject
    DataRepository dataRepository;

    private CloudItemsFragmentBinding binding;

    private FirebaseRecyclerAdapter<Note, ItemViewHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        setHasOptionsMenu(true);
        binding = CloudItemsFragmentBinding.inflate(getLayoutInflater());
        binding.signInMainButton.setOnClickListener(view -> signIn());
        binding.itemList.setHasFixedSize(true);

        return binding.getRoot();
    }
    private void initAdapter() {
        initAdapter(null);
    }

    private void initAdapter(final String filter) {
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<NoteEntity>()
                .setQuery(dataRepository.queryCloudNotes(getUser().getUid()), NoteEntity.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Note, ItemViewHolder>(options) {
            @Override
            public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                ItemLineBinding binding = DataBindingUtil
                        .inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_line,
                                viewGroup, false);
                return new ItemViewHolder(binding);
            }

            @Override
            protected void onBindViewHolder(ItemViewHolder viewHolder, int position, final Note model) {
                final DatabaseReference noteRef = getRef(position);
                // Set click listener for the whole note view
                final String noteKey = noteRef.getKey();
                viewHolder.itemView.setOnClickListener(v -> {
                    Toast.makeText(CloudItemsFragment.this.requireActivity(), "Đang xem: " + model.getTitle(), Toast.LENGTH_LONG).show();
                    AsyncTask.execute(() -> {
                        Intent intent = new Intent(CloudItemsFragment.this.requireActivity(), CloudEventDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", model.getId());
                        bundle.putString("event_name", model.getTitle());
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy    HH:mm");
                        String d = simpleDateFormat.format(model.getDate());
                        bundle.putString("Date", d);
                        bundle.putString("DesName", model.getLocationName());
                        bundle.putString("Content", model.getContent());
                        intent.putExtras(bundle);
                        CloudItemsFragment.this.requireActivity().startActivity(intent);
                    });
                });

                // Bind to ViewHolder
                viewHolder.getBinding().setItem(model);
                viewHolder.getBinding().itemActionIcon.setImageResource(R.drawable.ic_baseline_person_add_24);
                viewHolder.getBinding().itemActionIcon.setOnClickListener(view -> {
                    if(model.getInvitationId() == null) {
                        Toast.makeText(CloudItemsFragment.this.requireActivity(), "Lỗi dữ liệu! Invitation không tồn tại.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    dataRepository.queryCloudInvitation(model.getInvitationId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            InvitationEntity entity = snapshot.getValue(InvitationEntity.class);
                            if(entity == null) {
                                Toast.makeText(CloudItemsFragment.this.requireActivity(), "Lỗi dữ liệu! Không tìm thấy invitation trên hệ thống.", Toast.LENGTH_LONG).show();
                                return;
                            }
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, MessageGenerator.genInviteMessage(entity));
                            sendIntent.setType("text/plain");
                            Intent shareIntent = Intent.createChooser(sendIntent, null);
                            view.getContext().startActivity(shareIntent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                });
                if(!Strings.isEmptyOrWhitespace(filter) && !Strings.isEmptyOrWhitespace(model.getFts()) && !model.getFts().toLowerCase().contains(filter.toLowerCase())) {
                    viewHolder.itemView.setVisibility(LinearLayout.GONE);
                    viewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
            }
        };
        binding.itemList.setAdapter(adapter);
        adapter.startListening();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(isLogged()) {
            initAdapter();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
    @Override
    protected void signOut(){
        super.signOut();
        if (adapter != null) {
            adapter.stopListening();
            adapter = null;
        }
    }

    @Override
    public SearchView.OnQueryTextListener getSearchQueryListener() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (isLogged() && !Strings.isEmptyOrWhitespace(query)) {
                    initAdapter(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (isLogged() && newText.length() == 0) {
                    initAdapter();
                }
                return false;
            }
        };
    }

    @Override
    void onLoggedIn() {

    }

    @Override
    void onLoggedOut() {
        onResume();
    }

    @Override
    public void onResume() {
        if(isLogged()) {
            binding.signIn.setVisibility(LinearLayout.GONE);
            if(adapter == null) {
                initAdapter();
            }
        } else {
            adapter =null;
            binding.signIn.setVisibility(LinearLayout.VISIBLE);
        }
        super.onResume();
    }
}