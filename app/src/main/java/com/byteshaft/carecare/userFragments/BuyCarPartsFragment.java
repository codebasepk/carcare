package com.byteshaft.carecare.userFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.byteshaft.carecare.Adapters.VehicleMakeWithModel;
import com.byteshaft.carecare.Adapters.VehicleModelAdapter;
import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.CarCompanyItems;
import com.byteshaft.carecare.gettersetter.VehicleMakeWithModelItems;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class BuyCarPartsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private View mBaseView;
    private Spinner mVehicleModelSpinner;
    private Spinner mVehicleMakeSpinner;

    private VehicleModelAdapter vehicleModelAdapterAdapter;
    private ArrayList<VehicleMakeWithModelItems> arrayList;

    private VehicleMakeWithModel vehicleMakeAdapter;
    private ArrayList<CarCompanyItems> vehicleMakeArrayList;

    private int mVehicleMakeSpinnerId;
    private String mVehicleModelSpinnerString;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_buy_car_parts, container, false);
        mVehicleModelSpinner = mBaseView.findViewById(R.id.vehicle_model_Spinner);
        mVehicleMakeSpinner = mBaseView.findViewById(R.id.vehicle_make_spinner);
        arrayList = new ArrayList<>();
        vehicleMakeArrayList = new ArrayList<>();
        mVehicleModelSpinner.setOnItemSelectedListener(this);
        mVehicleMakeSpinner.setOnItemSelectedListener(this);
        getVehicleMake();
        getVehicleModel(mVehicleMakeSpinnerId);
        return mBaseView;
    }

    private void getVehicleModel(int id) {
        HttpRequest getStateRequest = new HttpRequest(getActivity());
        getStateRequest.setOnReadyStateChangeListener((request, readyState) -> {
            switch (readyState) {
                case HttpRequest.STATE_DONE:
                    switch (request.getStatus()) {
                        case HttpURLConnection.HTTP_OK:
                            arrayList = new ArrayList<>();
                            try {
                                JSONObject jsonObject = new JSONObject(request.getResponseText());
                                JSONArray jsonArray = jsonObject.getJSONArray("results");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    System.out.println("working " + jsonArray.getJSONObject(i));
                                    JSONObject VehicleTypeJsonObject = jsonArray.getJSONObject(i);
                                    VehicleMakeWithModelItems vehicleMakeWithModelItems = new VehicleMakeWithModelItems();
                                    vehicleMakeWithModelItems.setVehicleModelId(VehicleTypeJsonObject.getInt("id"));
                                    vehicleMakeWithModelItems.setVehicleModelName(VehicleTypeJsonObject.getString("name"));
                                    arrayList.add(vehicleMakeWithModelItems);
                                }
                                vehicleModelAdapterAdapter = new VehicleModelAdapter(getActivity(), arrayList);
                                mVehicleModelSpinner.setAdapter(vehicleModelAdapterAdapter);
                                mVehicleModelSpinner.setSelection(0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
            }
        });
        getStateRequest.open("GET", String.format("%svehicles/make/%s/models", AppGlobals.BASE_URL, id));
        getStateRequest.send();
    }

    private void getVehicleMake() {
        HttpRequest getStateRequest = new HttpRequest(getActivity());
        getStateRequest.setOnReadyStateChangeListener((request, readyState) -> {
            switch (readyState) {
                case HttpRequest.STATE_DONE:
                    switch (request.getStatus()) {
                        case HttpURLConnection.HTTP_OK:
                            try {
                                JSONObject jsonObject = new JSONObject(request.getResponseText());
                                JSONArray jsonArray = jsonObject.getJSONArray("results");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    System.out.println("Test " + jsonArray.getJSONObject(i));
                                    JSONObject vehicleMakeJsonObject = jsonArray.getJSONObject(i);
                                    CarCompanyItems carCompanyItems = new CarCompanyItems();
                                    carCompanyItems.setCompanyId(vehicleMakeJsonObject.getInt("id"));
                                    carCompanyItems.setCompanyName(vehicleMakeJsonObject.getString("name"));
                                    vehicleMakeArrayList.add(carCompanyItems);
                                }
                                vehicleMakeAdapter = new VehicleMakeWithModel(getActivity(), vehicleMakeArrayList);
                                mVehicleMakeSpinner.setAdapter(vehicleMakeAdapter);
                                mVehicleMakeSpinner.setSelection(0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
            }
        });
        getStateRequest.open("GET", String.format("%svehicles/make", AppGlobals.BASE_URL));
        getStateRequest.send();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.vehicle_model_Spinner:
                VehicleMakeWithModelItems vehicleMakeWithModelItems = arrayList.get(position);
                mVehicleModelSpinnerString = String.valueOf(vehicleMakeWithModelItems.getVehicleModelId());
                break;
            case R.id.vehicle_make_spinner:
                CarCompanyItems carCompanyItems = vehicleMakeArrayList.get(position);
                mVehicleMakeSpinnerId = carCompanyItems.getCompanyId();
                getVehicleModel(mVehicleMakeSpinnerId);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
