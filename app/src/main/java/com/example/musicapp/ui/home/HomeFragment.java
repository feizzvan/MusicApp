package com.example.musicapp.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    public static final String SCROLL_POSITION = "com.example.musicapp.ui.home.SCROLL_POSITION";
    private FragmentHomeBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            int scrollY = savedInstanceState.getInt(SCROLL_POSITION);
//            post(...) giúp chờ cho đến khi layout hiển thị xong rồi mới thực hiện scrollTo(...).
//            Nếu gọi scrollTo() ngay lập tức, có thể ScrollView chưa kịp đo kích thước và layout → cuộn không đúng.
            mBinding.scrollViewHome.post(() -> mBinding.scrollViewHome.scrollTo(0, scrollY));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mBinding == null) {
            return;
        }
        int scrollY = mBinding.scrollViewHome.getScrollY();
        outState.putInt(SCROLL_POSITION, scrollY);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}