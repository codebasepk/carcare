package com.byteshaft.carecare.provider;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byteshaft.carecare.R;

public class Home extends Fragment {
    private View mBaseView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.provider_home_fragment, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("Home");
        return mBaseView;
    }
}
