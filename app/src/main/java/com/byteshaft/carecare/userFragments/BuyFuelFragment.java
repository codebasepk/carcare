package com.byteshaft.carecare.userFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.byteshaft.carecare.R;
import com.byteshaft.requests.HttpRequest;

public class BuyFuelFragment extends Fragment implements HttpRequest.OnReadyStateChangeListener,
        HttpRequest.OnErrorListener, View.OnClickListener{

    private View mBaseView;
    private EditText mLitersEditText;
    private Button mNextButton;

    private String mLitersStringt;

    private HttpRequest request;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_buy_fuel, container, false);
        mLitersEditText = mBaseView.findViewById(R.id.liters_edit_text);
        mNextButton = mBaseView.findViewById(R.id.button_next);
        mNextButton.setOnClickListener(this);
        return mBaseView;
    }

    @Override
    public void onClick(View v) {
        mLitersStringt = mLitersEditText.getText().toString();
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {

    }
}
