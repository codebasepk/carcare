package com.byteshaft.carecare.provider;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.byteshaft.carecare.R;

public class Home extends Fragment implements View.OnClickListener {

    private View mBaseView;
    private ImageButton mAutoMachanicButton;
    private ImageButton mCarWashButton;
    private ImageButton mBuyCarPartsButton;
    private ImageButton mBuyFuelButton;
    private ImageButton mCarTowingButton;
    private ImageButton mCarPromos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.provider_home_fragment, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("Home");

        mAutoMachanicButton = mBaseView.findViewById(R.id.auto_mechanic_image_button);
        mCarWashButton = mBaseView.findViewById(R.id.car_wash_image_button);
        mBuyCarPartsButton = mBaseView.findViewById(R.id.car_parts_image_button);
        mBuyFuelButton = mBaseView.findViewById(R.id.buy_fuel_image_button);
        mCarTowingButton = mBaseView.findViewById(R.id.car_towing_image_button);
        mCarPromos = mBaseView.findViewById(R.id.car_promos_image_button);

        mAutoMachanicButton.setOnClickListener(this);
        mCarWashButton.setOnClickListener(this);
        mBuyCarPartsButton.setOnClickListener(this);
        mBuyFuelButton.setOnClickListener(this);
        mCarTowingButton.setOnClickListener(this);
        mCarPromos.setOnClickListener(this);

        return mBaseView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.auto_mechanic_image_button:
                startActivity(new Intent(getActivity(), MechanicActivity.class));
                break;
            case R.id.car_wash_image_button:
                Toast.makeText(getContext(), "this is ok", Toast.LENGTH_SHORT).show();
                break;
            case R.id.car_parts_image_button:
                startActivity(new Intent(getActivity(), CarPartsActivity.class));
                break;
            case R.id.buy_fuel_image_button:
                Toast.makeText(getContext(), "this is ok", Toast.LENGTH_SHORT).show();
                break;
            case R.id.car_towing_image_button:
                Toast.makeText(getContext(), "this is ok", Toast.LENGTH_SHORT).show();
                break;
            case R.id.car_promos_image_button:
                Toast.makeText(getContext(), "this is ok", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
