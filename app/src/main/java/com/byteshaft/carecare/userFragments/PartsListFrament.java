package com.byteshaft.carecare.userFragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class PartsListFrament extends Fragment implements HttpRequest.OnErrorListener,
        HttpRequest.OnReadyStateChangeListener, View.OnClickListener{

    private View mBaseView;
    private ListView mPartsListView;
    private HttpRequest request;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.parts_list_fragment, container, false);
        mPartsListView = mBaseView.findViewById(R.id.parts_list_view);
        return mBaseView;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {

    }

    private void orderFuel(String FuelType, String numberOfLiters) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sforgot-password", AppGlobals.BASE_URL));
        request.send(getFuelData(FuelType, numberOfLiters));
        Helpers.showProgressDialog(getActivity(), getString(R.string.recovery_email));
    }


    private String getFuelData(String FuelType, String numberOfLiters) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", FuelType);
            jsonObject.put("email", numberOfLiters);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
