package com.example.musicapp.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.musicapp.R;
import com.example.musicapp.data.network.request.RegisterRequest;
import com.example.musicapp.data.network.response.AuthenticationResponse;
import com.example.musicapp.data.source.remote.AppService;
import com.example.musicapp.data.source.remote.RetrofitHelper;
import com.example.musicapp.databinding.FragmentRegisterBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding mBinding;
    private final AppService appService = RetrofitHelper.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentRegisterBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupView();
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
        String confirmPassword = mBinding.edtConfirmPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getContext(), R.string.text_please_fill_in_all_information, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), R.string.text_invalid_email_format, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPassword(password)) {
            Toast.makeText(getContext(), R.string.text_invalid_password_format, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), R.string.text_confirmation_password_does_not_match, Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest request = new RegisterRequest(email, password, fullName);

        appService.register(request).enqueue(new Callback<AuthenticationResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthenticationResponse> call,
                                   @NonNull Response<AuthenticationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), R.string.text_registration_successful, Toast.LENGTH_SHORT).show();
                    NavDirections directions = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment();
                    NavHostFragment.findNavController(RegisterFragment.this).navigate(directions);
                } else {
                    Toast.makeText(getContext(), R.string.text_email_already_exists, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthenticationResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), R.string.text_network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$";
        return password.matches(passwordPattern);
    }
}