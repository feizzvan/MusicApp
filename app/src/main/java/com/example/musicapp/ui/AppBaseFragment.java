package com.example.musicapp.ui;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicapp.data.model.Song;
import com.example.musicapp.ui.dialog.OptionMenuViewModel;
import com.example.musicapp.ui.dialog.SongOptionMenuDialogFragment;
import com.example.musicapp.ui.viewmodel.SharedViewModel;

public class AppBaseFragment extends Fragment {

    protected void showAndPlay(Song song, int index, String playlistName) {
        SharedViewModel.getInstance().setCurrentPlaylist(playlistName);
        SharedViewModel.getInstance().setIndexToPlay(index);
    }

    protected void showMenuOption(Song song) {
        OptionMenuViewModel optionMenuViewModel =
                new ViewModelProvider(requireActivity()).get(OptionMenuViewModel.class);
        optionMenuViewModel.setSong(song);
        SongOptionMenuDialogFragment dialog = SongOptionMenuDialogFragment.newInstance();
        dialog.show(requireActivity().getSupportFragmentManager(), SongOptionMenuDialogFragment.TAG);
    }
}
