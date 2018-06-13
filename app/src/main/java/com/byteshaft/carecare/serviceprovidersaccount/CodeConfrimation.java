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
import android.widget.Toast;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.ServiceProviderActivity;
import com.byteshaft.carecare.WelcomeActivity;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class CodeConfrimation extends Fragment implements View.OnClickListener {

    private View mBaseView;
    private EditText mEmail;
    private EditText mVerificationCode;
    private Button mLoginButton;
    //    private TextView mSignTextView;
    private TextView mResendTextView;
    private String mEmailString;
    private String mVerificationCodeString;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_user_code_confirmation, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("Account Activation");
        mEmail = mBaseView.findViewById(R.id.email_edit_text);
        mVerificationCode = mBaseView.findViewById(R.id.otp_edit_text);
        mEmail.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL));
        mLoginButton = mBaseView.findViewById(R.id.button_activate);
        mResendTextView = mBaseView.findViewById(R.id.resend_verification_text_view);
        mLoginButton.setOnClickListener(this);
        mResendTextView.setOnClickListener(this);

        return mBaseView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_activate:
                if (validateFroResend()) {
                    activateUser(mEmailString, mVerificationCodeString);
                }
                break;
            case R.id.resend_verification_text_view:
                resendVerificationCOde(mEmail.getText().toString());
                break;
        }
    }

    public boolean validateFroResend() {
        boolean valid = true;
        mEmailString = mEmail.getText().toString();
        mVerificationCodeString = mVerificationCode.getText().toString();

        System.out.println(mEmailString);
        System.out.println(mVerificationCodeString);

        if (mEmailString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailString).matches()) {
            mEmail.setError("please provide a valid email");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        if (mVerificationCodeString.trim().isEmpty() && mVerificationCodeString.length() < 6) {
            mVerificationCode.setError("Provide 6 digit code");
            valid = false;
        } else {
            mVerificationCode.setError(null);
        }
        return valid;
    }

    private void resendVerificationCOde(String email) {
        Helpers.showProgressDialog(getActivity(), "Resending Verification code ");
        HttpRequest request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        Log.wtf("Key Resent", request.getResponseText());
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_BAD_REQUEST:
                                Toast.makeText(getActivity(), "Please enter correct account Verification code", Toast.LENGTH_LONG).show();
                                break;
                            case HttpURLConnection.HTTP_OK:
                                AppGlobals.alertDialog(getActivity(), "Code Resent!", "Verification code has been sent to you! Please check your Email");
                                break;
                            case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                                AppGlobals.alertDialog(getActivity(), "Sending Failed!", "please check your internet connection !");
                                break;
                            case HttpURLConnection.HTTP_NOT_FOUND:
                                AppGlobals.alertDialog(getActivity(), "Sending Failed!", "provide a valid EmailAddress !");
                                break;
                            case HttpURLConnection.HTTP_FORBIDDEN:
                                AppGlobals.alertDialog(getActivity(), "Sending Failed!", "User deactivated by admin !");
                                break;
                            case HttpURLConnection.HTTP_NOT_MODIFIED:
                                AppGlobals.alertDialog(getActivity(), "Sending Failed!", "Your account is already activated !");
                                break;

                        }
                }
            }
        });
        request.setOnErrorListener(new HttpRequest.OnErrorListener() {
            @Override
            public void onError(HttpRequest request, int readyState, short error, Exception exception) {

            }
        });
        request.open("POST", String.format("%srequest-activation-key", AppGlobals.BASE_URL));
        request.send(getresendVerificationData(email));
    }

    private String getresendVerificationData(String email) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    private void activateUser(String email, String emailOtp) {
        Helpers.showProgressDialog(getActivity(), "Activating User");
        HttpRequest request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        Log.i("TAG", request.getResponseText());
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_BAD_REQUEST:
                                Toast.makeText(getActivity(), "Please enter correct account Verification code", Toast.LENGTH_LONG).show();
                                break;
                            case HttpURLConnection.HTTP_OK:
                                Log.i("ON OK Code confirm", request.getResponseText());
                                try {
                                    JSONObject jsonObject = new JSONObject(request.getResponseText());
                                    String address = jsonObject.getString(AppGlobals.KEY_ADDRESS);
                                    String contactNumber = jsonObject.getString(AppGlobals.KEY_CONTACT_NUMBER);
                                    String contactPerson = jsonObject.getString(AppGlobals.KEY_CONTACT_PERSON);
                                    String email = jsonObject.getString(AppGlobals.KEY_EMAIL);
                                    String id = jsonObject.getString(AppGlobals.KEY_USER_ID);
                                    String organizationName = jsonObject.getString(AppGlobals.KEY_ORGANIZATION_NAME);
                                    String profilePhoto = jsonObject.getString(AppGlobals.KEY_SERVER_IMAGE);
                                    String token = jsonObject.getString(AppGlobals.KEY_TOKEN);
                                    String userName = jsonObject.getString(AppGlobals.KEY_USER_NAME);
                                    String userType = jsonObject.getString(AppGlobals.KEY_USER_TYPE);
                                    String addressCoordinates = jsonObject.getString(AppGlobals.KEY_LOCATION);

                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_CONTACT_NUMBER, contactNumber);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_CONTACT_PERSON, contactPerson);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_ADDRESS, address);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_LOCATION, addressCoordinates);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID, id);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_ORGANIZATION_NAME, organizationName);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_SERVER_IMAGE, profilePhoto);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_TOKEN, token);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_NAME, userName);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_TYPE, userType);
                                    startActivity(new Intent(getContext(), ServiceProviderActivity.class));
                                    ServiceProviderAccount.getInstance().finish();
                                    WelcomeActivity.getInstance().finish();
                                    AppGlobals.loginState(true);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        request.setOnErrorListener(new HttpRequest.OnErrorListener() {
            @Override
            public void onError(HttpRequest request, int readyState, short error, Exception exception) {

            }
        });
        request.open("POST", String.format("%sactivate", AppGlobals.BASE_URL));
        request.send(getUserActivationData(email, emailOtp));
    }


    private String getUserActivationData(String email, String emailOtp) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("email_otp", emailOtp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
