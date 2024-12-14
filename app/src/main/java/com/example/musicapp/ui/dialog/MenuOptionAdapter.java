package com.example.musicapp.ui.dialog;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapp.databinding.ItemMenuOptionBinding;

import java.util.ArrayList;
import java.util.List;

public class MenuOptionAdapter extends RecyclerView.Adapter<MenuOptionAdapter.ViewHolder> {

    private final List<MenuOptionItem> mMenuOptionItems = new ArrayList<>();
    private final OnMenuOptionItemClickListener mListener;

    public MenuOptionAdapter(OnMenuOptionItemClickListener mListener) {
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public MenuOptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemMenuOptionBinding binding = ItemMenuOptionBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuOptionAdapter.ViewHolder holder, int position) {
        holder.bind(mMenuOptionItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mMenuOptionItems.size();
    }

    public void updateMenuOptionItems(List<MenuOptionItem> menuOptionItems) {
        mMenuOptionItems.clear();
        mMenuOptionItems.addAll(menuOptionItems);
        notifyItemRangeChanged(0, mMenuOptionItems.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemMenuOptionBinding mBinding;
        private final OnMenuOptionItemClickListener mListener;

        public ViewHolder(ItemMenuOptionBinding binding, OnMenuOptionItemClickListener listener) {
            super(binding.getRoot());
            mBinding = binding;
            mListener = listener;
        }

        public void bind(MenuOptionItem menuOptionItem) {
            mBinding.textMenuOptionItemTitle.setText(menuOptionItem.getMenuItemTitle());
            Glide.with(mBinding.getRoot())
                    .load(menuOptionItem.getIconId())
                    .into(mBinding.imgMenuOptionItemIcon);
            mBinding.getRoot()
                    .setOnClickListener(view -> mListener.onMenuOptionItemClick(menuOptionItem));
        }
    }

    public interface OnMenuOptionItemClickListener {
        void onMenuOptionItemClick(MenuOptionItem menuOptionItem);
    }
}
