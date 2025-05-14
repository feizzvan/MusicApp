package com.example.musicapp.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.R;
import com.example.musicapp.databinding.FragmentRegisterBinding;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentRegisterBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        setupView();
    }

    private void setupView() {
        mBinding.btnRegister.setOnClickListener(view -> handleRegister());
        mBinding.actionMoveLogin.setOnClickListener(view -> {
            NavDirections directions = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment();
            NavHostFragment.findNavController(RegisterFragment.this).navigate(directions);
        });
    }

    private void handleRegister() {
        String fullName = mBinding.edtFullName.getText().toString().trim();
        String email = mBinding.edtEmail.getText().toString().trim();
        String password = mBinding.edtPassword.getText().toString().trim();



    }
}