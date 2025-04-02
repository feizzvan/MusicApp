package com.example.musicapp.ui;

import android.content.Intent;

import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicapp.R;
import com.example.musicapp.data.model.Song;
import com.example.musicapp.ui.dialog.OptionMenuViewModel;
import com.example.musicapp.ui.dialog.SongOptionMenuDialogFragment;
import com.example.musicapp.ui.playing.NowPlayingActivity;
import com.example.musicapp.ui.viewmodel.PermissionViewModel;
import com.example.musicapp.ui.viewmodel.SharedViewModel;

public class AppBaseFragment extends Fragment {

    protected void showAndPlay(Song song, int index, String playlistName) {
//        Boolean isPermissionGranted = PermissionViewModel.getInstance()
//                .getPermissionGranted()
//                .getValue();
//        if (isPermissionGranted != null && isPermissionGranted) {
//            doNavigate(index, playlistName);
//        } else if (!PermissionViewModel.isRegistered) {
//            PermissionViewModel.getInstance()
//                    .getPermissionGranted()
//                    .observe(requireActivity(), isGranted -> {
//                        if (isGranted) {
//                            doNavigate(index, playlistName);
//                        }
//                    });
//            PermissionViewModel.isRegistered = true;
//        }

        doNavigate(index, playlistName);
    }

    protected void showMenuOption(Song song) {
        OptionMenuViewModel optionMenuViewModel =
                new ViewModelProvider(requireActivity()).get(OptionMenuViewModel.class);
        optionMenuViewModel.setSong(song);
        SongOptionMenuDialogFragment dialog = SongOptionMenuDialogFragment.newInstance();
        dialog.show(requireActivity().getSupportFragmentManager(), SongOptionMenuDialogFragment.TAG);
    }

    private void doNavigate(int index, String playlistName) {
        SharedViewModel.getInstance().setCurrentPlaylist(playlistName);
        SharedViewModel.getInstance().setIndexToPlay(index);

        Intent intent = new Intent(requireContext(), NowPlayingActivity.class);
//        intent.putExtra(AppUtils.EXTRA_CURRENT_FRACTION, mRotationAnimator.getAnimatedFraction());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeCustomAnimation(requireContext(), R.anim.slide_up, R.anim.fade_out);
//        nowPlayingActivityLauncher.launch(intent, options);
        requireContext().startActivity(intent, options.toBundle());
    }
}
