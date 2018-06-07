package com.byteshaft.carecare;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.byteshaft.carecare.Adapters.CarAdapter;
import com.byteshaft.carecare.gettersetter.CarItems;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class ServiceRequestActivity extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    private Button mRequestButton;
    private Spinner mChooseCarSpinner;
    private EditText mDateEditText;
    private EditText mCarNumberEditText;
    private EditText mTimeEditText;
    private EditText mContactNumber;
    private RadioGroup radioGroup;
    private String mRadioButtonString;

    private String mChooseCarString;

    private ArrayList<CarItems> arrayList;
    private CarAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_request_activity);
        mChooseCarSpinner = findViewById(R.id.choose_car_spinner);
        mContactNumber = findViewById(R.id.contact_number_edit_text);
        mCarNumberEditText = findViewById(R.id.car_number_edit_text);
        mDateEditText = findViewById(R.id.date_edit_text);
        mTimeEditText = findViewById(R.id.time_edit_text);
        mRequestButton = findViewById(R.id.request_button);
        radioGroup = findViewById(R.id.radio_group);
        mRequestButton.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);
        mChooseCarSpinner.setOnItemSelectedListener(this);
        arrayList = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton radioButton = findViewById(checkedId);
        mRadioButtonString = radioButton.getText().toString();
        System.out.println(mRadioButtonString);

    }

    private void getCarList() {
        HttpRequest getStateRequest = new HttpRequest(this);
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
                                    CarItems carItems = new CarItems();
                                    carItems.setCarName(jsonObject.getString("name"));
                                    carItems.setCarId(jsonObject.getInt("id"));
                                    arrayList.add(carItems);
                                }
                                adapter = new CarAdapter(this, arrayList);
                                mChooseCarSpinner.setAdapter(adapter);
                                mChooseCarSpinner.setPrompt("Select Your Car");
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
        CarItems carItems = arrayList.get(position);
        mChooseCarString = String.valueOf(carItems.getCarId());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
