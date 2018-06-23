package com.byteshaft.carecare.provider;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.TowServiceItems;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class AddTowService extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText etServicePrice;
    private Spinner serviceSpinner;
    private Button addButton;


    private String price;
    private int serviceId;
    private ArrayList<TowServiceItems> towServiceItems;
    private TowServiceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Add Tow Service");
        setContentView(R.layout.activity_add_tow_service);
        etServicePrice = findViewById(R.id.et_service_price);
        serviceSpinner = findViewById(R.id.tow_service_spinner);
        addButton = findViewById(R.id.add_service);
        serviceSpinner.setOnItemSelectedListener(this);
        towServiceItems = new ArrayList<>();
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
        Helpers.showProgressDialog(AddTowService.this, "Pleas wait...");
        HttpRequest request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        Log.wtf("ok", request.getResponseText());
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_CREATED:
                                Helpers.showSnackBar(addButton, "Item Added");
                                finish();
                            case HttpURLConnection.HTTP_BAD_REQUEST:
                                Helpers.alertDialog(AddTowService.this, null, getResources().getString(R.string.service_already_added), null);
                        }
                }
            }
        });
        request.open("POST", String.format("%stowing-provider/services", AppGlobals.BASE_URL));
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
                                    TowServiceItems items = new TowServiceItems();
                                    items.setId(vehicleMakeJsonObject.getInt("id"));
                                    items.setName(vehicleMakeJsonObject.getString("name"));
                                    towServiceItems.add(items);
                                }

                                adapter = new TowServiceAdapter(AddTowService.this, towServiceItems);
                                serviceSpinner.setAdapter(adapter);
                                serviceSpinner.setSelection(0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
            }
        });
        getStateRequest.open("GET", String.format("%stowing-services", AppGlobals.BASE_URL));
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
        TowServiceItems services = towServiceItems.get(i);
        serviceId = services.getId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class TowServiceAdapter extends BaseAdapter {

        private ViewHolder viewHolder;
        private ArrayList<TowServiceItems> arrayList;
        private Activity activity;

        TowServiceAdapter(Activity activity, ArrayList<TowServiceItems> arrayList) {
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
            TowServiceItems towServiceItems = arrayList.get(position);
            viewHolder.spinnerText.setText(towServiceItems.getName());
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
