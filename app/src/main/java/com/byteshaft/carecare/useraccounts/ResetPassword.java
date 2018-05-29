package com.byteshaft.carecare.useraccounts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class ResetPassword extends Fragment implements View.OnClickListener, HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    private View mBaseView;
    private EditText mEmail;
    private EditText mOldPassword;
    private EditText mNewPassword;
    private EditText mVerifyPassword;
    private Button mUpdateButton;
    private String mOldPasswordString;
    private String mEmailString;
    private String mNewPasswordString;
    private String mVerifyPasswordString;
    private HttpRequest request;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_user_change_password, container, false);
        mEmail = mBaseView.findViewById(R.id.email_edit_text);
        mOldPassword = mBaseView.findViewById(R.id.old_password_edit_text);
        mNewPassword = mBaseView.findViewById(R.id.new_password_edit_text);
        mVerifyPassword = mBaseView.findViewById(R.id.confirm_password_edit_text);
        mUpdateButton = mBaseView.findViewById(R.id.button_update);

        mUpdateButton.setOnClickListener(this);

        return mBaseView;
    }

    @Override
    public void onClick(View v) {
        if (validateEditText()) {
            changePassword(mEmailString, mOldPasswordString, mNewPasswordString);

        }

    }

    private boolean validateEditText() {
        boolean valid = true;
        mEmailString = mEmail.getText().toString();
        mOldPasswordString = mOldPassword.getText().toString();
        mNewPasswordString = mNewPassword.getText().toString();
        mVerifyPasswordString = mVerifyPassword.getText().toString();

        if (mOldPasswordString.trim().isEmpty() || mOldPasswordString.length() < 4) {
            mOldPassword.setError(getString(R.string.old_password));
            valid = false;
        } else {
            mOldPassword.setError(null);
        }

        if (mNewPasswordString.trim().isEmpty() || mNewPasswordString.length() < 4) {
            mNewPassword.setError(getString(R.string.password_four_length));
            valid = false;
        } else {
            mNewPassword.setError(null);
        }

        if (mVerifyPasswordString.trim().isEmpty() || mVerifyPasswordString.length() < 4 ||
                !mVerifyPasswordString.equals(mNewPasswordString)) {
            mVerifyPassword.setError(getString(R.string.password_not_match));
            valid = false;
        } else {
            mVerifyPassword.setError(null);
        }
        return valid;
    }

    private void changePassword(String email, String oldPassword, String newPassword) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%schange-password", AppGlobals.BASE_URL));
        request.send(getUserChangePassword(email, oldPassword, newPassword));
        Helpers.showProgressDialog(getActivity(), getString(R.string.updating_password));

    }

    private String getUserChangePassword(String email, String oldPassword, String newPassword) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("email_otp", oldPassword);
            jsonObject.put("new_password", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
            switch (readyState) {
                case HttpRequest.STATE_DONE:
                    Helpers.dismissProgressDialog();
                    switch (request.getStatus()) {
                        case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                            AppGlobals.alertDialog(getActivity(), getString(R.string.resetting_failed),  getString(R.string.check_internet));
                            break;
                        case HttpURLConnection.HTTP_BAD_REQUEST:
                            AppGlobals.alertDialog(getActivity(), getString(R.string.resetting_failed), getString(R.string.old_password));
                            break;
                        case HttpURLConnection.HTTP_NOT_FOUND:
                            AppGlobals.alertDialog(getActivity(), getString(R.string.resetting_failed), getString(R.string.email_not_exist));
                            break;
                        case HttpURLConnection.HTTP_OK:
                            System.out.println(request.getResponseText() + "working ");
                            UserAccount.getInstance().loadFragment(new UserLogin());
                            Toast.makeText(getActivity(), R.string.your_password_changed, Toast.LENGTH_SHORT).show();
                    }
            }

    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        Helpers.dismissProgressDialog();
        switch (readyState) {
            case HttpRequest.ERROR_CONNECTION_TIMED_OUT:
                Helpers.showSnackBar(getView(), getString(R.string.connection_time_out));
                break;
            case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                Helpers.showSnackBar(getView(), exception.getLocalizedMessage());
                break;
        }

    }
}
