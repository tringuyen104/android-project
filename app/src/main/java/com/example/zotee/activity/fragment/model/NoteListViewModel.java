package com.example.zotee.activity.fragment.model;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.zotee.storage.DataRepository;
import com.example.zotee.storage.entity.NoteEntity;

import java.util.List;


/**
 * @author thinh.nguyen
 */

public class NoteListViewModel extends ViewModel {

    private DataRepository dataRepository;
    private SavedStateHandle savedStateHandle;
    private LiveData<List<NoteEntity>> listLiveData;

    @ViewModelInject
    public NoteListViewModel(@Assisted SavedStateHandle savedStateHandle, DataRepository dataRepository) {
        this.dataRepository = dataRepository;
        this.savedStateHandle = savedStateHandle;
        listLiveData = Transformations.switchMap(
                savedStateHandle.getLiveData("QUERY", null),
                (Function<CharSequence, LiveData<List<NoteEntity>>>) query -> {
                    if (TextUtils.isEmpty(query)) {
                        return dataRepository.loadAllNotes();
                    }
                    return dataRepository.search("*" + query + "*");
                });
    }

    public LiveData<List<NoteEntity>> getData() {
        return listLiveData;
    }
}
