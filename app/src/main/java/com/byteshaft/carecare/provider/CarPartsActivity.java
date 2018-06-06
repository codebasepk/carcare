package com.byteshaft.carecare.provider;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import java.net.HttpURLConnection;

public class CarPartsActivity extends AppCompatActivity {

    private ListView carPartsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_parts);
        carPartsList = findViewById(R.id.car_parts_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPartsList();
    }

    private void getPartsList() {
        HttpRequest request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                Log.wtf("Res", request.getResponseText());
                        }
                }
            }
        });
        request.open("GET", String.format("%sprovider/parts", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_carparts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            startActivity(new Intent(CarPartsActivity.this, AddCarPart.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
