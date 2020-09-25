package com.example.zotee.activity.fragment.model;

import android.text.TextUtils;

import androidx.arch.core.util.Function;
import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
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
                        return dataRepository.loadLocalNotes();
                    }
                    return dataRepository.search("*" + query + "*");
                });
    }

    public void setQuery(CharSequence query) {
        savedStateHandle.set("QUERY", query);
    }

    public LiveData<List<NoteEntity>> getData() {
        return listLiveData;
    }
}
