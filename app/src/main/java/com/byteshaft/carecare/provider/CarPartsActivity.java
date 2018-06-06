package com.byteshaft.carecare.provider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.CarParts;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class CarPartsActivity extends AppCompatActivity {

    private ListView listView;
    private List<CarParts> carPartsList;
    private PartsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_parts);
        setTitle("Car Parts");
        listView = findViewById(R.id.car_parts_list);
        carPartsList = new ArrayList<>();
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
                                try {
                                    JSONObject object = new JSONObject(request.getResponseText());
                                    JSONArray jsonArray = object.getJSONArray("results");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        CarParts carParts = new CarParts();
                                        carParts.setDescription(jsonObject.getString("description"));
                                        carParts.setMake(jsonObject.getString("make"));
                                        carParts.setModel(jsonObject.getString("model"));
                                        carParts.setPrice(jsonObject.getString("price"));
                                        carParts.setImage(jsonObject.getString("image"));
                                        carPartsList.add(carParts);
                                    }

                                    adapter = new PartsAdapter(getApplicationContext(), carPartsList);
                                    listView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

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

    private class PartsAdapter extends BaseAdapter {
        private ViewHolder viewHolder;
        private Context context;
        private List<CarParts> carParts;

        public PartsAdapter(Context context, List<CarParts> carParts) {
            this.context = context;
            this.carParts = carParts;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater()
                        .inflate(R.layout.delegate_car_part, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.make = convertView.findViewById(R.id.car_make);
                viewHolder.model = convertView.findViewById(R.id.car_model);
                viewHolder.price = convertView.findViewById(R.id.part_price);
                viewHolder.description = convertView.findViewById(R.id.part_description);
                viewHolder.partImage = convertView.findViewById(R.id.car_part_image);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            CarParts items = carParts.get(position);
            viewHolder.make.setText("Make: " + items.getMake());
            viewHolder.model.setText("Model: " + items.getModel());
            viewHolder.description.setText("Part Description: " + items.getDescription());
            viewHolder.price.setText("Price: " + items.getPrice());
            Log.wtf("ok image ", items.getImage());

            if (!items.getImage().isEmpty()) {
                Picasso.with(
                        getApplicationContext()).load((items.getImage()))
                        .resize(300, 300)
                        .centerCrop()
                        .into(viewHolder.partImage);
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return carParts.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        private class ViewHolder {
            private TextView make;
            private TextView model;
            private TextView price;
            private TextView description;
            private ImageView partImage;
        }
    }
}
