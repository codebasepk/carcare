package com.byteshaft.carecare.serviceprovidersaccount;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.byteshaft.carecare.MainActivity;
import com.byteshaft.carecare.R;
import com.byteshaft.carecare.WelcomeActivity;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class Login extends Fragment implements View.OnClickListener, HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    private View mBaseView;

    private Button mLoginButton;
    private TextView mSignUpTextView;
    private TextView mForgotPasswordTextView;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    private String mEmailString;
    private String mPasswordString;

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
                if (validate()) {
                    loginUser(mEmailString, mPasswordString);
                }
                break;
            case R.id.sign_up_text_view:
                ServiceProviderAccount.getInstance().loadFragment(new SignUp());
                break;
            case R.id.forgot_password_text_view:
                ServiceProviderAccount.getInstance().loadFragment(new ForgetPassword());
                break;
        }
    }

    public boolean validate() {
        boolean valid = true;
        mEmailString = mEmailEditText.getText().toString();
        mPasswordString = mPasswordEditText.getText().toString();
        if (mEmailString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailString).matches()) {
            mEmailEditText.setError("Please provide a valid email");
            valid = false;
        } else {
            mEmailEditText.setError(null);
        }

        if (mPasswordString.isEmpty() || mPasswordEditText.length() < 4) {
            mPasswordEditText.setError("Enter minimum 4 alphanumeric characters");
            valid = false;
        } else {
            mPasswordEditText.setError(null);
        }
        return valid;
    }


    private void loginUser(String email, String password) {
        HttpRequest request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%slogin", AppGlobals.BASE_URL));
        request.send(getUserLoginData(email, password));
        Helpers.showProgressDialog(getActivity(), "Logging In");
    }


    private String getUserLoginData(String email, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
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
                    case HttpURLConnection.HTTP_OK:
                        Log.i("ON OK Code confirm", request.getResponseText());
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
                            String address = jsonObject.getString(AppGlobals.KEY_ADDRESS);
                            String contactNumber = jsonObject.getString(AppGlobals.KEY_CONTACT_NUMBER);
//                            String contactPerson = jsonObject.getString(AppGlobals.KEY_CONTACT_PERSON);
                            String email = jsonObject.getString(AppGlobals.KEY_EMAIL);
                            String id = jsonObject.getString(AppGlobals.KEY_USER_ID);
                            String organizationName = jsonObject.getString(AppGlobals.KEY_ORGANIZATION_NAME);
//                            if (jsonObject.getString(AppGlobals.KEY_SERVER_IMAGE) != null) {
//                                String profilePhoto = jsonObject.getString(AppGlobals.KEY_SERVER_IMAGE);
//                                AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_SERVER_IMAGE, profilePhoto);
//                            }

                            String token = jsonObject.getString(AppGlobals.KEY_TOKEN);
                            String userName = jsonObject.getString(AppGlobals.KEY_USER_NAME);
                            String userType = jsonObject.getString(AppGlobals.KEY_USER_TYPE);

                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_CONTACT_NUMBER, contactNumber);
//                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_CONTACT_PERSON, contactPerson);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_ADDRESS, address);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID, id);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_ORGANIZATION_NAME, organizationName);

                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_TOKEN, token);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_NAME, userName);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_TYPE, userType);

                            startActivity(new Intent(getContext(), MainActivity.class));
                            ServiceProviderAccount.getInstance().finish();
                            WelcomeActivity.getInstance().finish();
                            AppGlobals.loginState(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case HttpURLConnection.HTTP_FORBIDDEN:
                        // TODO: 29/05/2018 User Not active
                        ServiceProviderAccount.getInstance().loadFragment(new CodeConfrimation());
                        AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, mEmailString);
                        break;
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        // TODO: 29/05/2018 Enter a valid Email Address
                }
        }
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }
}
