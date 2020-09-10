package com.example.zotee.activity.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.zotee.R;
import com.example.zotee.activity.recycler.ItemViewHolder;
import com.example.zotee.databinding.CloudItemsFragmentBinding;
import com.example.zotee.databinding.ItemLineBinding;
import com.example.zotee.storage.DataRepository;
import com.example.zotee.storage.entity.NoteEntity;
import com.example.zotee.storage.model.Note;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;


import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CloudItemsFragment extends BaseActionBarFragment {

    @Inject
    DataRepository dataRepository;

    private CloudItemsFragmentBinding binding;
    private BottomSheetDialog bottomSheetDialog;

    private FirebaseRecyclerAdapter<Note, ItemViewHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = CloudItemsFragmentBinding.inflate(getLayoutInflater());
        binding.signInMainButton.setOnClickListener(view -> signIn());
        binding.itemList.setHasFixedSize(true);

        return binding.getRoot();
    }
    private void initAdapter() {
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<NoteEntity>()
                .setQuery(dataRepository.queryCloudNotes(getUser().getUid()), NoteEntity.class)
//                .setQuery(dataRepository.queryCloudNotes(getUser().getUid()), NoteEntity.class)
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
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole note view
                final String noteKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(v -> {
                    // on item click

                });

                // Bind to ViewHolder
                viewHolder.getBinding().setItem(model);
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
       /* bottomSheetDialog = new BottomSheetDialog(this.requireActivity(), R.style.BottomSheetStyleDialogTheme);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.sigin_dialog, view.findViewById(R.id.bottomSheetContainer));
        bottomSheetView.findViewById(R.id.sign_in_button).setOnClickListener(views -> {
            signIn();
        });
        bottomSheetDialog.setContentView(bottomSheetView);*/
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