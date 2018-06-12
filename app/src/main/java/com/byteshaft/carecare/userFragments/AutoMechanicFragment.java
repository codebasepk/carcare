package com.byteshaft.carecare.userFragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.AutoMechanicCarWashItems;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class AutoMechanicFragment extends Fragment implements HttpRequest.OnReadyStateChangeListener,
        HttpRequest.OnErrorListener, View.OnClickListener {

    private View mBaseView;
    private RadioGroup radioGroup;
    private EditText mDetailsEditText;
    private Button mNextButton;
    private HttpRequest request;

    private ArrayList<AutoMechanicCarWashItems> arrayList;
    private int serviceId;
    private RadioGroup.LayoutParams layoutParams;
    private AutoMechanicCarWashItems items;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_auto_mechanic, container, false);
        radioGroup = mBaseView.findViewById(R.id.radio_group);
        mDetailsEditText = mBaseView.findViewById(R.id.details_edit_text);
        mNextButton = mBaseView.findViewById(R.id.button_next);
        mNextButton.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                items = arrayList.get(checkedId);
                serviceId = items.getServiceId();
                Log.e("onCheckedChanged", "" + serviceId);

            }
        });
        arrayList = new ArrayList<>();
        getAutoMechanicsServicesList();

        return mBaseView;
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        Helpers.dismissProgressDialog();
        switch (readyState) {
            case HttpRequest.ERROR_CONNECTION_TIMED_OUT:
                Helpers.showSnackBar(getView(), getString(R.string.connection_time_out));
                break;
            case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                Helpers.showSnackBar(getView(), exception.getLocalizedMessage());
                break;
        }

    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_OK:
                        try {
                            JSONObject mainJsonObject = new JSONObject(request.getResponseText());
                            JSONArray jsonArray = mainJsonObject.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                System.out.println("Test " + jsonArray.getJSONObject(i));
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                items = new AutoMechanicCarWashItems();
                                items.setServiceId(jsonObject.getInt("id"));
                                items.setServiceName(jsonObject.getString("name"));
                                RadioButton radioButton = new RadioButton(getActivity());
                                radioButton.setText(items.getServiceName());
                                radioButton.setId(i);
                                layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.
                                        WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                                radioGroup.addView(radioButton, layoutParams);
                                arrayList.add(items);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }

    }

    private void getAutoMechanicsServicesList() {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("GET", String.format("%smechanic-services", AppGlobals.BASE_URL));
        request.send();
        Helpers.showProgressDialog(getActivity(), "Fetching Services...");
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putInt("service_id", serviceId);
        ListOfServicesProviders listOfServicesProviders = new ListOfServicesProviders();
        listOfServicesProviders.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.container, listOfServicesProviders).commit();

    }

}
