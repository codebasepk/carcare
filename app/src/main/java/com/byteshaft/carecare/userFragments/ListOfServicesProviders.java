package com.byteshaft.carecare.userFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.byteshaft.carecare.Adapters.ServiceProvidersListAdapter;
import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.PartsListItems;
import com.byteshaft.carecare.gettersetter.ServicesProvidersListItems;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class ListOfServicesProviders extends Fragment implements HttpRequest.OnReadyStateChangeListener,
        HttpRequest.OnErrorListener{

    private View mBaseView;
    private ListView mServiceProvidersListView;
    private ArrayList<ServicesProvidersListItems> arrayList;
    private ServiceProvidersListAdapter adapter;
    private HttpRequest request;
    private int mServiceId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_service_providers, container, false);
        mServiceProvidersListView = mBaseView.findViewById(R.id.service_providers_list_view);
        arrayList = new ArrayList<>();
        adapter = new ServiceProvidersListAdapter(getActivity(), arrayList);
        mServiceProvidersListView.setAdapter(adapter);
        Bundle bundle = getArguments();
        if(bundle != null) {
            mServiceId = bundle.getInt("service_id");
        }
        getServiceProvidersList("30.0723233,71.1988781", mServiceId);
        return mBaseView;
    }

    @Override
    public void onReadyStateChange(HttpRequest httpRequest, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_OK:
                        try {
                            JSONObject mainJsonObject = new JSONObject(request.getResponseText());
                            JSONArray jsonArray = mainJsonObject.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length() ; i++) {
                                Log.e("working " ,  " " + jsonArray.getJSONObject(i));
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ServicesProvidersListItems servicesProvidersListItems = new ServicesProvidersListItems();
                                servicesProvidersListItems.setServiceProviderId(jsonObject.getInt("id"));
                                servicesProvidersListItems.setServiceProviderName(jsonObject.getString("name"));
                                servicesProvidersListItems.setProvidersContactNumber(jsonObject.getString("contact"));
                                servicesProvidersListItems.setServiceProviderImage(jsonObject.getString("image"));
                                arrayList.add(servicesProvidersListItems);
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }

    }

    @Override
    public void onError(HttpRequest httpRequest, int readyState, short i1, Exception exception) {
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

    private void getServiceProvidersList(String baseLocation, int service) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("GET", String.format("%sfind-mechanic?base_location=%s&service=%s", AppGlobals.BASE_URL,
                baseLocation, service));
        request.send();
        Helpers.showProgressDialog(getActivity(), getString(R.string.parts_list));
    }
}
