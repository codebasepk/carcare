package com.byteshaft.carecare.serviceprovidersaccount;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.byteshaft.carecare.R;

public class Login extends Fragment implements View.OnClickListener {

    private View mBaseView;

    private Button mLoginButton;
    private TextView mSignUpTextView;
    private TextView mForgotPasswordTextView;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_user_login, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("Login");

        mEmailEditText = mBaseView.findViewById(R.id.email_edit_text);
        mPasswordEditText = mBaseView.findViewById(R.id.password_edit_text);

        mLoginButton = mBaseView.findViewById(R.id.button_sign_in);
        mSignUpTextView = mBaseView.findViewById(R.id.sign_up_text_view);
        mForgotPasswordTextView = mBaseView.findViewById(R.id.forgot_password_text_view);

        mLoginButton.setOnClickListener(this);
        mSignUpTextView.setOnClickListener(this);
        mForgotPasswordTextView.setOnClickListener(this);
        return mBaseView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_sign_in:
                break;
            case R.id.sign_up_text_view:
                ServiceProviderAccount.getInstance().loadFragment(new SignUp());
                break;
            case R.id.forgot_password_text_view:
                ServiceProviderAccount.getInstance().loadFragment(new ForgetPassword());
                break;
        }
    }
}
