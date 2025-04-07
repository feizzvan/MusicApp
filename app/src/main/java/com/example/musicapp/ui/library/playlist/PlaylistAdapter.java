package com.example.musicapp.ui.library.playlist;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.data.model.Playlist;
import com.example.musicapp.databinding.ItemPlaylistBinding;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    private final List<Playlist> mPlaylists = new ArrayList<>();
    private final onPlaylistItemClickListener mItemClickListener;
    private final onPlaylistOptionMenuClickListener mOptionMenuClickListener;

    public PlaylistAdapter(onPlaylistItemClickListener itemClickListener,
                           onPlaylistOptionMenuClickListener optionMenuClickListener) {
        mItemClickListener = itemClickListener;
        mOptionMenuClickListener = optionMenuClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemPlaylistBinding binding = ItemPlaylistBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding, mItemClickListener, mOptionMenuClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mPlaylists.get(position));
    }

    @Override
    public int getItemCount() {
        return mPlaylists.size();
    }

    void updatePlaylists(List<Playlist> playlists) {
        if (playlists != null) {
            int oldSize = mPlaylists.size();
            mPlaylists.clear();
            mPlaylists.addAll(playlists);
            if (oldSize > mPlaylists.size()) {
                notifyItemRangeRemoved(0, oldSize);
            }
            notifyItemRangeChanged(0, mPlaylists.size());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemPlaylistBinding mBinding;
        private final onPlaylistItemClickListener mItemClickListener;
        private final onPlaylistOptionMenuClickListener mOptionMenuClickListener;

        public ViewHolder(@NonNull ItemPlaylistBinding binding,
                          onPlaylistItemClickListener itemClickListener,
                          onPlaylistOptionMenuClickListener optionMenuClickListener) {
            super(binding.getRoot());
            mBinding = binding;
            mItemClickListener = itemClickListener;
            mOptionMenuClickListener = optionMenuClickListener;
        }

        public void bind(Playlist playlist) {
            mBinding.textItemPlaylistName.setText(playlist.getName());
            String content = mBinding.getRoot().getContext().getString(R.string.text_number_song, playlist.getSongs().size());
            mBinding.textItemPlaylistCount.setText(content);
            Glide.with(mBinding.getRoot())
                    .load(playlist.getArtwork())
                    .error(R.drawable.ic_album)
                    .into(mBinding.imgItemPlaylistAvatar);
            mBinding.getRoot().setOnClickListener(view -> mItemClickListener.onClick(playlist));
            mBinding.btnItemPlaylistOption.setOnClickListener(view ->
                    mOptionMenuClickListener.onClick(playlist));
        }
    }

    public interface onPlaylistItemClickListener {
        void onClick(Playlist playlist);
    }

    public interface onPlaylistOptionMenuClickListener {
        void onClick(Playlist playlist);
    }
}
