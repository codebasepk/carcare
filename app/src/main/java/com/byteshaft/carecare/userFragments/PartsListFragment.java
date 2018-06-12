package com.byteshaft.carecare.userFragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.byteshaft.carecare.Adapters.PartsListAdapter;
import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.PartsListItems;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class PartsListFragment extends Fragment implements HttpRequest.OnErrorListener,
        HttpRequest.OnReadyStateChangeListener, View.OnClickListener {

    private View mBaseView;
    private ListView mPartsListView;
    private HttpRequest request;

    private String vehicleYearString;
    private int vehicleMakeId;
    private int vehicleModelId;

    private ArrayList<PartsListItems> arrayList;
    private PartsListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.parts_list_fragment, container, false);
        mPartsListView = mBaseView.findViewById(R.id.parts_list_view);

        arrayList = new ArrayList<>();
        adapter = new PartsListAdapter(getActivity(), arrayList);
        mPartsListView.setAdapter(adapter);
        Bundle bundle = getArguments();
        if (bundle != null) {
            vehicleYearString = bundle.getString("vehicle_year");
            vehicleMakeId = bundle.getInt("vehicle_make");
            vehicleModelId = bundle.getInt("vehicle_model");
            Log.e("TAG", "" + vehicleYearString);
            Log.e("TAG", "" + vehicleMakeId);
            Log.e("TAG", "" + vehicleModelId);
        }
        getPartsList(vehicleYearString, vehicleMakeId, vehicleModelId);
        return mBaseView;
    }

    @Override
    public void onClick(View v) {

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
                                Log.e("working ", " " + jsonArray.getJSONObject(i));
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                PartsListItems partsListItems = new PartsListItems();
                                partsListItems.setPartId(jsonObject.getInt("id"));
                                partsListItems.setPartName(jsonObject.getString("description"));
                                partsListItems.setPartPrice(jsonObject.getString("price"));
                                partsListItems.setPartImage(jsonObject.getString("image"));
                                partsListItems.setModelYear(jsonObject.getString("start_year"));
                                partsListItems.setProvidersContactNumber(jsonObject.getString("contact"));

                                JSONObject makeJSONObject = jsonObject.getJSONObject("make");
                                partsListItems.setPartMake(makeJSONObject.getString("name"));

                                JSONObject modelJSONObject = jsonObject.getJSONObject("model");
                                partsListItems.setPartMake(modelJSONObject.getString("name"));
                                arrayList.add(partsListItems);
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }

    }

    private void getPartsList(String vehicleYear, int vehicleMake, int vehicleModel) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("GET", String.format("%sparts?year=%s&make=%s&model=%s", AppGlobals.BASE_URL,
                vehicleYear, vehicleMake, vehicleModel));
        request.send();
        Helpers.showProgressDialog(getActivity(), getString(R.string.parts_list));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
                    adapter.callAction();
                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


}
