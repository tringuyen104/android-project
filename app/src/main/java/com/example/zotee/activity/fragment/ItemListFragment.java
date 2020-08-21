package com.example.zotee.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import com.example.zotee.R;
import com.example.zotee.activity.fragment.model.NoteListViewModel;
import com.example.zotee.activity.recycler.NoteAdapter;
import com.example.zotee.databinding.ItemListFragmentBinding;

public class ItemListFragment extends Fragment {


    private ItemListFragmentBinding binding;
    private NoteAdapter noteAdapter;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.item_list_fragment, container, false);
        noteAdapter = new NoteAdapter((note) -> {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                ItemFragment itemFragment = new ItemFragment();
            }
        });
        binding.itemList.setAdapter(noteAdapter);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NoteListViewModel viewModel =
                new ViewModelProvider(requireActivity()).get(NoteListViewModel.class);

        viewModel.getData().observe(getViewLifecycleOwner(), noteEntities -> {
            if (noteEntities != null) {
                noteAdapter.setNoteList(noteEntities);
            }
            binding.executePendingBindings();
        });
    }

    @Override
    public void onDestroyView() {
        binding = null;
        noteAdapter = null;
        super.onDestroyView();
    }
}