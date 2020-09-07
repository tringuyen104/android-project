package com.example.zotee.activity.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zotee.databinding.CloudItemsFragmentBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class CloudItemsFragment extends BaseActionBarFragment {




    private CloudItemsFragmentBinding binding;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = CloudItemsFragmentBinding.inflate(getLayoutInflater());
        binding.signInMainButton.setOnClickListener(view -> signIn());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       /* bottomSheetDialog = new BottomSheetDialog(this.requireActivity(), R.style.BottomSheetStyleDialogTheme);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.sigin_dialog, view.findViewById(R.id.bottomSheetContainer));
        bottomSheetView.findViewById(R.id.sign_in_button).setOnClickListener(views -> {
            signIn();
        });
        bottomSheetDialog.setContentView(bottomSheetView);*/
    }

}