package com.byteshaft.carecare.provider;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.CarWashService;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class AddCarWashService extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText etServicePrice;
    private Spinner serviceSpinner;
    private Button addButton;


    private String price;
    private int serviceId;
    private ArrayList<CarWashService> carWashServiceArrayList;
    private CarWashServiceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car_wash_service);
        setTitle("Add Service");
        etServicePrice = findViewById(R.id.et_service_price);
        serviceSpinner = findViewById(R.id.car_wash_service_spinner);
        addButton = findViewById(R.id.add_service);
        serviceSpinner.setOnItemSelectedListener(this);
        carWashServiceArrayList = new ArrayList<>();
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


    private void addService(String price, int service) {
        Helpers.showProgressDialog(AddCarWashService.this, "Pleas wait...");
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
        request.open("POST", String.format("%sservice-station/services", AppGlobals.BASE_URL));
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
                                    CarWashService carWashService = new CarWashService();
                                    carWashService.setId(vehicleMakeJsonObject.getInt("id"));
                                    carWashService.setName(vehicleMakeJsonObject.getString("name"));
                                    carWashServiceArrayList.add(carWashService);
                                }

                                adapter = new CarWashServiceAdapter(AddCarWashService.this, carWashServiceArrayList);
                                serviceSpinner.setAdapter(adapter);
                                serviceSpinner.setSelection(0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
            }
        });
        getStateRequest.open("GET", String.format("%scar-wash-services", AppGlobals.BASE_URL));
        getStateRequest.send();
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        CarWashService services = carWashServiceArrayList.get(i);
        serviceId = services.getId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private class CarWashServiceAdapter extends BaseAdapter {

        private ViewHolder viewHolder;
        private ArrayList<CarWashService> arrayList;
        private Activity activity;

        CarWashServiceAdapter(Activity activity, ArrayList<CarWashService> arrayList) {
            this.activity = activity;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = activity.getLayoutInflater().inflate(R.layout.delegate_spinner, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.spinnerText = convertView.findViewById(R.id.spinner_text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            CarWashService carWashService = arrayList.get(position);
            viewHolder.spinnerText.setText(carWashService.getName());
            return convertView;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        class ViewHolder {
            TextView spinnerText;
        }
    }

}
