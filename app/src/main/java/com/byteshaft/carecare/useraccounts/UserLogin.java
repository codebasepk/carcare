package com.byteshaft.carecare.useraccounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.ServiceProviderActivity;
import com.byteshaft.carecare.WelcomeActivity;
import com.byteshaft.carecare.serviceprovidersaccount.ServiceProviderAccount;
import com.byteshaft.carecare.userFragments.UserHomeFragment;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class UserLogin extends Fragment implements HttpRequest.OnReadyStateChangeListener,
        HttpRequest.OnErrorListener, View.OnClickListener {

    private View mBaseView;

    private EditText mEmail;
    private EditText mPassword;
    private Button mLoginButton;
    private TextView mForgotPasswordTextView;
    private TextView mSignUpTextView;
    private String mPasswordString;
    private String mEmailString;
    private HttpRequest request;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_user_login, container, false);
        mEmail = mBaseView.findViewById(R.id.email_edit_text);
        mPassword = mBaseView.findViewById(R.id.password_edit_text);
        mLoginButton = mBaseView.findViewById(R.id.button_sign_in);
        mForgotPasswordTextView = mBaseView.findViewById(R.id.forgot_password_text_view);
        mSignUpTextView = mBaseView.findViewById(R.id.sign_up_text_view);

        mLoginButton.setOnClickListener(this);
        mSignUpTextView.setOnClickListener(this);
        mForgotPasswordTextView.setOnClickListener(this);

        mEmail.setTypeface(AppGlobals.typefaceNormal);
        mPassword.setTypeface(AppGlobals.typefaceNormal);
        mLoginButton.setTypeface(AppGlobals.typefaceNormal);
        mForgotPasswordTextView.setTypeface(AppGlobals.typefaceNormal);
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
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(getActivity(), "Login Failed!", "please check your internet connection");
                        break;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        AppGlobals.alertDialog(getActivity(), "Login Failed!", "provide a valid EmailAddress");
                        break;
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        AppGlobals.alertDialog(getActivity(), "Login Failed!", "Please enter correct password");
                        break;
                    case HttpURLConnection.HTTP_FORBIDDEN:
//                        System.out.println("LOgin" +AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_NAME));
//                        Toast.makeText(getActivity(), "Please activate your account !", Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(getApplicationContext(), CodeConfirmationActivity.class);
//                        startActivity(intent);
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
                            String userType = jsonObject.getString(AppGlobals.KEY_USER_TYPE);

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
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_TYPE, userType);
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
                            startActivity(new Intent(getActivity(), UserAccount.class));
                            UserAccount.getInstance().finish();
                            WelcomeActivity.getInstance().finish();
                            AppGlobals.loginState(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }

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
                Log.wtf("cok", "okkm cli");
                UserAccount.getInstance().loadFragment(new UserSignUp());
                break;
            case R.id.forgot_password_text_view:
                UserAccount.getInstance().loadFragment(new ForgetPassword());
                break;

        }
    }

    public boolean validate() {
        boolean valid = true;
        mEmailString = mEmail.getText().toString();
        mPasswordString = mPassword.getText().toString();
        if (mEmailString.trim().isEmpty()) {
            mEmail.setError("enter a valid email or username");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        if (mPasswordString.isEmpty() || mPassword.length() < 4) {
            mPassword.setError("Enter minimum 4 alphanumeric characters");
            valid = false;
        } else {
            mPassword.setError(null);
        }
        return valid;
    }

    private void loginUser(String email, String password) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%slogin", AppGlobals.BASE_URL));
        request.send(getUserLoginData(email, password));
        Helpers.showProgressDialog(getActivity(), "Logging In");
    }


    private String getUserLoginData(String email, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(
                    mEmailString).matches()) {
                jsonObject.put("email", email);
            } else {
                jsonObject.put("username", email);
            }
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }

}
