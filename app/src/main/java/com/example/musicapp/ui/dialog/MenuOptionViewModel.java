package com.example.musicapp.ui.dialog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapp.data.model.Song;
import com.example.musicapp.utils.MenuOptionUtils;

import java.util.List;

public class MenuOptionViewModel extends ViewModel {
    private final MutableLiveData<List<MenuOptionItem>> mMenuOptionItem = new MutableLiveData<>();
    private final MutableLiveData<Song> mSong = new MutableLiveData<>();

    public MenuOptionViewModel() {
        mMenuOptionItem.setValue(MenuOptionUtils.getSongMenuOptionItems());
    }

    public LiveData<Song> getSong() {
        return mSong;
    }

    public LiveData<List<MenuOptionItem>> getMenuOptionItem() {
        return mMenuOptionItem;
    }

    public void setSong(Song song) {
        mSong.setValue(song);
    }

    public void setMenuOptionItem(List<MenuOptionItem> menuOptionItems) {
        if (menuOptionItems != null) {
            mMenuOptionItem.setValue(menuOptionItems);
        }
    }
}
