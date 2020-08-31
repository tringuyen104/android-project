package com.example.zotee.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

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
    private NoteListViewModel viewModel;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.item_list_fragment, container, false);
        noteAdapter = new NoteAdapter((note) -> {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            }
        });
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                viewModel.setQuery(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        noteAdapter = null;
        super.onDestroyView();
    }
}