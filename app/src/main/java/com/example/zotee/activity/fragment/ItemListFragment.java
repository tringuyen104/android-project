package com.example.zotee.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.zotee.R;
import com.example.zotee.activity.fragment.model.NoteListViewModel;
import com.example.zotee.activity.recycler.NoteAdapter;
import com.example.zotee.databinding.ItemListFragmentBinding;

public class ItemListFragment extends SearchableActionBarFragment {


    private ItemListFragmentBinding binding;
    private NoteAdapter noteAdapter;
    private NoteListViewModel viewModel;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.item_list_fragment, container, false);
        noteAdapter = new NoteAdapter();
        binding.itemList.setAdapter(noteAdapter);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(NoteListViewModel.class);

        viewModel.getData().observe(getViewLifecycleOwner(), noteEntities -> {
            if (noteEntities != null) {
                noteAdapter.setNoteList(noteEntities);
            }
            binding.executePendingBindings();
        });
    }

    @Override
    public SearchView.OnQueryTextListener getSearchQueryListener() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                viewModel.setQuery(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() == 0) {
                    viewModel.setQuery("");
                }
                return false;
            }

        };
    }


    @Override
    public void onDestroyView() {
        binding = null;
        noteAdapter = null;
        super.onDestroyView();
    }
}