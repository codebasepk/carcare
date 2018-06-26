package com.byteshaft.carecare.provider;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.byteshaft.carecare.Adapters.AutoMechanicAdapter;
import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.AutoMechanicItems;
import com.byteshaft.carecare.gettersetter.AutoMechanicSubItem;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class AddMechanicService extends AppCompatActivity implements
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    private HttpRequest request;
    private ListView listView;
    private ArrayList<AutoMechanicItems> arrayList;
    private AutoMechanicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mechanic_service);
        setTitle("Auto Mechanic");
        listView = findViewById(R.id.services_list_view);
        getAutoMechanicsServicesList();
    }


    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        Helpers.dismissProgressDialog();
        switch (readyState) {
            case HttpRequest.ERROR_CONNECTION_TIMED_OUT:
                Helpers.showSnackBar(listView, getString(R.string.connection_time_out));
                break;
            case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                Helpers.showSnackBar(listView, exception.getLocalizedMessage());
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
                        arrayList = new ArrayList<>();
                        adapter = new AutoMechanicAdapter(AddMechanicService.this, arrayList);
                        listView.setAdapter(adapter);
                        try {
                            JSONArray jsonArray = new JSONArray(request.getResponseText());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                AutoMechanicItems items = new AutoMechanicItems();
                                items.setCategoryName(jsonObject.getString("name"));
                                JSONArray serviceSubItemsJsonArray = jsonObject.getJSONArray("sub_services");
                                ArrayList<AutoMechanicSubItem> array = new ArrayList<>();
                                for (int j = 0; j < serviceSubItemsJsonArray.length(); j++) {
                                    JSONObject serviceSubItemsJsonObject = serviceSubItemsJsonArray.getJSONObject(j);
                                    System.out.println("Test " + serviceSubItemsJsonObject);
                                    AutoMechanicSubItem autoMechanicSubItemsList = new AutoMechanicSubItem();
                                    autoMechanicSubItemsList.setServiceId(serviceSubItemsJsonObject.getInt("id"));
                                    autoMechanicSubItemsList.setServiceName(serviceSubItemsJsonObject.getString("name"));
                                    Log.i("TAG", " adding " + serviceSubItemsJsonObject.getString("name"));
                                    array.add(autoMechanicSubItemsList);
                                }
                                items.setSubItemsArrayList(array);
                                arrayList.add(items);
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }

    }

    private void getAutoMechanicsServicesList() {
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("GET", String.format("%smechanic-services", AppGlobals.BASE_URL));
        request.send();
        Helpers.showProgressDialog(AddMechanicService.this, "Fetching Services...");
    }
}
