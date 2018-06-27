package com.byteshaft.carecare.provider;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Calendar;

public class MechanicalProfile extends Fragment implements View.OnClickListener {
    private View mBaseView;
    private TextView mStartTime;
    private TextView mEndTime;
    private EditText etYearsOfExperience;
    private Button setButton;
    private int minute, hour;
    private String startTime = "";
    private String endTime = "";
    private String yearsOfExperience;

    TimePickerDialog timePickerDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.mechanical_profile, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("Mechanical Profile");
        getProfileData();
        mStartTime = mBaseView.findViewById(R.id.start_time);
        mEndTime = mBaseView.findViewById(R.id.end_time);
        etYearsOfExperience = mBaseView.findViewById(R.id.years_of_experience);
        setButton = mBaseView.findViewById(R.id.set_time);

        mStartTime.setOnClickListener(this);
        mEndTime.setOnClickListener(this);
        setButton.setOnClickListener(this);

        etYearsOfExperience.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_YEARS_OF_EXP));
        mStartTime.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_START_TIME));
        mEndTime.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_END_TIME));

        startTime = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_START_TIME);
        endTime = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_END_TIME);
        return mBaseView;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_time:
                timePickerDialog = new TimePickerDialog(getContext(),
                        (view1, hourOfDay, minute) -> {
                            final Calendar c = Calendar.getInstance();
                            hour = c.get(Calendar.HOUR_OF_DAY);
                            minute = c.get(Calendar.MINUTE);

                            String AM_PM;
                            if (hourOfDay < 12) {
                                AM_PM = "AM";
                            } else {
                                AM_PM = "PM";
                            }
                            startTime = hourOfDay + ":" + minute + " " + AM_PM;
                            mStartTime.setText(startTime);
                        }, hour, minute, true);
                timePickerDialog.show();
                break;
            case R.id.end_time:
                timePickerDialog = new TimePickerDialog(getContext(),
                        (view12, hourOfDay, minute) -> {
                            final Calendar c = Calendar.getInstance();
                            hour = c.get(Calendar.HOUR_OF_DAY);
                            minute = c.get(Calendar.MINUTE);

                            String AM_PM;
                            if (hourOfDay < 12) {
                                AM_PM = "AM";
                            } else {
                                AM_PM = "PM";
                            }
                            endTime = hourOfDay + ":" + minute + " " + AM_PM;
                            mEndTime.setText(endTime);
                        }, hour, minute, true);
                timePickerDialog.show();
                break;
            case R.id.set_time:
                if (validate()) {
                    Log.wtf("ok", startTime + " " + endTime);
                    setProfile(etYearsOfExperience.getText().toString(), startTime, endTime);
                }
        }
    }


    public boolean validate() {
        boolean valid = true;
        yearsOfExperience = etYearsOfExperience.getText().toString();
        if (yearsOfExperience.trim().isEmpty()) {
            etYearsOfExperience.setError("Required");
            valid = false;
        } else {
            etYearsOfExperience.setError(null);
        }

        if (startTime.isEmpty()) {
            Toast.makeText(getContext(), "Please Select Start Time", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            valid = true;
        }

        if (endTime.isEmpty()) {
            Toast.makeText(getContext(), "Please Select End Time", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            valid = true;
        }
        return valid;
    }

    private void getProfileData() {
        Helpers.showProgressDialog(getActivity(), "Please wait...");
        HttpRequest request = new HttpRequest(getContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int i) {
                switch (i) {
                    case HttpRequest.STATE_DONE:
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                try {
                                    JSONObject jsonObject = new JSONObject(request.getResponseText());
                                    String years = jsonObject.getString("experience");
                                    String start = jsonObject.getString("start_time");
                                    String endTime = jsonObject.getString("end_time");

                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_YEARS_OF_EXP, years);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_START_TIME, start);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_END_TIME, endTime);
                                    etYearsOfExperience.setText(years);
                                    mStartTime.setText(start);
                                    mEndTime.setText(endTime);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        request.open("GET", String.format("%smechanic/profile", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send();
    }

    private void setProfile(String experience, String startingTiming, String endingTime) {
        Helpers.showProgressDialog(getActivity(), "Please wait...");
        HttpRequest request = new HttpRequest(getContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int i) {
                switch (i) {
                    case HttpRequest.STATE_DONE:
                        Log.wtf("Mechanic Profile Response", request.getResponseText());
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_CREATED:
                                try {
                                    JSONObject jsonObject = new JSONObject(request.getResponseText());
                                    String years = jsonObject.getString("experience");
                                    String start = jsonObject.getString("start_time");
                                    String endTime = jsonObject.getString("end_time");

                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_YEARS_OF_EXP, years);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_START_TIME, start);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_END_TIME, endTime);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            case HttpURLConnection.HTTP_OK:
                                try {
                                    JSONObject jsonObject = new JSONObject(request.getResponseText());
                                    String years = jsonObject.getString("experience");
                                    String start = jsonObject.getString("start_time");
                                    String endTime = jsonObject.getString("end_time");

                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_YEARS_OF_EXP, years);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_START_TIME, start);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_END_TIME, endTime);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        request.open("PUT", String.format("%smechanic/profile", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("experience", experience);
            jsonObject.put("start_time", startingTiming);
            jsonObject.put("end_time", endingTime);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        request.send(jsonObject.toString());
    }
}
