package com.byteshaft.carecare.userFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.byteshaft.carecare.Adapters.FuelTypeAdapter;
import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.FuelTypeItems;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class BuyFuelFragment extends Fragment implements HttpRequest.OnReadyStateChangeListener,
        HttpRequest.OnErrorListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private View mBaseView;
    private EditText mLitersEditText;
    private Spinner mFuelTypeSpinner;
    private Button mNextButton;

    private String mLitersString;
    private String mFuelTypeSpinnerString;

    private ArrayList<FuelTypeItems> arrayList;
    private FuelTypeAdapter adapter;

    private HttpRequest request;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_buy_fuel, container, false);
        mLitersEditText = mBaseView.findViewById(R.id.liters_edit_text);
        mNextButton = mBaseView.findViewById(R.id.button_next);
        mFuelTypeSpinner = mBaseView.findViewById(R.id.fuel_type_spinner);
        mNextButton.setOnClickListener(this);
        mFuelTypeSpinner.setOnItemSelectedListener(this);
        arrayList = new ArrayList<>();
        getFuelType();
        return mBaseView;
    }

    @Override
    public void onClick(View v) {
        if (validate()) {
            orderFuel(mFuelTypeSpinnerString, mLitersString);

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

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(getActivity(), getString(R.string.fuel_failed), getString(R.string.check_internet));
                        break;
                    case HttpURLConnection.HTTP_OK:
                        System.out.println(request.getResponseText());
                }
        }

    }

        private void getFuelType() {
        HttpRequest getStateRequest = new HttpRequest(getActivity());
        getStateRequest.setOnReadyStateChangeListener((request, readyState) -> {
            switch (readyState) {
                case HttpRequest.STATE_DONE:
                    switch (request.getStatus()) {
                        case HttpURLConnection.HTTP_OK:
                            try {
                                JSONArray jsonArray = new JSONArray(request.getResponseText());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    System.out.println("Test " + jsonArray.getJSONObject(i));
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    FuelTypeItems fuelTypeItems = new FuelTypeItems();
                                    fuelTypeItems.setFuelName(jsonObject.getString("name"));
                                    fuelTypeItems.setFuelId(jsonObject.getInt("id"));
                                    arrayList.add(fuelTypeItems);
                                }
                                adapter = new FuelTypeAdapter(getActivity(), arrayList);
                                mFuelTypeSpinner.setAdapter(adapter);
                                mFuelTypeSpinner.setSelection(0);
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
        FuelTypeItems fuelTypeItems = arrayList.get(position);
        mFuelTypeSpinnerString = String.valueOf(fuelTypeItems.getFuelId());

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void orderFuel(String FuelType, String numberOfLiters) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sforgot-password", AppGlobals.BASE_URL));
        request.send(getFuelData(FuelType, numberOfLiters));
        Helpers.showProgressDialog(getActivity(), getString(R.string.recovery_email));
    }


    private String getFuelData(String FuelType, String numberOfLiters) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", FuelType);
            jsonObject.put("email", numberOfLiters);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private boolean validate() {
        boolean valid = true;
        mLitersString = mLitersEditText.getText().toString();

        if (mLitersString.trim().isEmpty()) {
            mLitersEditText.setError(getString(R.string.fuel_error));
            valid = false;
        } else {
            mLitersEditText.setError(null);
        }
        return valid;
    }
}
