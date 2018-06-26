package com.byteshaft.carecare.provider;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.byteshaft.carecare.R;

public class MechanicalProfile extends Fragment implements View.OnClickListener {
    private View mBaseView;
    private TextView mStartTime;
    private TextView mEndTime;
    private EditText etYearsOfExperience;
    private Button setButton;
    private int minute, hour;
    TimePickerDialog timePickerDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.mechanical_profile, container, false);
        mStartTime = mBaseView.findViewById(R.id.start_time);
        mEndTime = mBaseView.findViewById(R.id.end_time);
        etYearsOfExperience = mBaseView.findViewById(R.id.years_of_experience);
        setButton = mBaseView.findViewById(R.id.set_time);


        mStartTime.setOnClickListener(this);
        mEndTime.setOnClickListener(this);
        setButton.setOnClickListener(this);


        return mBaseView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_time:
                timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                String AM_PM;
                                if (hourOfDay < 12) {
                                    AM_PM = "AM";
                                } else {
                                    AM_PM = "PM";
                                }
                                mStartTime.setText(hourOfDay + ":" + minute + " " + AM_PM);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
                break;
            case R.id.end_time:
                timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                String AM_PM;
                                if (hourOfDay < 12) {
                                    AM_PM = "AM";
                                } else {
                                    AM_PM = "PM";
                                }
                                mEndTime.setText(hourOfDay + ":" + minute + " " + AM_PM);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();

                break;
            case R.id.set_time:

        }
    }
}
