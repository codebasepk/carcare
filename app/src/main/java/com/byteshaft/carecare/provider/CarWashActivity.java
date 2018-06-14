package com.byteshaft.carecare.provider;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.AutoMechanicService;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class CarWashActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_wash);
        getAllCarWashServices();
    }

    private void getAllCarWashServices() {
        HttpRequest request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        Log.wtf("On DOne ", request.getResponseText());
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                try {
                                    JSONObject object = new JSONObject(request.getResponseText());
                                    JSONArray jsonArray = object.getJSONArray("results");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        AutoMechanicService mechanicServices = new AutoMechanicService();
                                        JSONObject serviceObject = jsonObject.getJSONObject("service");
                                        mechanicServices.setId(jsonObject.getInt("id"));
                                        mechanicServices.setName(serviceObject.getString("name"));
                                        mechanicServices.setPrice(jsonObject.getString("price"));
//                                        arrayList.add(mechanicServices);
                                    }

//                                    adapter = new ServiceAdapter(getApplicationContext(), arrayList);
//                                    listView.setAdapter(adapter);
//                                    adapter.notifyDataSetChanged();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        request.open("GET", String.format("%sservice-station/services", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send();
    }
}
