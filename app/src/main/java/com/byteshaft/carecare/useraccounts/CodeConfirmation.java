package com.byteshaft.carecare.useraccounts;

import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.serviceprovidersaccount.Login;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class CodeConfirmation extends Fragment implements HttpRequest.OnReadyStateChangeListener,
        HttpRequest.OnErrorListener, View.OnClickListener {

    private View mBaseView;
    private Button mActivateButton;
    private EditText mConfirmationCodeEditText;
    private EditText mEmailEditText;
    private TextView mResendVerificationTextView;

    private String mEmailString;
    private String mVerificationCodeString;

    private HttpRequest request;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_user_code_confirmation, container, false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mEmailEditText = mBaseView.findViewById(R.id.email_edit_text);
        mConfirmationCodeEditText = mBaseView.findViewById(R.id.otp_edit_text);
        mActivateButton = mBaseView.findViewById(R.id.button_activate);
        mResendVerificationTextView = mBaseView.findViewById(R.id.resend_verification_text_view);

        mEmailEditText.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL));
        mEmailString = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL);

        mActivateButton.setOnClickListener(this);
        mResendVerificationTextView.setOnClickListener(this);
        return mBaseView;
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        Toast.makeText(getActivity(), "Please enter correct account activation key", Toast.LENGTH_LONG).show();
                        break;
                    case HttpURLConnection.HTTP_OK:
                        System.out.println(request.getResponseText() + "working ");
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
                            String userId = jsonObject.getString(AppGlobals.KEY_USER_ID);
                            String token = jsonObject.getString(AppGlobals.KEY_TOKEN);
                            String email = jsonObject.getString(AppGlobals.KEY_EMAIL);
                            String fullName = jsonObject.getString(AppGlobals.KEY_FULL_NAME);
                            String userName = jsonObject.getString(AppGlobals.KEY_USER_NAME);

                            String contactNumber = jsonObject.getString(AppGlobals.KEY_CONTACT_NUMBER);
                            String vehicleModel = jsonObject.getString(AppGlobals.KEY_VEHICLE_MODEL);

                            String vehicleYear = jsonObject.getString(AppGlobals.KEY_VEHICLE_YEAR);
                            String location = jsonObject.getString(AppGlobals.KEY_LOCATION);
                            String address = jsonObject.getString(AppGlobals.KEY_ADDRESS);
                            String profilePhoto = jsonObject.getString(AppGlobals.KEY_SERVER_IMAGE);

                            JSONObject vehicleTypeJSONObject = jsonObject.getJSONObject("vehicle_type");
                            String vehicleTypeId = vehicleTypeJSONObject.getString(AppGlobals.KEY_VEHICLE_TYPE_SERVER_ID);
                            String vehicleTypeName = vehicleTypeJSONObject.getString(AppGlobals.KEY_VEHICLE_TYPE_NAME);
                            //saving values
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_VEHICLE_TYPE_ID, vehicleTypeId);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_VEHICLE_TYPE_SERVER_NAME, vehicleTypeName);

                            JSONObject vehicleMakeJSONObject = jsonObject.getJSONObject("vehicle_make");
                            String vehicleMakeId = vehicleMakeJSONObject.getString(AppGlobals.KEY_VEHICLE_MAKE_SERVER_ID);
                            String vehicleMakeName = vehicleMakeJSONObject.getString(AppGlobals.KEY_VEHICLE_MAKE_SERVER_NAME);
                            //saving values
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_VEHICLE_MAKE_ID, vehicleMakeId);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_VEHICLE_MAKE_NAME, vehicleMakeName);


                            //saving values
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID, userId);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_TOKEN, token);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_FULL_NAME, fullName);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_CONTACT_NUMBER, contactNumber);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_NAME, userName);

                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_VEHICLE_MODEL, vehicleModel);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_VEHICLE_YEAR, vehicleYear);

                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_LOCATION, location);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_SERVER_IMAGE, profilePhoto);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_ADDRESS, address);
                            AppGlobals.loginState(true);
                            UserAccount.getInstance().loadFragment(new UserLogin());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_activate:
                if (validate()) {
                    activateUser(mEmailString, mVerificationCodeString);
                }
                break;
            case R.id.resend_verification_text_view:
                resendOtp(mEmailString);
                break;
        }
    }

    public boolean validate() {
        boolean valid = true;
        mEmailString = mEmailEditText.getText().toString();
        mVerificationCodeString = mConfirmationCodeEditText.getText().toString();

        System.out.println(mEmailString);
        System.out.println(mVerificationCodeString);

        if (mEmailString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailString).matches()) {
            mEmailEditText.setError(getString(R.string.provide_email));
            valid = false;
        } else {
            mEmailEditText.setError(null);
        }
        if (mVerificationCodeString.trim().isEmpty() || mVerificationCodeString.length() < 6) {
            mConfirmationCodeEditText.setError(getString(R.string.verification_code_length));
            valid = false;
        } else {
            mConfirmationCodeEditText.setError(null);
        }
        return valid;
    }

    private void activateUser(String email, String emailOtp) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sactivate", AppGlobals.BASE_URL));
        request.send(getUserActivationData(email, emailOtp));
        Helpers.showProgressDialog(getActivity(), "Activating User");
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

    private void resendOtp(String email) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener((request, readyState) -> {
            switch (readyState) {
                case HttpRequest.STATE_DONE:
                    Helpers.dismissProgressDialog();
                    switch (request.getStatus()) {
                        case HttpURLConnection.HTTP_BAD_REQUEST:
                            break;
                        case HttpURLConnection.HTTP_OK:
                    }
            }

        });
        request.setOnErrorListener(new HttpRequest.OnErrorListener() {
            @Override
            public void onError(HttpRequest request, int readyState, short error, Exception exception) {

            }
        });
        request.open("POST", String.format("%srequest-activation-key", AppGlobals.BASE_URL));
        request.send(getOtpData(email));
        Helpers.showProgressDialog(getActivity(), "Resending OTP");
    }

    private String getOtpData(String email) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
