package com.example.musicapp.ui.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.example.musicapp.R;
import com.example.musicapp.data.network.request.LoginRequest;
import com.example.musicapp.data.network.response.AuthenticationResponse;
import com.example.musicapp.data.source.remote.AppService;
import com.example.musicapp.databinding.FragmentLoginBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding mBinding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentLoginBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupView();
    }

    private void setupView() {
        mBinding.btnLogin.setOnClickListener(view -> handleLogin());
        mBinding.actionMoveSignUp.setOnClickListener(view -> {
            NavDirections directions = LoginFragmentDirections.actionLoginFragmentToRegisterFragment();
            NavHostFragment.findNavController(LoginFragment.this).navigate(directions);
        });
    }

    public void handleLogin() {
        String email = mBinding.edtEmail.getText().toString().trim();
        String password = mBinding.edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), R.string.text_please_fill_in_all_information, Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest request = new LoginRequest(email, password);

        AppService.apiService.login(request).enqueue(new Callback<AuthenticationResponse>() {
            @Override
            public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccessToken();

                    // Lưu token vào SharedPreferences
                    SharedPreferences preferences = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE);
                    preferences.edit().putString("access_token", token).apply();

                    Toast.makeText(requireContext(), R.string.text_login_successful, Toast.LENGTH_SHORT).show();
                    NavDirections directions = LoginFragmentDirections.actionLoginFragmentToMainActivity();
                    NavHostFragment.findNavController(LoginFragment.this).navigate(directions);
                } else {
                    Toast.makeText(requireContext(), R.string.text_please_check_your_login_information_again, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

