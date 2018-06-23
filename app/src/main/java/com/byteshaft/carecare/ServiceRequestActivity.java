package com.byteshaft.carecare;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.byteshaft.carecare.Adapters.CarAdapter;
import com.byteshaft.carecare.Adapters.VehicleMakeWithModel;
import com.byteshaft.carecare.Adapters.VehicleModelAdapter;
import com.byteshaft.carecare.gettersetter.CarCompanyItems;
import com.byteshaft.carecare.gettersetter.CarItems;
import com.byteshaft.carecare.gettersetter.VehicleMakeWithModelItems;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.requests.HttpRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ServiceRequestActivity extends Activity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    private Button mRequestButton;
    private Spinner mVehicleModelSpinner;
    private Spinner mVehicleMakeSpinner;
    private EditText mDateEditText;
    private EditText mCarNumberEditText;
    private EditText mTimeEditText;
    private EditText mCurrentLocationEditText;
    private EditText mContactNumber;
    private RadioGroup radioGroup;
    private String mRadioButtonString;
    private VehicleModelAdapter vehicleModelAdapter;
    private ArrayList<VehicleMakeWithModelItems> arrayList;

    private VehicleMakeWithModel vehicleMakeAdapter;
    private ArrayList<CarCompanyItems> vehicleMakeArrayList;
    public String mDateEditTextString;
    private Calendar mCalendar;
    private DatePickerDialog.OnDateSetListener date;

    private int mVehicleMakeSpinnerId;
    private int mVehicleModelSpinnerId;
    private String addressCoordinates;

    private int PLACE_PICKER_REQUEST = 121;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_request_activity);
        mContactNumber = findViewById(R.id.contact_number_edit_text);
        mCarNumberEditText = findViewById(R.id.car_number_edit_text);
        mCurrentLocationEditText = findViewById(R.id.pick_location_edit_text);

        mDateEditText = findViewById(R.id.date_edit_text);
        mTimeEditText = findViewById(R.id.time_edit_text);
        mRequestButton = findViewById(R.id.request_button);
        radioGroup = findViewById(R.id.radio_group);

        mVehicleModelSpinner = findViewById(R.id.vehicle_model_Spinner);
        mVehicleMakeSpinner = findViewById(R.id.vehicle_make_spinner);
        mDateEditText = findViewById(R.id.date_edit_text);

        mRequestButton.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);
        mVehicleModelSpinner.setOnItemSelectedListener(this);
        mVehicleMakeSpinner.setOnItemSelectedListener(this);
        mDateEditText.setOnClickListener(this);
        mTimeEditText.setOnClickListener(this);
        mCurrentLocationEditText.setOnClickListener(this);

        arrayList = new ArrayList<>();
        vehicleMakeArrayList = new ArrayList<>();
        getVehicleMake();
        getVehicleModel(mVehicleMakeSpinnerId);
        mContactNumber.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_CONTACT_NUMBER));

        mCalendar = Calendar.getInstance();
        date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        mDateEditText.setText(sdf.format(mCalendar.getTime()));
        mDateEditText.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_edit_text:
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, 0);
                long lowerLimit = calendar.getTimeInMillis();
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, date, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(lowerLimit);
                datePickerDialog.show();
                break;
            case R.id.time_edit_text:
                timePickerDialog();
                break;
            case R.id.pick_location_edit_text:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    private void timePickerDialog() {
        Calendar mCurrentTime = Calendar.getInstance();
        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mCurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            String AMPM;
            if (selectedHour < 12) {
                AMPM = " AM";
            } else {
                AMPM = " PM";
            }
            mTimeEditText.setText(selectedHour + ":" + selectedMinute);
        }, hour, minute, false);
        mTimePicker.setTitle(getString(R.string.select_time));
        mTimePicker.show();

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton radioButton = findViewById(checkedId);
        mRadioButtonString = radioButton.getText().toString();
        System.out.println(mRadioButtonString);

    }

    private void getVehicleModel(int id) {
        HttpRequest getStateRequest = new HttpRequest(this);
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
                                vehicleModelAdapter = new VehicleModelAdapter(this, arrayList);
                                mVehicleModelSpinner.setAdapter(vehicleModelAdapter);
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
        HttpRequest getStateRequest = new HttpRequest(this);
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
                                vehicleMakeAdapter = new VehicleMakeWithModel(this, vehicleMakeArrayList);
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
                mVehicleModelSpinnerId = vehicleMakeWithModelItems.getVehicleModelId();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                Place place = PlacePicker.getPlace(data, getApplicationContext());
                LatLng latLng = place.getLatLng();
                String latitude = String.valueOf(latLng.latitude);
                String longitude = String.valueOf(latLng.longitude);
                addressCoordinates = latitude + "," + longitude;
                Log.wtf("Address:  ", addressCoordinates);
                mCurrentLocationEditText.setText(place.getName());
            }
        }
    }
}
