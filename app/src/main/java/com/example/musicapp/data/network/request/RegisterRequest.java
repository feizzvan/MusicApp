package com.example.musicapp.data.network.request;

public class RegisterRequest {
    private String mEmail;
    private String mPassword;

    private final String role = "CUSTOMER";

    private String mFullName;

    public RegisterRequest(String email, String password, String fullName) {
        mEmail = email;
        mPassword = password;
        mFullName = fullName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String fullName) {
        mFullName = fullName;
    }
}
