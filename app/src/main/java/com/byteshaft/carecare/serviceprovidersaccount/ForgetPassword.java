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

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.requests.HttpRequest;

public class ForgetPassword extends Fragment implements View.OnClickListener {

    private View mBaseView;
    private HttpRequest request;
    private Button mRecoverButton;
    private EditText mUserNameEditText;
    private String mUserNameString;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_user_forget_password, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("Forgot Password");
        mUserNameEditText = mBaseView.findViewById(R.id.email_edit_text);
        mRecoverButton = mBaseView.findViewById(R.id.button_recover);
        mRecoverButton.setOnClickListener(this);
        mUserNameEditText.setTypeface(AppGlobals.typefaceNormal);
        mRecoverButton.setTypeface(AppGlobals.typefaceNormal);
        mUserNameEditText.requestFocus();

        return mBaseView;
    }

    @Override
    public void onClick(View view) {

    }
}
