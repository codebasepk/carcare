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

public class ForgetPassword extends Fragment implements View.OnClickListener, HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {
    private View mBaseView;
    private EditText mEmail;
    private Button mRecoverButton;
    private String mEmailString;
    private HttpRequest request;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_user_forget_password, container, false);
        mEmail = mBaseView.findViewById(R.id.email_edit_text);
        mRecoverButton = mBaseView.findViewById(R.id.button_recover);

        mEmail.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL));
        mEmailString = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL);

        mRecoverButton.setOnClickListener(this);
        return mBaseView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_recover:
                if (validate()) {
                    recoverUserPassword(mEmailString);
                }
                break;
        }
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(getActivity(), getString(R.string.recovery_failed), getString(R.string.check_internet));
                        break;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        AppGlobals.alertDialog(getActivity(), getString(R.string.recovery_failed), getString(R.string.valid_email));
                        break;
                    case HttpURLConnection.HTTP_OK:
                        Toast.makeText(getActivity(), R.string.recovery_message, Toast.LENGTH_LONG).show();
                        UserAccount.getInstance().loadFragment(new ResetPassword());
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

    private void recoverUserPassword(String email) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sforgot-password", AppGlobals.BASE_URL));
        request.send(getUserPassword(email));
        Helpers.showProgressDialog(getActivity(), getString(R.string.recovery_email));
    }


    private String getUserPassword(String email) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private boolean validate() {
        boolean valid = true;
        mEmailString = mEmail.getText().toString();
        System.out.println(mEmailString);

        if (mEmailString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailString).matches()) {
            mEmail.setError(getString(R.string.email_error));
            valid = false;
        } else {
            mEmail.setError(null);
        }
        return valid;
    }
}

