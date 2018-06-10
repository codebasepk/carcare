package com.byteshaft.carecare.provider;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.byteshaft.carecare.Adapters.MechanicServiceAdapter;
import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.MechanicServices;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class AddMechanicService extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText etServicePrice;
    private Spinner serviceSpinner;
    private Button addButton;

    private String price;
    private int serviceId;
    private ArrayList<MechanicServices> mechanicServicesArrayList;
    private MechanicServiceAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mechanic_service);
        setTitle("Auto Mechanic");
        etServicePrice = findViewById(R.id.et_service_price);
        serviceSpinner = findViewById(R.id.mechanic_service_spinner);
        addButton = findViewById(R.id.add_service);

        serviceSpinner.setOnItemSelectedListener(this);
        mechanicServicesArrayList = new ArrayList<>();
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    addService(etServicePrice.getText().toString(), serviceId);
                }
            }
        });
        getServices();
    }

    public boolean validate() {
        boolean valid = true;
        price = etServicePrice.getText().toString();


        if (price.trim().isEmpty()) {
            etServicePrice.setError("Required");
            valid = false;
        } else {
            etServicePrice.setError(null);
        }

        return valid;
    }


    private void addService(String price, int service) {
        Helpers.showProgressDialog(AddMechanicService.this, "Pleas wait...");
        HttpRequest request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_CREATED:
                                Helpers.showSnackBar(addButton, "Item Added");
                                finish();
                        }
                }
            }
        });
        request.open("POST", String.format("%smechanic/services", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        JSONObject object = new JSONObject();
        try {
            object.put("price", price);
            object.put("service", service);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request.send(object.toString());
    }

    private void getServices() {
        HttpRequest getStateRequest = new HttpRequest(getApplicationContext());
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
                                    MechanicServices mechanicServices = new MechanicServices();
                                    mechanicServices.setId(vehicleMakeJsonObject.getInt("id"));
                                    mechanicServices.setName(vehicleMakeJsonObject.getString("name"));
                                    mechanicServicesArrayList.add(mechanicServices);
                                }

                                adapter = new MechanicServiceAdapter(AddMechanicService.this, mechanicServicesArrayList);
                                serviceSpinner.setAdapter(adapter);
                                serviceSpinner.setSelection(0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
            }
        });
        getStateRequest.open("GET", String.format("%smechanic-services", AppGlobals.BASE_URL));
        getStateRequest.send();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        MechanicServices services = mechanicServicesArrayList.get(i);
        serviceId = services.getId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
