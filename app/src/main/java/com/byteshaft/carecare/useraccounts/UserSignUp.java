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

import com.byteshaft.carecare.Adapters.VehicleMakes;
import com.byteshaft.carecare.Adapters.VehicleType;
import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.VehicleMakeItems;
import com.byteshaft.carecare.gettersetter.VehicleTypeItems;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class UserSignUp extends Fragment implements HttpRequest.OnReadyStateChangeListener,
        HttpRequest.OnErrorListener {

    private View mBaseView;

    private Button mSignUpButtonButton;
    private EditText mFullNameEditText;
    private EditText mUserNameEditText;
    private EditText mContactNumberEditText;
    private EditText mEmailAddressEditText;
    private EditText mPasswordEditText;
    private EditText mVerifyPasswordEditText;
    private EditText mAddressEditText;
    private TextView mPickForCurrentLocation;
    private Spinner mVehicleMakeSpinner;
    private Spinner mVehicleTypeSpinner;

    private VehicleMakes vehicleMakesAdapter;
    private ArrayList<VehicleMakeItems> vehicleMakeArrayList;
    private VehicleType vehicleTypeAdapter;
    private ArrayList<VehicleTypeItems> vehicleTypeArrayList;

    private HttpRequest request;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_user_sign_up, container, false);

        mFullNameEditText = mBaseView.findViewById(R.id.full_name_edit_text);
        mUserNameEditText = mBaseView.findViewById(R.id.user_name_edit_text);
        mEmailAddressEditText = mBaseView.findViewById(R.id.email_edit_text);
        mContactNumberEditText = mBaseView.findViewById(R.id.contact_number_edit_text);

        mPasswordEditText = mBaseView.findViewById(R.id.password_edit_text);
        mVerifyPasswordEditText = mBaseView.findViewById(R.id.confirm_edit_text);
        mAddressEditText = mBaseView.findViewById(R.id.address_edit_text);
        mPickForCurrentLocation = mBaseView.findViewById(R.id.pick_for_current_location);

        mVehicleMakeSpinner = mBaseView.findViewById(R.id.vehicle_make_spinner);
        mVehicleTypeSpinner = mBaseView.findViewById(R.id.vehicle_type_spinner);
        vehicleMakeArrayList = new ArrayList<>();
        vehicleTypeArrayList = new ArrayList<>();
        return mBaseView;
    }

    private void getVehicleMake() {
        HttpRequest getStateRequest = new HttpRequest(getActivity());
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
                                        VehicleMakeItems vehicleMakeItems = new VehicleMakeItems();
                                        vehicleMakeItems.setVehicleMakeName(jsonObject.getString("name"));
                                        vehicleMakeItems.setVehicleMakeId(jsonObject.getInt("id"));
                                        vehicleMakeArrayList.add(vehicleMakeItems);
                                    }
                                    vehicleMakesAdapter = new VehicleMakes(getActivity(), vehicleMakeArrayList);
                                    mVehicleMakeSpinner.setAdapter(vehicleMakesAdapter);
                                    mVehicleMakeSpinner.setSelection(0);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        getStateRequest.open("GET", String.format("%svehicles/make", AppGlobals.BASE_URL));
        getStateRequest.send();
    }

    private void getVehicleType() {
        HttpRequest getStateRequest = new HttpRequest(getActivity());
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
                                        VehicleTypeItems vehicleItems = new VehicleTypeItems();
                                        vehicleItems.setVehicleTypeId(jsonObject.getInt("id"));
                                        vehicleItems.setVehicleTypeName(jsonObject.getString("name"));
                                        vehicleTypeArrayList.add(vehicleItems);
                                    }
                                    vehicleTypeAdapter = new VehicleType(getActivity(), vehicleTypeArrayList);
                                    mVehicleTypeSpinner.setAdapter(vehicleTypeAdapter);
                                    mVehicleTypeSpinner.setSelection(0);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        getStateRequest.open("GET", String.format("%svehicles/type", AppGlobals.BASE_URL));
        getStateRequest.send();
    }

    private void registerUser(String username, String name, String email, String contactNumber,
                              String address, String addressCoordinates, String vehiclMake, String vehicleModel,
                              String vehicleType, String vehicleYear, String password) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sregister-customer", AppGlobals.BASE_URL));
        request.send(getRegisterData(username, name, email, contactNumber, address, addressCoordinates,
                vehiclMake, vehicleModel, vehicleType, vehicleYear, password));
        Helpers.showProgressDialog(getActivity(), "Registering User ");
    }


    private String getRegisterData(String username, String name, String email, String contactNumber,
                                   String address, String addressCoordinates, String vehiclMake, String vehicleModel,
                                   String vehicleType, String vehicleYear, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("name", name);
            jsonObject.put("email", email);
            jsonObject.put("contact_number", contactNumber);
            jsonObject.put("address", address);
            jsonObject.put("address_coordinates", addressCoordinates);
            jsonObject.put("vehicle_make", vehiclMake);
            jsonObject.put("vehicle_model", vehicleModel);
            jsonObject.put("vehicle_type", vehicleType);
            jsonObject.put("vehicle_year", vehicleYear);
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
