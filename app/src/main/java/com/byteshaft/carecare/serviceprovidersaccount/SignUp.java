package com.byteshaft.carecare.serviceprovidersaccount;

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
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class SignUp extends Fragment implements View.OnClickListener, HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    private View mBaseView;
    private EditText etOrganizationName, etUsername, etEmail, etContactNumber, etContactPerson, etPassword,
            etAddress, etVerifyPassword;

    private Button mButtonCreateAccoutn;
    private TextView mButtonLogin;

    private String organizationName, username, email, contactNumber, contactPerson, password, address, verifyPassword;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_service_provider_sign_up, container, false);
        etOrganizationName = mBaseView.findViewById(R.id.organization_edit_text);
        etUsername = mBaseView.findViewById(R.id.username_edit_text);
        etEmail = mBaseView.findViewById(R.id.email_edit_text);
        etContactNumber = mBaseView.findViewById(R.id.contact_number_edit_text);
        etContactPerson = mBaseView.findViewById(R.id.contact_person_edit_text);
        etPassword = mBaseView.findViewById(R.id.password_edit_text);
        etAddress = mBaseView.findViewById(R.id.address_edit_text);
        etVerifyPassword = mBaseView.findViewById(R.id.verify_password_edit_text);


        mButtonCreateAccoutn = mBaseView.findViewById(R.id.register_button);
        mButtonLogin = mBaseView.findViewById(R.id.sign_up_text_view);
        mButtonCreateAccoutn.setOnClickListener(this);
        mButtonLogin.setOnClickListener(this);
        return mBaseView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_button:
                if (validateEditText()) {
                    registerUser(organizationName, email, username, contactPerson, contactNumber, address, password, "Provider");
                }
                break;
            case R.id.sign_up_text_view:
                ServiceProviderAccount.getInstance().loadFragment(new Login());
                break;
        }
    }

    private boolean validateEditText() {
        boolean valid = true;
        organizationName = etOrganizationName.getText().toString();
        username = etUsername.getText().toString();
        email = etEmail.getText().toString();
        contactNumber = etContactNumber.getText().toString();
        contactPerson = etContactPerson.getText().toString();
        password = etPassword.getText().toString();
        verifyPassword = etVerifyPassword.getText().toString();
        address = etAddress.getText().toString();

        if (organizationName.trim().isEmpty()) {
            etOrganizationName.setError("required");
            valid = false;
        } else {
            etOrganizationName.setError(null);
        }

        if (username.trim().isEmpty()) {
            etUsername.setError("required");
            valid = false;
        } else {
            etUsername.setError(null);
        }

        if (contactNumber.trim().isEmpty()) {
            etContactNumber.setError("required");
            valid = false;
        } else {
            etContactNumber.setError(null);
        }

        if (contactPerson.trim().isEmpty()) {
            etContactPerson.setError("required");
            valid = false;
        } else {
            etContactPerson.setError(null);
        }

        if (address.trim().isEmpty()) {
            etAddress.setError("required");
            valid = false;
        } else {
            etAddress.setError(null);
        }

        if (email.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please provide a valid email");
            valid = false;
        } else {
            etEmail.setError(null);
        }
        if (password.trim().isEmpty() || password.length() < 4) {
            etPassword.setError("enter at least 4 characters");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        if (verifyPassword.trim().isEmpty() || verifyPassword.length() < 4 ||
                !password.equals(verifyPassword)) {
            etVerifyPassword.setError("password does not match");
            valid = false;
        } else {
            etVerifyPassword.setError(null);
        }
        return valid;
    }

    private void registerUser(String name, String email, String userName, String contactPerson,
                              String contactNumber, String address, String password,
                              String accountType) {
        HttpRequest request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sregister-provider", AppGlobals.BASE_URL));
        request.send(getRegisterData(name, email, userName, contactPerson, contactNumber, address, password, accountType));
        Helpers.showProgressDialog(getActivity(), "Registering...");
    }

    private String getRegisterData(String name, String email, String userName, String contactPerson,
                                   String contactNumber, String address, String password,
                                   String accountType) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account_type", accountType);
            jsonObject.put("name", name);
            jsonObject.put("username", userName);
            jsonObject.put("email", email);
            jsonObject.put("address", address);
            jsonObject.put("contact_person", contactPerson);
            jsonObject.put("contact_number", contactNumber);
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
                Log.wtf("Done", request.getResponseText());
                Helpers.dismissProgressDialog();
                Log.i("TAG", "Response " + request.getResponseText());
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_CREATED:
                        Log.wtf("Created", request.getResponseText());
                }
        }
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        Helpers.dismissProgressDialog();
    }
}
