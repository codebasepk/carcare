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
import android.widget.Spinner;
import android.widget.TextView;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.CarModelItems;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class UserSignUp extends Fragment implements HttpRequest.OnReadyStateChangeListener,
        HttpRequest.OnErrorListener {

    private View mBaseView;

    private Button mSignUpButtonButton;
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mContactNumberEditText;
    private EditText mEmailAddressEditText;
    private EditText mPasswordEditText;
    private EditText mVerifyPasswordEditText;
    private EditText mAddressEditText;
    private TextView mPickForCurrentLocation;
    private Spinner mCaModelSpinner;
    private Spinner mCaTypeSpinner;

    private HttpRequest request;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_user_sign_up, container, false);

        mFirstNameEditText = mBaseView.findViewById(R.id.first_name_edit_text);
        mLastNameEditText = mBaseView.findViewById(R.id.last_name_edit_text);
        mEmailAddressEditText = mBaseView.findViewById(R.id.email_edit_text);
        mContactNumberEditText = mBaseView.findViewById(R.id.contact_number_edit_text);
        mPasswordEditText = mBaseView.findViewById(R.id.password_edit_text);
        mVerifyPasswordEditText = mBaseView.findViewById(R.id.confirm_edit_text);
        mAddressEditText = mBaseView.findViewById(R.id.address_edit_text);
        mPickForCurrentLocation = mBaseView.findViewById(R.id.pick_for_current_location);
        mCaModelSpinner = mBaseView.findViewById(R.id.car_model_spinner);
        mCaTypeSpinner = mBaseView.findViewById(R.id.car_type_spinner);

        return mBaseView;
    }

    private void getCarModels() {
        HttpRequest getStateRequest = new HttpRequest(getActivity().getApplicationContext());
        getStateRequest.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                try {
                                    JSONObject object = new JSONObject(request.getResponseText());
                                    JSONArray jsonArray = object.getJSONArray("results");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        System.out.println("Test " + jsonArray.getJSONObject(i));
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        CarModelItems carModelItems = new CarModelItems();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        getStateRequest.open("GET", String.format("%sstates", AppGlobals.BASE_URL));
        getStateRequest.send();
    }

    private void registerUser(String full_name, String email, String phoneNumber,
                              String address, String landline, String location, String sector,
                              String username, String password) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sregister", AppGlobals.BASE_URL));
        request.send(getRegisterData(full_name, email, phoneNumber, address, landline, location,
                sector, username, password));
        Helpers.showProgressDialog(getActivity(), "Registering User ");
    }


    private String getRegisterData(String full_name, String email, String phoneNumber,
                                   String address, String landline, String location, String sector,
                                   String username, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("full_name", full_name);
            jsonObject.put("email", email);
            jsonObject.put("mobile_number", phoneNumber);
            jsonObject.put("address", address);
            jsonObject.put("landline", landline);
            jsonObject.put("location", location);
            jsonObject.put("sector", sector);
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {

    }
}
