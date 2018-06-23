package com.byteshaft.carecare.userFragments;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.byteshaft.carecare.MainActivity;
import com.byteshaft.carecare.R;
import com.byteshaft.carecare.useraccounts.UserAccount;

import static android.content.Context.VIBRATOR_SERVICE;

public class UserHomeFragment extends Fragment implements View.OnClickListener {
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
        mBaseView = inflater.inflate(R.layout.user_home_fragment, container, false);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.auto_mechanic_image_button:
                MainActivity.getInstance().loadFragment(new AutoMechanicFragment());
                break;
            case R.id.car_wash_image_button:
                MainActivity.getInstance().loadFragment(new CarWashFragment());
                break;
            case R.id.car_parts_image_button:
                MainActivity.getInstance().loadFragment(new BuyCarPartsFragment());
                break;
            case R.id.buy_fuel_image_button:
                MainActivity.getInstance().loadFragment(new BuyFuelFragment());
                break;
            case R.id.car_towing_image_button:
                MainActivity.getInstance().loadFragment(new TowingFragment());
                break;
            case R.id.car_promos_image_button:
                break;
        }

    }
}
